package com.flying.common.cache;

import com.flying.framework.cache.Cache;
import com.flying.framework.cache.CacheProvider;
import com.flying.framework.module.LocalModule;

public class MemoryCacheProvider implements CacheProvider {

	@Override
	public synchronized Cache getCache(LocalModule module, String cacheName) {
		return new MemoryCache(module);
	}

}
