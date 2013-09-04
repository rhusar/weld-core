/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.injection.producer;

import static org.jboss.weld.logging.messages.BeanMessage.INJECTED_FIELD_CANNOT_BE_PRODUCER;
import static org.jboss.weld.logging.messages.BeanMessage.PRODUCER_FIELD_ON_SESSION_BEAN_MUST_BE_STATIC;
import static org.jboss.weld.logging.messages.BeanMessage.PRODUCER_METHOD_CANNOT_HAVE_A_WILDCARD_RETURN_TYPE;
import static org.jboss.weld.logging.messages.BeanMessage.PRODUCER_METHOD_WITH_TYPE_VARIABLE_RETURN_TYPE_MUST_BE_DEPENDENT;
import static org.jboss.weld.logging.messages.UtilMessage.ACCESS_ERROR_ON_FIELD;
import static org.jboss.weld.util.reflection.Reflections.cast;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;
import javax.inject.Inject;

import org.jboss.weld.annotated.enhanced.EnhancedAnnotatedField;
import org.jboss.weld.bean.DisposalMethod;
import org.jboss.weld.bean.SessionBean;
import org.jboss.weld.exceptions.DefinitionException;
import org.jboss.weld.exceptions.WeldException;
import org.jboss.weld.interceptor.util.proxy.TargetInstanceProxy;
import org.jboss.weld.logging.messages.BeanMessage;
import org.jboss.weld.security.GetAccessibleCopyOfMember;
import org.jboss.weld.util.reflection.Reflections;

/**
 * {@link Producer} implementation for producer fields.
 *
 * @author Jozef Hartinger
 *
 */
public abstract class ProducerFieldProducer<X, T> extends AbstractMemberProducer<X, T> {

    private final Field accessibleField;

    public ProducerFieldProducer(EnhancedAnnotatedField<T, ? super X> enhancedField, DisposalMethod<?, ?> disposalMethod) {
        super(enhancedField, disposalMethod);
        this.accessibleField = AccessController.doPrivileged(new GetAccessibleCopyOfMember<Field>(enhancedField.getJavaMember()));
        checkProducerField(enhancedField);
    }

    protected void checkProducerField(EnhancedAnnotatedField<T, ? super X> enhancedField) {
        if (getDeclaringBean() instanceof SessionBean<?> && !enhancedField.isStatic()) {
            throw new DefinitionException(PRODUCER_FIELD_ON_SESSION_BEAN_MUST_BE_STATIC, enhancedField, enhancedField.getDeclaringType());
        }
        if (enhancedField.isAnnotationPresent(Inject.class)) {
            if (getDeclaringBean() != null) {
                throw new DefinitionException(INJECTED_FIELD_CANNOT_BE_PRODUCER, enhancedField, getDeclaringBean());
            } else {
                throw new DefinitionException(INJECTED_FIELD_CANNOT_BE_PRODUCER, enhancedField, enhancedField.getDeclaringType());
            }
        }
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public abstract AnnotatedField<? super X> getAnnotated();

    @Override
    public T produce(Object receiver, CreationalContext<T> creationalContext) {
        // unwrap if we have a proxy
        if (receiver instanceof TargetInstanceProxy) {
            receiver = Reflections.<TargetInstanceProxy<T>> cast(receiver).getTargetInstance();
        }
        try {
            return cast(accessibleField.get(receiver));
        } catch (IllegalAccessException e) {
            throw new WeldException(ACCESS_ERROR_ON_FIELD, e, accessibleField.getName(), accessibleField.getDeclaringClass());
        }
    }

    @Override
    public String toString() {
        return getAnnotated().toString();
    }

    protected BeanMessage producerCannotHaveWildcardBeanType() {
        return PRODUCER_METHOD_CANNOT_HAVE_A_WILDCARD_RETURN_TYPE;
    }

    protected BeanMessage producerWithTypeVariableBeanTypeMustBeDependent() {
        return PRODUCER_METHOD_WITH_TYPE_VARIABLE_RETURN_TYPE_MUST_BE_DEPENDENT;
    }
}