package org.jboss.weld.tests.proxy.superclass.instance;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Yann Diorcet
 */
@ApplicationScoped
public class Bar extends Foo {

    public Bar() {
        super("Bar");
    }
}
