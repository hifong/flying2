package com.flying.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flying.framework.annotation.service.Handler;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.common.annotation.handler.MethodLoggerHandler")
public @interface MethodLogger {
	String logger() default "MethodLogger";
	String tag() default "";
	String[] requests() default "";
	String[] responses() default "";
	boolean logTime() default true;
	boolean logServiceId() default true;
	boolean logErrorSimple() default true;
	boolean logErrorDetail() default false;
}
