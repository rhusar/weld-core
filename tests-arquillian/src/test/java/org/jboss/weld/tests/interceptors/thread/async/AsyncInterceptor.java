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
package org.jboss.weld.tests.interceptors.thread.async;

import java.util.concurrent.Future;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.spi.DefinitionException;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.weld.tests.interceptors.thread.ThreadPool;

@Async
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 100)
public class AsyncInterceptor {

    @Inject
    private ThreadPool pool;

    @AroundInvoke
    public Object intercept(final InvocationContext ctx) throws Exception {
        final Class<?> returnType = ctx.getMethod().getReturnType();
        if (returnType == void.class) {
            pool.submit(ctx);
            return null;
        } else if (returnType == Future.class) {
            return pool.submit(() -> AsyncResult.unwrap(ctx.proceed()));
        } else {
            throw new DefinitionException("Invalid return type " + returnType);
        }
    }
}
