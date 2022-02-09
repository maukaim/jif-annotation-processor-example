package org.maukaim.jif.annotation.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Check if Constructor has the arguments required
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface CheckConstructor {
    Class<?>[] value() default {};
}
