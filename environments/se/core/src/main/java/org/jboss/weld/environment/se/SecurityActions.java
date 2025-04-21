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
package org.jboss.weld.environment.se;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;

import org.jboss.weld.exceptions.WeldException;
import org.jboss.weld.security.GetDeclaredConstructorAction;
import org.jboss.weld.security.NewInstanceAction;

/**
 *
 * @author Martin Kouba
 */
final class SecurityActions {

    private SecurityActions() {
    }

    /**
     *
     * @param javaClass
     * @return a new instance of the given class
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    static <T> T newInstance(Class<T> javaClass)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (System.getSecurityManager() != null) {
            try {
                Constructor<T> constructor = AccessController.doPrivileged(GetDeclaredConstructorAction.of(javaClass));
                return AccessController.doPrivileged(NewInstanceAction.of(constructor));
            } catch (PrivilegedActionException e) {
                throw new WeldException(e.getCause());
            }
        } else {
            return javaClass.getDeclaredConstructor().newInstance();
        }
    }

    static <T> Constructor<T> getDeclaredConstructor(Class<T> javaClass, Class<?>... parameterTypes)
            throws NoSuchMethodException, PrivilegedActionException {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(GetDeclaredConstructorAction.of(javaClass, parameterTypes));
        } else {
            return javaClass.getDeclaredConstructor(parameterTypes);
        }
    }

    static <T> T newInstance(Constructor<T> constructor, Object... params)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (System.getSecurityManager() != null) {
            try {
                return AccessController.doPrivileged(NewInstanceAction.of(constructor, params));
            } catch (PrivilegedActionException e) {
                throw new WeldException(e.getCause());
            }
        } else {
            return constructor.newInstance(params);
        }
    }

    /**
     *
     * @param hook
     */
    static void addShutdownHook(Thread hook) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    Runtime.getRuntime().addShutdownHook(hook);
                    return null;
                }
            });
        } else {
            Runtime.getRuntime().addShutdownHook(hook);
        }
    }

}
