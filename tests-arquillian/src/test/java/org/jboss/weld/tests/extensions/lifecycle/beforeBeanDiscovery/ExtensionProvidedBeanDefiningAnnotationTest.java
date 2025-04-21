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
package org.jboss.weld.tests.extensions.lifecycle.beforeBeanDiscovery;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;

import org.junit.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.category.Integration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @author Kirill Gaevskii
 *
 */
@RunWith(Arquillian.class)
@Category(Integration.class)
@Ignore("WELD-1624")
public class ExtensionProvidedBeanDefiningAnnotationTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, Utils.getDeploymentNameAsHash(ExtensionProvidedBeanDefiningAnnotationTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addClasses(MainBean.class, ClassForInject.class)
                .addAsLibraries(
                        ShrinkWrap.create(JavaArchive.class).addClasses(ScopeRegistryExtension.class, CustomAnnotation.class, CustomContext.class)
                                .addAsServiceProvider(Extension.class, ScopeRegistryExtension.class));
    }

    @Inject
    MainBean mainBean;

    @Test
    public void testCreateBean() {
        Assert.assertNotNull(mainBean);
    }

    @Test
    public void testInjectedBeanMustSayHello() {
        Assert.assertEquals("Hello world!", mainBean.greetingFromBean());
    }
}
