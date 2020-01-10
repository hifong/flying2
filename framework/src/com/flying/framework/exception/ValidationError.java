package com.flying.framework.exception;

import com.flying.common.util.StringUtils;

@SuppressWarnings("rawtypes")
public class ValidationError {
	private final String title;		//字段标题
	private final String field;
	private final Class type;
	private Object realValue;
	private Object referenceValue;
	private final OP op;
	
	public ValidationError(String title, String field, Class type, OP op) {
		this.title = title;
		this.field = field;
		this.type = type;
		this.op = op;
	}
	

	public ValidationError(String title, String field, Class type, OP op, Object realValue, Object referenceValue) {
		this(title, field, type, op);
		this.realValue = realValue;
		this.referenceValue = referenceValue;
	}
	
	public String getTitle() {
		return title == null?field: title;
	}
	
	public String getField() {
		return field;
	}

	public Object getRealValue() {
		return realValue;
	}

	public OP getOp() {
		return op;
	}
	
	public Object getReferenceValue() {
		return this.referenceValue;
	}

	public Class getType() {
		return type;
	}

	public enum OP {
		max,
		min,
		enums,
		length,
		maxlength,
		required,
		format
	}
	
	public String toString() {
		if(this.op == OP.required) 
			return getTitle() + " 不能为空!";
		else if(this.op == OP.max)
			return getTitle() + " 超出了最大值：" + this.referenceValue;
		else if(this.op == OP.min)
			return getTitle() + " 超出了最小值：" + this.referenceValue;
		else if(this.op == OP.length)
			return getTitle() + " 不满足指定长度：" + this.referenceValue;
		else if(this.op == OP.maxlength)
			return getTitle() + " 超出了最大长度：" + this.referenceValue;
		else if(this.op == OP.format)
			return getTitle() + " 不满足格式：" + this.referenceValue;
		else if(this.op == OP.enums)
			return getTitle() + " 只能取其中之一：" + StringUtils.join(this.referenceValue, ",");
		else
			return getTitle() + " " + this.op + " " + this.referenceValue;
	}
}
