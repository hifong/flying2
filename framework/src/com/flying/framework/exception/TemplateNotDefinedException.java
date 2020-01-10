package com.flying.framework.exception;


@SuppressWarnings("serial")
public class TemplateNotDefinedException extends ObjectNotFoundException {

	public TemplateNotDefinedException(String id) {
		super("Template", id);
	}

}
