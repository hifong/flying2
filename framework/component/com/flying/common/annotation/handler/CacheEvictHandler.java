package com.flying.common.annotation.handler;

import org.apache.log4j.Logger;

import com.flying.common.annotation.CacheEvict;
import com.flying.framework.cache.Cache;
import com.flying.framework.cache.Removable;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class CacheEvictHandler implements ServiceHandler<CacheEvict>{
	private final static Logger log = Logger.getLogger(CacheEvictHandler.class);

	@Override
	public Data handle(CacheEvict annotation, Data request, ServiceHandlerContext context)
					throws Exception {
		final LocalModule module = context.getModule();
		try {
			return context.doChain(request);
		} finally {
			Cache cache = module.getCacheProvider() != null?module.getCacheProvider().getCache(module, annotation.cacheName()):null;
			if(cache == null) return null;

			final String tag = annotation.tag().startsWith("$")? request.getString(annotation.tag().substring(1), annotation.tag()) : annotation.tag();
			final String[] keys = annotation.keys();
			
			StringBuffer cacheKey = new StringBuffer(tag);
			if(keys != null) {
				for(String k: keys) {
					if(!request.contains(k)) continue;
					if(cacheKey.length() != 0) cacheKey.append(".");
					cacheKey.append(request.getString(k, k));
				}
			}
			if(cache instanceof Removable) {
				((Removable)cache).remove(cacheKey.toString());
				log.debug("RemoveCache remove key:" + cacheKey);
			}
		}
	}

}
