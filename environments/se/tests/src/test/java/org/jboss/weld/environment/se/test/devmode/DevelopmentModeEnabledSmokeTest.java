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
package org.jboss.weld.environment.se.test.devmode;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import org.jboss.arquillian.container.se.api.ClassPath;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.probe.MonitoredComponent;
import org.jboss.weld.probe.ProbeExtension;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DevelopmentModeEnabledSmokeTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ClassPath.builder().add(ShrinkWrap.create(BeanArchive.class).addClasses(DevelopmentModeEnabledSmokeTest.class, Omega.class))
                .addSystemProperty(Weld.DEV_MODE_SYSTEM_PROPERTY, "true").build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDevelopmentMode() {
        try (WeldContainer container = new Weld().initialize()) {
            assertNotNull(container.select(ProbeExtension.class).get());
            BeanManager beanManager = container.getBeanManager();
            Bean<Omega> bean = (Bean<Omega>) beanManager.resolve(beanManager.getBeans(Omega.class));
            assertTrue(bean.getStereotypes().contains(MonitoredComponent.class));
            container.select(Omega.class).get().ping();
        }
    }

}
