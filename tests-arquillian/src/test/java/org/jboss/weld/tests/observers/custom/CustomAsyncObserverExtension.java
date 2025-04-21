/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat, Inc., and individual contributors
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

package org.jboss.weld.tests.observers.custom;

import java.util.concurrent.CountDownLatch;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessObserverMethod;

public class CustomAsyncObserverExtension implements Extension {

    public static boolean NOTIFIED = false;

    private AnnotatedMethod<?> om = null;
    public void monitorOM(@Observes ProcessObserverMethod<CountDownLatch, FooBean> event) {
        if (om == null) {
            this.om = event.getAnnotatedMethod();
            event.veto();
        }
    }

    public void registerObservers(@Observes AfterBeanDiscovery event) {
        if (om == null) {
            throw new IllegalStateException("Did not find observer method to read from!");
        } else {
            event.<CountDownLatch>addObserverMethod().read(om).notifyWith(context -> {
                NOTIFIED = true;
                context.getEvent().countDown();
            });
        }
    }
}
