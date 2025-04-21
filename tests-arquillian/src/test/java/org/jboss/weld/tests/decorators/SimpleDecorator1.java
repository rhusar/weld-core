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
package org.jboss.weld.tests.decorators;

import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.inject.Inject;

/**
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
@Decorator
public abstract class SimpleDecorator1 implements SimpleBean {
    @Inject
    @Delegate
    SimpleBean delegate;

    public static boolean echo1;
    public static boolean echo3;

    public static void reset() {
        echo1 = false;
        echo3 = false;
    }

    public int echo1(int i) {
        echo1 = true;
        return delegate.echo1(i);
    }

    public int echo3(int i) {
        echo3 = true;
        return delegate.echo3(i);
    }
}
