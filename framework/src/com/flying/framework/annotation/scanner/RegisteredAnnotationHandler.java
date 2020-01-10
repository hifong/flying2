package com.flying.framework.annotation.scanner;

import java.lang.annotation.Annotation;

import com.flying.framework.module.LocalModule;

public interface RegisteredAnnotationHandler<T extends Annotation> {
	void handle(LocalModule module, T annotation, Class<?> cls);
}
