package com.flying.framework.annotation;

public interface ConstantEnum<T> {
	T value();
	String text();
	
	default  boolean equalsByValue(T x) {
		return value().equals(x);
	}
}
