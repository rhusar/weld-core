/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.producer.disposer.decorated;

import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.inject.Inject;

import org.jboss.weld.test.util.ActionSequence;

/**
 * Just having the decorator will force Weld to create certain bytecode.
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@Decorator
@Priority(100)
public class WorkerDecorator implements Worker {

    @Inject
    @Delegate
    Worker worker;

    @Override
    public void doStuff() {
        worker.doStuff();
        ActionSequence.addAction(WorkerDecorator.class.getName());
    }

}
