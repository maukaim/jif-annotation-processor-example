package org.maukaim.jif.annotation.processor;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Check if Constructor with list of classes <>value</> exist
 */
@Retention(RetentionPolicy.SOURCE)
@Target(TYPE)
@Inherited
@Repeatable(HasConstructors.class)
public @interface HasConstructor {
    Parameter[] value() default {};

    boolean isOrdered() default true;
}
