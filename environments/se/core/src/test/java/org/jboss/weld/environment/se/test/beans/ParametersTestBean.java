/**
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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

package org.jboss.weld.environment.se.test.beans;

import org.jboss.weld.environment.se.bindings.Parameters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @author Peter Royle
 */
@ApplicationScoped
public class ParametersTestBean implements Serializable {

    List<String> parameters;

    public ParametersTestBean() {
    }

    @Inject
    public ParametersTestBean(@Parameters List<String> params) {
        this.parameters = params;
        // even if no args are given, it should will always at least be an empty
        // array
        assertNotNull(params);
    }

    public List<String> getParameters() {
        return parameters;
    }

}
