/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.contexts.weld1159;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;

@SessionScoped
public class SessionScopedFoo extends AbstractBean {

    private static final long serialVersionUID = -5049309334561158325L;

    @Inject
    public void init(ApplicationScopedFoo bean) throws Exception {
        latch.countDown();
        latch.await(5, TimeUnit.SECONDS); // synchronize with the other thread
        bean.ping();
    }
}
