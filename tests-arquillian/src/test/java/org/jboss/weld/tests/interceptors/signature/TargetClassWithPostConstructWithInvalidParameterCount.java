package org.jboss.weld.tests.interceptors.signature;

import jakarta.annotation.PostConstruct;
import jakarta.interceptor.InvocationContext;

/**
* @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
*/
public class TargetClassWithPostConstructWithInvalidParameterCount {
    public boolean postConstructInvoked;

    @PostConstruct
    public void postConstructWithInvalidParameterCount(InvocationContext invocationContext) {
        postConstructInvoked = true;
    }

    public String foo() {
        return "foo";
    }
}
