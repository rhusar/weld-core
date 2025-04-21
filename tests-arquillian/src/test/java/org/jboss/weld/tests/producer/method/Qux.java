package org.jboss.weld.tests.producer.method;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class Qux {

    private final String name;

    public Qux(String name) {
        this.name = name;
    }

    public Qux() {
        this("qux");
    }

    public String getBar() {
        return name;
    }

}
