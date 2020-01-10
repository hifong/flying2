package com.flying.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flying.framework.annotation.event.Event;
import com.flying.framework.annotation.service.ServiceConfig;

@Target({ElementType.TYPE})  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
public @interface Service {  
    String value();
    String desc() default "";
    boolean loadOnStartup() default false;
    boolean isSingleInstance() default true;
    ServiceConfig[] configs() default {};
    Event[] events() default {};
}