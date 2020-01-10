package com.flying.common.util;

public class Three<K, V, T> extends Pair<K, V> {
	private T three;

	public Three() {
		super();
	}

	public Three(K k, V v, T t) {
		super(k, v);
		this.setThree(t);
	}

	public T getThree() {
		return three;
	}

	public void setThree(T three) {
		this.three = three;
	}

	public String toString() {
		return String.format("Key: %s, Value: %s, Three: %s", super.getKey(), super.getValue(), this.three);
	}
}