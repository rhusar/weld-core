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
package org.jboss.weld.tests.injectionPoint;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.util.reflection.Reflections;

public class CowShed {

    @Produces
    public Cow get(InjectionPoint ip) {
        assert ip.getAnnotated() instanceof AnnotatedField<?>;
        assert Reflections.<AnnotatedField<?>>cast(ip.getAnnotated()).getDeclaringType().getJavaClass().equals(Field.class);
        return new Cow("daisy");
    }

}
