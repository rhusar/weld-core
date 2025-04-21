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
package org.jboss.weld.tests.contexts.conversation;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ConversationScoped
public class Thunderstorm implements Serializable {

    @Inject
    Cloud cloud;

    public static final String NAME = Thunderstorm.class.getName();

    private static final long serialVersionUID = 5765109971012677278L;

    @PreDestroy
    public void destroy() {
        throw new RuntimeException();
    }

    public String getName() {
        return NAME;
    }

    public String cloud() {
        cloud.setName("bob");
        return "cloud";
    }

}
