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
package org.jboss.weld.tests.annotatedType.decoration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;

/**
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public abstract class MockAnnotated implements Annotated {

    private static class InjectLiteral extends AnnotationLiteral<Inject> implements Inject {

        public static final Inject INSTANCE = new InjectLiteral();

    }

    private Annotated delegate;

    public MockAnnotated(Annotated delegate) {
        this.delegate = delegate;
    }

    Annotated getDelegate() {
        return delegate;
    }

    public Type getBaseType() {
        return delegate.getBaseType();
    }

    public Set<Type> getTypeClosure() {
        return delegate.getTypeClosure();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        if (annotationType == Inject.class) {
            return (T) InjectLiteral.INSTANCE;
        }
        return null;
    }

    public Set<Annotation> getAnnotations() {
        return Collections.singleton((Annotation) InjectLiteral.INSTANCE);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return annotationType == Inject.class;
    }
}
