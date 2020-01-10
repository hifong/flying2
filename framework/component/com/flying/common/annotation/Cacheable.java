package com.flying.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flying.framework.annotation.service.Handler;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.common.annotation.handler.CacheableHandler")
public @interface Cacheable {
	String cacheName() default "default-cache";
	String tag();		//all object of one category,so you can remove or improve it's version
	String[] keys() default {};		//other keys
}
