package com.flying.common.helper;

import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;

public abstract class SequenceHelper {
	public static long nextVal(LocalModule module, String category) {
		Data resp = module.invoke("SequenceService:nextValue", new Data("category", category));
		return resp.getLong("value", System.currentTimeMillis());
	}
}
