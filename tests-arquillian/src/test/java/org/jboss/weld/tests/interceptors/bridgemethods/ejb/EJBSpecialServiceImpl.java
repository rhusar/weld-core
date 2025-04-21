package org.jboss.weld.tests.interceptors.bridgemethods.ejb;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;

import org.jboss.weld.tests.interceptors.bridgemethods.common.SomeInterceptorBinding;
import org.jboss.weld.tests.interceptors.bridgemethods.common.SpecialService;

/**
 *
 */
@Stateless
@Local(SpecialService.class)
@SomeInterceptorBinding
public class EJBSpecialServiceImpl implements SpecialService {

    public void doSomething(String param) {
    }

    public String returnSomething() {
        return "";
    }
}