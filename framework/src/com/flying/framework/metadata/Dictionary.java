package com.flying.framework.metadata;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
public @interface Dictionary {
	String relationField();
	String entity();
	String referenceField();
	String valueField();
}
