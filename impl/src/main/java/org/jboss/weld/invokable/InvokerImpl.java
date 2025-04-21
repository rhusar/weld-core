package org.jboss.weld.invokable;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.build.compatible.spi.InvokerInfo;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.enterprise.invoke.Invoker;
import jakarta.inject.Named;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InvokerImpl<T, R> implements Invoker<T, R>, InvokerInfo {

    // method handle for the bean method, including all applied transformers
    private final MethodHandle beanMethodHandle;
    private final MethodHandle invocationWrapper;
    private final Method method;
    // null if no lookup is to be done
    private final Annotation[] instanceLookupQualifiers;
    private final Annotation[][] argLookupQualifiers;
    private final boolean hasInstanceTransformer;
    private final Class<T> beanClass;
    private final InvokerCleanupActions cleanupActions;

    // this variant is only used for invocation wrapper and assumes fully-initialized state
    private InvokerImpl(MethodHandle beanMethodHandle, Annotation[] instanceLookupQualifiers, Annotation[][] argLookupQualifiers,
                        boolean hasInstanceTransformer, InvokerCleanupActions cleanupActions,
                        Class<T> beanClass, Method method) {
        this.beanMethodHandle = beanMethodHandle;
        this.instanceLookupQualifiers = instanceLookupQualifiers;
        this.argLookupQualifiers = argLookupQualifiers;
        this.hasInstanceTransformer = hasInstanceTransformer;
        this.cleanupActions = cleanupActions;
        this.beanClass = beanClass;
        this.method = method;
        this.invocationWrapper = null;
    }

    InvokerImpl(AbstractInvokerBuilder<T, ?> builder) {
        // needs to be initialized first, as can be used when creating method handles
        this.cleanupActions = new InvokerCleanupActions();
        this.beanClass = builder.beanClass;
        this.method = builder.method;
        if (builder.instanceLookup) {
            this.instanceLookupQualifiers = extractInstanceQualifiers(beanClass, builder.beanManager);
        } else {
            this.instanceLookupQualifiers = null;
        }
        this.argLookupQualifiers = new Annotation[builder.argLookup.length][];
        for (int i = 0; i < builder.argLookup.length; i++) {
            // positions that remain null are those for which we don't perform lookup
            if (builder.argLookup[i]) {
                this.argLookupQualifiers[i] = extractParamQualifiers(method.getParameters()[i], builder.beanManager);
            }
        }

        // handles transformers
        this.hasInstanceTransformer = builder.instanceTransformer != null;
        MethodHandle instanceTransformer = !hasInstanceTransformer ? null : createMethodHandleFromTransformer(builder.instanceTransformer, builder.beanClass);
        MethodHandle[] argTransformers = new MethodHandle[builder.argTransformers.length];
        for (int i = 0; i < builder.argTransformers.length; i++) {
            if (builder.argTransformers[i] != null) {
                argTransformers[i] = createMethodHandleFromTransformer(builder.argTransformers[i], builder.method.getParameters()[i].getType());
            }
        }
        MethodHandle returnValueTransformer = builder.returnValueTransformer == null ? null : createMethodHandleFromTransformer(builder.returnValueTransformer, builder.method.getReturnType());
        MethodHandle exceptionTransformer = builder.exceptionTransformer == null ? null : createMethodHandleFromTransformer(builder.exceptionTransformer, Throwable.class);
        // resolve invocation wrapper and save separately
        this.invocationWrapper = builder.invocationWrapper == null ? null : createMethodHandleFromTransformer(builder.invocationWrapper, beanClass);

        MethodHandle finalMethodHandle = getMethodHandle(builder.method, builder.method.getParameterTypes());

        // handle instance transformer, instance is the first arg
        if (!Modifier.isStatic(method.getModifiers()) && instanceTransformer != null) {
            finalMethodHandle = MethodHandles.filterArguments(finalMethodHandle, 0, instanceTransformer);
        }

        // handle method argument transformations - passed null values are treated as an identity function
        finalMethodHandle = MethodHandles.filterArguments(finalMethodHandle, Modifier.isStatic(method.getModifiers()) ? 0 : 1, argTransformers);

        // handle return type transformer
        if (returnValueTransformer != null) {
            finalMethodHandle = MethodHandles.filterReturnValue(finalMethodHandle, returnValueTransformer);
        }

        // handle exception transformers
        if (exceptionTransformer != null) {
            finalMethodHandle = MethodHandles.catchException(finalMethodHandle, Throwable.class, exceptionTransformer);
        }

        // assign the bean method handle after applying all transformers
        this.beanMethodHandle = finalMethodHandle;
    }


    private MethodHandle createMethodHandleFromTransformer(TransformerMetadata transformer, Class<?> transformationArgType) {
        List<Method> matchingMethods = new ArrayList<>();
        List<Class<?>> methodHandleArgs = new ArrayList<>();
        for (Method m : transformer.getTargetClass().getDeclaredMethods()) {
            // method match is only based on class and name, no match is a problem and so are multiple
            if (m.getName().equals(transformer.getMethodName())) {
                matchingMethods.add(m);
            }
        }
        if (matchingMethods.size() != 1) {
            // TODO better exception
            throw new DeploymentException("There needs to be exactly one matching method for transformer: "
                    + transformer + " Methods found: " + matchingMethods);
        }
        Method method = matchingMethods.get(0);
        // validate method
        processMethodCandidate(method, transformer, transformationArgType, methodHandleArgs);
        MethodHandle result = getMethodHandle(method, methodHandleArgs.toArray(new Class<?>[methodHandleArgs.size()]));

        // if there was an extra Consumer<Runnable> param (param count == 2), we now want to insert that argument
        if (method.getParameterCount() == 2) {
            result = MethodHandles.insertArguments(result, result.type().parameterCount() - 1, this.cleanupActions);
        }

        // for input transformers, we might need to change return type to whatever the original method expects
        // for output transformers, we might need to change their input params
        // this enables transformers to operate on subclasses (input tf) or superclasses (output tf)
        if (transformer.isInputTransformer()
                && !result.type().returnType().equals(transformationArgType)) {
            result = result.asType(result.type().changeReturnType(transformationArgType));
        } else if (transformer.isOutputTransformer()
                && result.type().parameterCount() > 0
                && !result.type().parameterType(0).equals(transformationArgType)) {
            result = result.asType(result.type().changeParameterType(0, transformationArgType));
        }
        if (TransformerType.EXCEPTION.equals(transformer.getType())) {
            // if assignable, then just alter return type
            if (this.method.getReturnType().isAssignableFrom(result.type().returnType())) {
                // exception handlers can return a subtype of original class
                //so long as it is assignable, just cast it to the required/expected type
                result = result.asType(result.type().changeReturnType(this.method.getReturnType()));
            } else {
                // if not assignable, then we need to apply a return value transformer which hides the value in an exception
                try {
                    MethodHandle hideReturnValue = getMethodHandle(ValueCarryingException.class.getDeclaredMethod("hideReturnValue", Object.class), Object.class);
                    // cast return value of the custom method we use to whatever the original method expects - we'll never use it anyway
                    hideReturnValue = hideReturnValue.asType(hideReturnValue.type().changeReturnType(this.method.getReturnType()));
                    // adapt the parameter type as well, we don't really care what it is, we just store it and throw it
                    hideReturnValue = hideReturnValue.asType(hideReturnValue.type().changeParameterType(0, result.type().returnType()));
                    result = MethodHandles.filterReturnValue(result, hideReturnValue);
                } catch (NoSuchMethodException e) {
                    // should never happen
                    throw new IllegalStateException("Unable to locate Weld internal helper method");
                }
            }
        }
        return result;
    }

    private void processMethodCandidate(Method m, TransformerMetadata transformer, Class<?> transformationArgType, List<Class<?>> methodHandleArgs) {
        // all transformers have to be public to ensure accessibility
        if (!Modifier.isPublic(m.getModifiers())) {
            // TODO better exception
            throw new DeploymentException("All invocation transformers need to be public - " + transformer);
        }
        int paramCount = m.getParameterCount();
        if (transformer.isInputTransformer()) {
            // input transformers need to validate assignability of their return type versus original arg type
            if (!transformationArgType.isAssignableFrom(m.getReturnType())) {
                // TODO better exception
                throw new DeploymentException("Input transformer " + transformer + " has a return value that is not assignable to expected class: " + transformationArgType);
            }
            // static method is no-param, otherwise its 1-2 with second being Consumer<Runnable>
            if (!Modifier.isStatic(m.getModifiers()) ) {
                if (paramCount != 0) {
                    // TODO better exception
                    throw new DeploymentException("Non-static input transformers are expected to have zero input parameters! Transformer: " + transformer);
                }
            } else {
                if (paramCount > 2) {
                    // TODO better exception
                    throw new DeploymentException("Static input transformers can only have one or two parameters. " + transformer);
                }
                methodHandleArgs.add(m.getParameters()[0].getType());
                if (paramCount == 2) {
                    // we do not validate type param of Consumer, i.e. if it's exactly Consumer<Runnable>
                    if (!Consumer.class.equals(m.getParameters()[1].getType())) {
                        // TODO better exception
                        throw new DeploymentException("Static input transformers with two parameters can only have Consumer<Runnable> as their second parameter! " + transformer);
                    }
                    methodHandleArgs.add(Consumer.class);
                }
            }
        } else if (transformer.isOutputTransformer()) {
            // output transformers need to validate assignability of their INPUT
            // this also means static methods need no validation in this regard
            if (!Modifier.isStatic(m.getModifiers()) ) {
                if (paramCount != 0) {
                    // TODO better exception
                    throw new DeploymentException("Non-static output transformers are expected to have zero input parameters! Transformer: " + transformer);
                }
            } else {
                if (paramCount != 1) {
                    // TODO better exception
                    throw new DeploymentException("Static output transformers are expected to have one input parameter! Transformer: " + transformer);
                }
                if (!m.getParameters()[0].getType().isAssignableFrom(transformationArgType)) {
                    // TODO better exception
                    throw new DeploymentException("Output transformer " + transformer + " parameter is not assignable to the expected type " + transformationArgType);
                }
                methodHandleArgs.add(m.getParameters()[0].getType());
            }
        } else {
            // transformer is a wrapper which has exactly three arguments
            if (m.getParameterCount() == 3
                                    && m.getParameters()[0].getType().equals(transformationArgType)
                                    && m.getParameters()[1].getType().equals(Object[].class)
                                    && m.getParameters()[2].getType().equals(Invoker.class)) {
                methodHandleArgs.add(transformationArgType);
                methodHandleArgs.add(Object[].class);
                methodHandleArgs.add(Invoker.class);
            } else {
                // TODO better exception
                throw new DeploymentException("Invocation wrapper transformer has unexpected parameters " + transformer + "\nExpected param types are: " + transformationArgType + ", Object[], Invoker.class");
            }
        }
    }

    private MethodHandle getMethodHandle(Method method, Class<?>... methodArgs) {
        MethodHandles.Lookup lookup;
        // use public lookup if possible
        if (Modifier.isPublic(method.getModifiers())) {
            lookup = MethodHandles.publicLookup();
        } else {
            lookup = MethodHandles.lookup();
        }
        MethodType methodType = MethodType.methodType(method.getReturnType(), methodArgs);

        // different method handle lookup for static methods
        try {
            if (Modifier.isStatic(method.getModifiers())) {
                return lookup.findStatic(method.getDeclaringClass(), method.getName(), methodType);
            } else {
                return lookup.findVirtual(method.getDeclaringClass(), method.getName(), methodType);
            }
            // TODO proper exception handling
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public R invoke(T instance, Object[] arguments) {
        // if there is an invocation wrapper, just invoke the wrapper immediately
        if (this.invocationWrapper != null) {
            try {
                return (R) invocationWrapper.invoke(instance, arguments, new InvokerImpl<>(beanMethodHandle, instanceLookupQualifiers,
                        argLookupQualifiers, hasInstanceTransformer, cleanupActions, beanClass, method));
            } catch (Throwable e) {
                // invocation wrapper or the method itself threw an exception, we just rethrow
                throw new RuntimeException(e);
            }
        }

        // TODO do we want to verify that the T instance is the exact class of the bean?
        if (arguments.length != method.getParameterCount()) {
            // TODO better exception
            throw new IllegalArgumentException("Wrong number of args for invoker! Expected: " + method.getParameterCount() + " Provided: " + arguments.length);
        }
        T beanInstance = instance;
        Instance<Object> cdiLookup = CDI.current().getBeanManager().createInstance();
        if (instanceLookupQualifiers != null) {
            // instance lookup set, ignore the parameter we got and lookup the instance
            // standard CDI resolution errors can occur
            beanInstance = getInstance(cdiLookup, beanClass, instanceLookupQualifiers);
        } else {
            // arg should not be null unless the method is (a) static, (b) has instance transformer, (c) has instance lookup
            if (beanInstance == null && !Modifier.isStatic(method.getModifiers()) && !hasInstanceTransformer) {
                // TODO better exception
                throw new IllegalArgumentException("Invoker target instance cannot be null unless it's static method or set for CDI lookup!");
            }
        }

        List<Object> methodArgs = new ArrayList<>(argLookupQualifiers.length + 1);
        for (int i = 0; i < argLookupQualifiers.length; i++) {
            if (argLookupQualifiers[i] != null) {
                methodArgs.add(i, getInstance(cdiLookup, beanMethodHandle.type().parameterType(Modifier.isStatic(method.getModifiers()) ? i : i + 1), argLookupQualifiers[i]));
            } else {
                methodArgs.add(i, arguments[i]);
            }
        }

        // for non-static methods, first arg is the instance to invoke it on
        if (!Modifier.isStatic(method.getModifiers())) {
            methodArgs.add(0, beanInstance);
        }

        try {
            // TODO the need to cast is weird, shouldn't this method just return Object, assuming user knows?
            return (R) beanMethodHandle.invokeWithArguments(methodArgs);
        } catch (ValueCarryingException e) {
            // there was an exception transformer which we wrapped, just return what was inside
            return (R) e.getMethodReturnValue();
        } catch (Throwable e) {
            // TODO at this point there might have been exception transformer invoked as well, guess we just rethrow?
            // we just rethrow the original exception
            throw new RuntimeException(e);
        } finally {
            // invoke cleanup tasks running all registered consumers as well as destroying dependent beans
            this.cleanupActions.cleanup();
        }
    }

    private <TYPE> TYPE getInstance(Instance<Object> lookup, Class<TYPE> classToLookup, Annotation... qualifiers) {
        Instance.Handle<TYPE> handle = lookup.select(classToLookup, qualifiers).getHandle();
        this.cleanupActions.addInstanceHandle(handle);
        return handle.get();
    }

    private Annotation[] extractInstanceQualifiers(Class<?> beanClass, BeanManager bm) {
        return extractQualifiers(beanClass.getAnnotations(), bm);
    }

    private Annotation[] extractParamQualifiers(Parameter parameter, BeanManager bm) {
        return extractQualifiers(parameter.getAnnotations(), bm);
    }

    private Annotation[] extractQualifiers(Annotation[] annotations, BeanManager bm) {
        List<Annotation> qualifiers = new ArrayList<>();
        for (Annotation a : annotations) {
            if (bm.isQualifier(a.annotationType())) {
                qualifiers.add(a);
            }
        }
        // add default when there are no qualifiers or just @Named
        if (qualifiers.isEmpty() || (qualifiers.size() == 1 && qualifiers.get(0).annotationType().equals(Named.class))) {
            qualifiers.add(Default.Literal.INSTANCE);
        }
        return qualifiers.toArray(new Annotation[]{});
    }

    private static class InvokerCleanupActions implements Consumer<Runnable> {
        private final List<Runnable> cleanupTasks = new ArrayList<>();
        private final List<Instance.Handle<?>> dependentInstances = new ArrayList<>();

        @Override
        public void accept(Runnable runnable) {
            cleanupTasks.add(runnable);
        }

        public void addInstanceHandle(Instance.Handle<?> handle) {
            if (handle.getBean().getScope().equals(Dependent.class)) {
                dependentInstances.add(handle);
            }
        }

        public void cleanup() {
            // run all registered tasks
            for (Runnable r : cleanupTasks) {
                r.run();
            }
            cleanupTasks.clear();

            // destroy dependent beans we created
            for (Instance.Handle<?> handle : dependentInstances) {
                handle.destroy();
            }
            dependentInstances.clear();
        }
    }
}
