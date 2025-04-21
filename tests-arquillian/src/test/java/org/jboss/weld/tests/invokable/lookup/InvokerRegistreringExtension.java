package org.jboss.weld.tests.invokable.lookup;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessManagedBean;
import jakarta.enterprise.invoke.Invoker;
import org.junit.Assert;

import java.util.Collection;

public class InvokerRegistreringExtension implements Extension {

    private Invoker<InvokableBean, ?> instanceLookupInvoker;
    private Invoker<InvokableBean, ?> unsatisfiedLookupInvoker;
    private Invoker<InvokableBean, ?> ambiguousLookupInvoker;
    private Invoker<InvokableBean, ?> correctLookupInvoker;
    private Invoker<InvokableBean, ?> lookupWithRegisteredQualifier;

    public Invoker<InvokableBean, ?> getInstanceLookupInvoker() {
        return instanceLookupInvoker;
    }

    public Invoker<InvokableBean, ?> getUnsatisfiedLookupInvoker() {
        return unsatisfiedLookupInvoker;
    }

    public Invoker<InvokableBean, ?> getAmbiguousLookupInvoker() {
        return ambiguousLookupInvoker;
    }

    public Invoker<InvokableBean, ?> getCorrectLookupInvoker() {
        return correctLookupInvoker;
    }

    public Invoker<InvokableBean, ?> getLookupWithRegisteredQualifier() {
        return lookupWithRegisteredQualifier;
    }

    public void createInvokers(@Observes ProcessManagedBean<InvokableBean> pmb) {
        Collection<AnnotatedMethod<? super InvokableBean>> invokableMethods = pmb.getInvokableMethods();
        Assert.assertEquals(5, invokableMethods.size());
        for (AnnotatedMethod<? super InvokableBean> invokableMethod : invokableMethods) {
            if (invokableMethod.getJavaMember().getName().contains("instanceLookup")) {
                instanceLookupInvoker = pmb.createInvoker(invokableMethod).setInstanceLookup().build();
            } else if (invokableMethod.getJavaMember().getName().contains("unsatisfiedLookup")) {
                unsatisfiedLookupInvoker = pmb.createInvoker(invokableMethod).setArgumentLookup(0).build();
            } else if (invokableMethod.getJavaMember().getName().contains("ambiguousLookup")) {
                ambiguousLookupInvoker = pmb.createInvoker(invokableMethod).setArgumentLookup(0).build();
            } else if (invokableMethod.getJavaMember().getName().contains("lookupWithRegisteredQualifier")) {
                lookupWithRegisteredQualifier = pmb.createInvoker(invokableMethod).setArgumentLookup(0).build();
            } else {
                correctLookupInvoker = pmb.createInvoker(invokableMethod).setArgumentLookup(0).setArgumentLookup(1).build();
            }
        }
    }

    public void registerQualifier(@Observes BeforeBeanDiscovery bbd) {
        bbd.addQualifier(ToBeQualifier.class);
    }
}
