package org.jboss.weld.tests.extensions;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Stereotype;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Stereotype
@Alternative
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Relief {
}
