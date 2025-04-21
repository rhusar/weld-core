package org.jboss.weld.tests.event.observer.validation;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class StringListArrayObserverBean {

    private List<String>[] data;
    private boolean receivedUpdate;

    public void listenForEvent(@Observes final ArrayList<String>[] data) {
        this.data = data;
        this.receivedUpdate = true;
    }

    public List<String>[] getData() {
        return data;
    }

    public boolean isReceivedUpdate() {
        return receivedUpdate;
    }

    void reset() {
        this.data = null;
        this.receivedUpdate = false;
    }

}
