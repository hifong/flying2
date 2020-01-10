package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodInfo {
	String 	value() 	default "";		//方法描述
	String 	version() 	default "1.0";
	Scope 	scope() 	default Scope.PUBLIC;
	
	enum Scope {
		PUBLIC, 	//公开接口
		LOCAL		//本地接口
	}
}
