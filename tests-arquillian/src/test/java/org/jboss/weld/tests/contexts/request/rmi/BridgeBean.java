package org.jboss.weld.tests.contexts.request.rmi;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class BridgeBean implements Bridge {
    @Inject
    @My
    private Config config;

    public String doSomething() {
        System.out.println("Bridge.doSomething.");
        return config.toString();
    }
}
