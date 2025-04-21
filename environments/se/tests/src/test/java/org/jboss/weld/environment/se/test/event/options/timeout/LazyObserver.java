/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.environment.se.test.event.options.timeout;

import java.util.concurrent.CountDownLatch;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.ObservesAsync;

/**
 * All observers in this class will complete AFTER timeout
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@Dependent
public class LazyObserver {

    public void observeAndDrinkCoffee(@ObservesAsync @Priority(2) CountDownLatch latch) throws InterruptedException {
        // take coffee break
        Thread.sleep(3000);
        NotificationTimeoutTest.SUCCESSION_OF_EVENTS.add("Coffee");
        latch.countDown();
    }
}
