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
package org.jboss.weld.tests.proxy.client.privatemethods;

import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

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
 * @author Martin Kouba
 *
 */
@RunWith(Arquillian.class)
public class ClientProxyPrivateMethodsTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(ClientProxyPrivateMethodsTest.class))
                .addPackage(ClientProxyPrivateMethodsTest.class.getPackage()).addClass(Utils.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPrivateMethodsIgnored(BeanManager beanManager) {
        Bean<Foo> fooBean = (Bean<Foo>) beanManager.resolve(beanManager.getBeans(Foo.class));
        Foo foo = (Foo) beanManager.getReference(fooBean, Foo.class, beanManager.createCreationalContext(fooBean));
        foo.ping();
        assertNull(find("pong", foo.getClass().getDeclaredMethods()));
    }

    private Method find(String name, Method[] methods) {
        for (Method method : methods) {
            if (name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
