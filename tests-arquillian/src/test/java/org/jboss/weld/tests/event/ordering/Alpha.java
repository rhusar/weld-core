package org.jboss.weld.tests.event.ordering;

import jakarta.annotation.Priority;
import jakarta.enterprise.event.Observes;

public class Alpha {

    public void observeEvent(@Observes @Priority(1) EventPayload payload) {
        payload.record(Alpha.class.getName());
    }

}
