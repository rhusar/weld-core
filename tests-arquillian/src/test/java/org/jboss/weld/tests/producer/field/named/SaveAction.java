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
package org.jboss.weld.tests.producer.field.named;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("save")
@SessionScoped
public class SaveAction implements Serializable {

    @Produces
    @Named
    private Employee employeeField = new Employee();

    private Employee employeeMethod = new Employee();

    private boolean executeCalled;

    @Produces
    @Named
    public Employee getEmployeeMethod() {
        return employeeMethod;
    }

    public SaveAction() {
    }

    public String execute() {
        assert employeeMethod.getName().equals("Gavin");
        assert employeeField.getName().equals("Pete");
        this.executeCalled = true;
        return "/home?faces-redirect=true";
    }

    public boolean isExecuteCalled() {
        return executeCalled;
    }

}
