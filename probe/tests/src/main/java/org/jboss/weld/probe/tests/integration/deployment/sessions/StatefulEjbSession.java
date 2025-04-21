package org.jboss.weld.probe.tests.integration.deployment.sessions;

import jakarta.annotation.Priority;
import jakarta.ejb.Stateful;
import jakarta.enterprise.inject.Alternative;

@Alternative
@Priority(StatefulEjbSession.PRIORITY)
@Stateful
public class StatefulEjbSession implements DecoratedInterface {

    public static final int PRIORITY = 2500;

    @Override
    public String testMethod() {
        return "Hello from "+StatefulEjbSession.class.getSimpleName();
    }
}
