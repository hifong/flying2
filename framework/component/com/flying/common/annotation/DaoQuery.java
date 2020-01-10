package com.flying.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flying.framework.annotation.service.Handler;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Handler(handler="com.flying.common.annotation.handler.DaoQueryHandler")
public @interface DaoQuery {
	public Position position() default Position.after_body;
	public String entity() default "";
	public boolean pageable() default false;
	public boolean single() default false;		//result just one record
	public String qsql() default "";		//select xx from x
	public String wsql() default "";		//field=value and f=value2
	public String osql() default "";		//order by
	public String gsql() default "";		//group by
	public String csql() default "";		//select count(1) from x
	public String resultsets() default "rows";
	public String connectionTag() default "jdbcTemplate";
	
	public Class<?> modelClass() default Object.class;
	public boolean throwsNotFoundException() default false;
	
	enum Position{
		before_body, after_body, none_body
	}
}
