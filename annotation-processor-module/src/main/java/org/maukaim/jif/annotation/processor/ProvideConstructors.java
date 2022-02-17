package org.maukaim.jif.annotation.processor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Check if Constructor with list of classes <>value</> exist
 */
@Retention(RetentionPolicy.SOURCE)
@Target(TYPE)
@Inherited
public @interface ProvideConstructors {
    ProvideConstructor[] value();
}
