package org.jboss.weld.tests.injectionPoint;

import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import java.io.Serializable;

public class Farm implements Serializable {

    @Inject
    private InjectionPoint injectionPoint;

    public void ping() {
    }

    public InjectionPoint getInjectionPoint() {
        return injectionPoint;
    }

}
