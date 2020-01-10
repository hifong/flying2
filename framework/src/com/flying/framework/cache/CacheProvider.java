package com.flying.framework.cache;

import com.flying.framework.module.LocalModule;

public interface CacheProvider {
	public final static String DEFAULT_CACHE_NAME = "default-cache";
	
	Cache getCache(LocalModule module, String cacheName);
}
