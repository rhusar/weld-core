package org.jboss.weld.tests.producer.method.broken.invalidBeanType;

import jakarta.enterprise.inject.Produces;

/**
* @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
*/
public class MultiDimensionalWildcardTypeArrayProducer {
    @Produces
    public Foo<?>[][][] produceWildcardFooArray() {
        return null;
    }
}
