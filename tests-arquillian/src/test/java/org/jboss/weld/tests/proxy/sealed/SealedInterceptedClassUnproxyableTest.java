package org.jboss.weld.tests.proxy.sealed;

import jakarta.enterprise.inject.spi.DeploymentException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SealedInterceptedClassUnproxyableTest {

    @ShouldThrowException(DeploymentException.class)
    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(SealedDependentBeanWithNoProxyTest.class))
                .addClasses(MyDependentIntercepted.class, MyDependentInterceptedSubclass.class, MyBinding.class,
                        MyInterceptor.class, InjectingBean2.class);
    }

    @Test
    public void testSealedDependentScopedBeanCannotBeIntercepted() {
        // dependent bean would work but adding an interceptor adds proxyability requirement
    }
}
