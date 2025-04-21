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
package org.jboss.weld.tests.interceptors.lifecycle;

import static org.jboss.weld.util.reflection.Reflections.cast;
import static org.junit.Assert.assertTrue;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Jozef Hartinger
 *
 */
@RunWith(Arquillian.class)
public class LifecycleInterceptorOrderingTest {

    @Inject
    private BeanManager manager;

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(LifecycleInterceptorOrderingTest.class))
                .intercept(FooInterceptor.class, BarInterceptor.class, BazInterceptor.class)
                .addPackage(LifecycleInterceptorOrderingTest.class.getPackage());
    }

    @Test
    public void testPostConstruct() {
        reset();
        Bean<Donkey> bean = cast(manager.resolve(manager.getBeans(Donkey.class)));
        CreationalContext<Donkey> ctx = manager.createCreationalContext(bean);
        @SuppressWarnings("unused")
        Donkey instance = bean.create(ctx);
        assertTrue(FooInterceptor.isPostConstructCalled());
        assertTrue(BarInterceptor.isPostConstructCalled());
        assertTrue(BazInterceptor.isPostConstructCalled());
    }

    @Test
    public void testPreDestroy() {
        reset();
        Bean<Donkey> bean = cast(manager.resolve(manager.getBeans(Donkey.class)));
        CreationalContext<Donkey> ctx = manager.createCreationalContext(bean);
        Donkey instance = bean.create(ctx);
        bean.destroy(instance, ctx);
        assertTrue(FooInterceptor.isPreDestroyCalled());
        assertTrue(BarInterceptor.isPreDestroyCalled());
        assertTrue(BazInterceptor.isPreDestroyCalled());
    }

    private void reset() {
        FooInterceptor.reset();
        BarInterceptor.reset();
        BazInterceptor.reset();
        Donkey.reset();
    }
}
