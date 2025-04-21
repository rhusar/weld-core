/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.interceptors.resource;

import jakarta.annotation.Resource;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

public class BeanManagerInjectingInterceptor {

    private BeanManager manager;

    @Resource(mappedName = "java:comp/BeanManager")
    public void setManager(BeanManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException("Resource injection performed more than once");
        }
        this.manager = manager;
    }

    @Inject
    public void init() {
        if (manager == null) {
            throw new IllegalStateException("Resource injection not performed");
        }
    }

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        if (manager == null) {
            throw new IllegalStateException();
        }
        if (ctx.getParameters().length == 1 && ctx.getParameters()[0] == null) {
            ctx.setParameters(new Object[] { manager });
        }
        return ctx.proceed();
    }
}
