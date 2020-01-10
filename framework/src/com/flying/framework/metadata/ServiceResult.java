package com.flying.framework.metadata;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.flying.framework.annotation.Property;

@Target({FIELD})
@Retention(RUNTIME)
public @interface ServiceResult {
	String serviceId() default "";
	Property[] params() default {};
	String valueField() default"";
}
