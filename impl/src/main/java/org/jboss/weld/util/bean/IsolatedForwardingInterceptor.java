/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.util.bean;

import java.lang.annotation.Annotation;
import java.util.Set;

import jakarta.enterprise.inject.spi.BeanAttributes;
import jakarta.enterprise.inject.spi.InterceptionType;
import jakarta.enterprise.inject.spi.Interceptor;
import jakarta.enterprise.inject.spi.ProcessBeanAttributes;
import jakarta.interceptor.InvocationContext;

/**
 * Delegating implementation of {@link Interceptor}. Separate delegate is used for {@link BeanAttributes} methods, allowing this
 * class to be used for processing of extension-provided beans.
 *
 * @see ProcessBeanAttributes
 *
 * @author Jozef Hartinger
 *
 */
public abstract class IsolatedForwardingInterceptor<T> extends IsolatedForwardingBean<T> implements Interceptor<T> {

    public abstract Interceptor<T> delegate();

    @Override
    public Set<Annotation> getInterceptorBindings() {
        return delegate().getInterceptorBindings();
    }

    @Override
    public boolean intercepts(InterceptionType type) {
        return delegate().intercepts(type);
    }

    @Override
    public Object intercept(InterceptionType type, T instance, InvocationContext ctx) throws Exception {
        return delegate().intercept(type, instance, ctx);
    }

    public static class Impl<T> extends IsolatedForwardingInterceptor<T> {
        private final WrappedBeanHolder<T, Interceptor<T>> cartridge;

        public Impl(WrappedBeanHolder<T, Interceptor<T>> cartridge) {
            this.cartridge = cartridge;
        }

        @Override
        public Interceptor<T> delegate() {
            return cartridge.getBean();
        }

        @Override
        protected BeanAttributes<T> attributes() {
            return cartridge.getAttributes();
        }
    }
}
