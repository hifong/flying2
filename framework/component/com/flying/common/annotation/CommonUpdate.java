package com.flying.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flying.framework.annotation.service.Handler;

@Deprecated
@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.common.annotation.handler.CommonUpdateHandler")
public @interface CommonUpdate {
	Position position() default Position.none_body;
	String[] params() default {};
	String usql() default "";
	enum Position{
		before_body, after_body, none_body
	}
}
