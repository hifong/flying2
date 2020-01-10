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
@Handler(handler="com.flying.common.annotation.handler.CommonQueryHandler")
public @interface CommonQuery {
	Position position() default Position.none_body;
	boolean pageable() default true;
	boolean single() default false;		//result just one record
	String qsql() default "";		//query sql,when empty serviceConfig.get(sql.methodName)
	String csql() default "";		//count sql,when empty serviceConfig.get(sql.methodName.count)
	String[] params() default {};		//params from request
	
	enum Position{
		before_body, after_body, none_body
	}
}
