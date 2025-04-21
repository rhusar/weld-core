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
package org.jboss.weld.tests.specialization.weld1651.broken;

import jakarta.enterprise.inject.spi.DefinitionException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.test.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * {@link MyRawBean} extends raw version of {@link MyBean}, therefore all type parameters/arguments among supertypes of
 * {@link MyBean} are "ignored". Especially type argument {@link Number} in the generic definition '{@link MyInterface} extends
 * {@link MySuperInterface}<{@link Number}>.
 *
 * @author Matus Abaffy
 */
@RunWith(Arquillian.class)
public class GenericBeanRawSpecialization03Test {

    @Deployment
    @ShouldThrowException(DefinitionException.class)
    public static Archive<?> createArchive() {
        return ShrinkWrap.create(BeanArchive.class, Utils.getDeploymentNameAsHash(GenericBeanRawSpecialization03Test.class)).addClasses(GenericBeanRawSpecialization03Test.class, MyBean.class,
                MyRawBean.class, MyInterface.class, MySuperInterface.class);
    }

    @Test
    public void testDeploymentWithSpecializationOfRawType() {
        // should throw definition exception
     }
}
