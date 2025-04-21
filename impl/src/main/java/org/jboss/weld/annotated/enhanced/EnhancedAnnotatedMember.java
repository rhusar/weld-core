/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.annotated.enhanced;

import java.lang.reflect.Member;

import jakarta.enterprise.inject.spi.AnnotatedMember;

/**
 * AnnotatedMember provides enhanced access to an annotated member
 *
 * @author Pete Muir
 */
public interface EnhancedAnnotatedMember<T, X, S extends Member> extends EnhancedAnnotated<T, S>, AnnotatedMember<X> {

    /**
     * Gets an abstraction of the declaring class
     *
     * @return The declaring class
     */
    EnhancedAnnotatedType<X> getDeclaringType();

    /**
     * Returns a lightweight implementation of {@link AnnotatedMember} with minimal memory footprint.
     *
     * @return the slim version of this {@link AnnotatedMember}
     */
    AnnotatedMember<X> slim();

}
