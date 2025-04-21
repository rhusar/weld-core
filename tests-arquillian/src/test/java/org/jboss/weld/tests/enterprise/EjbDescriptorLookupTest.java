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
package org.jboss.weld.tests.enterprise;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.bean.SessionBean;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.test.util.Utils;
import org.jboss.weld.tests.category.Integration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @author pmuir
 */
@RunWith(Arquillian.class)
@Category(Integration.class) // all EJB tests need to use this category because the arquillian-weld-ee container does not implement EjbDescriptor.isPassivationCapable()
public class EjbDescriptorLookupTest {
    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(EjbDescriptorLookupTest.class))
                .addPackage(EjbDescriptorLookupTest.class.getPackage());
    }

    @Inject
    private BeanManagerImpl beanManager;

    @Test
    public void testCorrectSubType() {
        EjbDescriptor<CatLocal> descriptor = beanManager.getEjbDescriptor("Cat");
        assert descriptor.getClass().getSimpleName().equals("InternalEjbDescriptor");
        Bean<CatLocal> bean = beanManager.getBean(descriptor);
        Assert.assertNotNull(bean);
        Assert.assertTrue(bean instanceof SessionBean<?>);
        Assert.assertEquals(Cat.class, bean.getBeanClass());
        InjectionTarget<CatLocal> it = beanManager.createInjectionTarget(descriptor);
        Assert.assertNotNull(it);
        Assert.assertEquals(bean.getInjectionPoints(), it.getInjectionPoints());
        Assert.assertTrue(it.produce(beanManager.createCreationalContext(bean)) instanceof CatLocal);
    }

}
