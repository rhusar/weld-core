/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.integration.multideployment;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanDiscoveryMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.weld.tests.category.Integration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Weld cross deployment injection test case. Verifies that weld can read beans.xml and scan classes outside the
 * deployment.
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
@Category(Integration.class)
public class CrossDeploymentTest {

    @Deployment(name = "d1", order = 1, testable = false)
    public static Archive<?> deploy() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "d1.jar");
        jar.addClasses(SimpleBean.class);
        jar.addAsManifestResource(new BeansXml(BeanDiscoveryMode.ALL), "beans.xml");
        return jar;
    }

    @Deployment(name = "d2", order = 2)
    public static Archive<?> deploy2() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "d2.jar");
        jar.addClass(CrossDeploymentTest.class);
        jar.addAsManifestResource(new BeansXml(BeanDiscoveryMode.ALL), "beans.xml");
        jar.addAsManifestResource(new StringAsset("Dependencies: deployment.d1.jar meta-inf\n"), "MANIFEST.MF");
        return jar;
    }

    @Inject
    private SimpleBean bean;

    @Test
    @OperateOnDeployment("d2")
    public void testSimpleBeanInjected() throws Exception {
        Assert.assertNotNull(bean);
    }
}
