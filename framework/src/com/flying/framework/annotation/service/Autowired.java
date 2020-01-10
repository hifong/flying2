package com.flying.framework.annotation.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.CONSTRUCTOR,ElementType.FIELD,ElementType.METHOD})
public abstract @interface Autowired{
  
  // Method descriptor #18 ()Z
  public abstract boolean required() default true;

}