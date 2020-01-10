package com.flying.common.annotation.handler;

import com.flying.common.annotation.Cacheable;
import com.flying.common.log.Logger;
import com.flying.framework.cache.Cache;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class CacheableHandler implements ServiceHandler<Cacheable> {
	private final static Logger log = Logger.getLogger(CacheableHandler.class);

	@Override
	public Data handle(Cacheable annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		Cache cache = module.getCacheProvider() != null?module.getCacheProvider().getCache(module, annotation.cacheName()):null;
		
		final String tag = annotation.tag().startsWith("$")? request.getString(annotation.tag().substring(1), annotation.tag()) : annotation.tag();
		final String[] keys = annotation.keys();
		StringBuffer cacheKey = new StringBuffer(tag).append("-");
		if (keys != null) {
			for (String k : keys) {
				if(!request.contains(k)) continue;
				if (cacheKey.length() != 0) cacheKey.append(".");
				cacheKey.append(request.getString(k, k));
			}
		}
		// read cache
		if (cache != null) {
			Object value = cache.get(cacheKey.toString());
			if (value != null) {
				log.debug(context.getServiceId() + "." + context.getMethodName() + " Get data from cache, key[" + cacheKey + "]");
				return (Data) value;
			}
		}
		// cache no data,continue
		Data d = context.doChain(request);
		// write cache
		if (cache != null) {
			cache.put(cacheKey.toString(), d);
			log.debug(context.getServiceId() + "." + context.getMethodName() + " Set data to cache,key[" + cacheKey + "]");
		}
		return d;
	}

}
