package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface ServiceField {
	String moduleId() default "";
	String serviceId();
	ServiceFieldParam[] params() default {};
	boolean lazyLoad() default true;
	String valueAttribute() default "SELF";
	
	public final static String NONE = "NONE";
}
