package org.jboss.weld.tests.event.ordering;

import jakarta.enterprise.event.Observes;

public class NoPriority {

    public void observeEvent(@Observes EventPayload payload) {
        payload.record(NoPriority.class.getName());
    }

}
