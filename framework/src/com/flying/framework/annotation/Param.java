package com.flying.framework.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface Param {
	String value();
	String alias() default "";		//别名
	String desc() default "";
	String tag() default "";
	//validation
	@SuppressWarnings("rawtypes")
	Class<? extends ConstantEnum> enumClass() default ConstantEnum.class;
	String max() default "";		//最大，日期使用yyy-MM-dd格式
	String min() default "";		//最小，日期使用yyy-MM-dd格式
	int maxlength() default 0;		//最大长度，仅适合字符串参数
	int length() default 0;		    //指定长度，字符串必须是特定长度，仅适合字符串参数
	boolean required() default false;	//是否必须
	String format() default "";		//字符串满足的格式
	//settings
	Property[] props() default {};  //参数扩展属性
	String generator() default "";  //值生成器
	
}
