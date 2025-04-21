/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.tests.bridge.methods;

import static org.junit.Assert.assertEquals;

import jakarta.enterprise.event.Event;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.test.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Martin Kouba
 * @see WELD-1577
 */
@RunWith(Arquillian.class)
public class ObserverBridgeMethodTest {

    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(ObserverBridgeMethodTest.class))
                .addClasses(Foo.class, Bar.class, CharlieObserver.class, BasicObserver.class);
    }

    @Test
    public void testObserverBridgeMethodIsIgnored(Event<Foo> event) {
        CharlieObserver.OBSERVATIONS.set(0);
        event.fire(new Bar());
        assertEquals(1, CharlieObserver.OBSERVATIONS.get());
    }

}
