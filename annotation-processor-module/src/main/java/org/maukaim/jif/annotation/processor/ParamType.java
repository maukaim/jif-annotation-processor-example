package org.maukaim.jif.annotation.processor;

import javax.lang.model.type.NullType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Check if Constructor with list of classes <>value</> exist
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface ParamType {
    Class<?> value();
    Class<?>[] genericTypes() default {};
}
