package com.flying.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flying.framework.annotation.service.Handler;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.common.annotation.handler.DaoUpdateHandler")
public @interface DaoUpdate {
	public Position position() default Position.after_body;
	public String sql() default "";
	public String wsql() default "";		//field=value and f=value2
	public String entity() default "";
	public int maxEffectRows() default -1;	//如果指定非负数，一次最多能更新多少行，否则抛异常，用于控制不确定的数据更新
	public String connectionTag() default "jdbcTemplate";
	enum Position{
		before_body, after_body, none_body
	}
}
