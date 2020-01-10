package com.flying.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flying.framework.annotation.service.Handler;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.common.annotation.handler.DaoCreateHandler")
public @interface DaoCreate {
	public Position position() default Position.after_body;
	public String sql() default "";
	public String entity();
	public String connectionTag() default "jdbcTemplate";
	enum Position{
		before_body, after_body, none_body
	}
}
