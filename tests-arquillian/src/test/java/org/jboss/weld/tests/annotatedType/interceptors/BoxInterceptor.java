package org.jboss.weld.tests.annotatedType.interceptors;

import java.io.Serializable;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@BoxBinding
@SuppressWarnings("serial")
public class BoxInterceptor implements Serializable {

    @AroundInvoke
    Object intercept(InvocationContext ctx) throws Exception {
        return true;
    }
}
