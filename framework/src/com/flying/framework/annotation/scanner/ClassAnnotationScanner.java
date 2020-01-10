package com.flying.framework.annotation.scanner;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import com.flying.common.util.Utils;
import com.flying.framework.annotation.Service;
import com.flying.framework.metadata.Meta;
import com.flying.framework.metadata.MetaAnnotationHandler;
import com.flying.framework.module.LocalModule;

public class ClassAnnotationScanner {
	private static Map<Class<? extends Annotation>, RegisteredAnnotationHandler<? extends Annotation>> handlers = Utils.newHashMap();
	static {
		handlers.put(Service.class, new ServiceAnnotationHandler());
		handlers.put(Meta.class, new MetaAnnotationHandler());
	}
	
	@SuppressWarnings("rawtypes")
	private static RegisteredAnnotationHandler getRegisteredHandler(Class<? extends Annotation> cls) {
		return handlers.get(cls);
	}
	
	private static Set<Class<? extends Annotation>> getRegisteredAnnotations() {
		return handlers.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public static void scanClass(LocalModule module, Class<?> cls) {
		for(Class<? extends Annotation> ancls: getRegisteredAnnotations()) {
			Annotation an = cls.getAnnotation(ancls);
			if(an != null) {
				getRegisteredHandler(ancls).handle(module, an, cls);
			}
		}
	}
}
