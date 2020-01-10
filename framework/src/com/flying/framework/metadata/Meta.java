package com.flying.framework.metadata;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.flying.framework.annotation.Property;

/**
 * @author king
 * 元数据提供者
 * 描述
 * 1、实体类，整个实体类是一个Metadata，从类的Field解析出数据；
 * 2、方法，方法返回元数据对象
 *
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface Meta {
	String id();
	String title();
	String table() default "";
	String sql() default "";
	String entity() default "";
	String[] fields() default {};
	String[] primaryKeys() default {};
	//settings
	Property[] props() default {};  //参数扩展属性
}
