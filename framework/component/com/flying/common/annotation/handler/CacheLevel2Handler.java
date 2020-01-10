package com.flying.common.annotation.handler;

import java.util.Map;

import org.apache.log4j.Logger;

import com.flying.common.annotation.CacheLevel2;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class CacheLevel2Handler implements ServiceHandler<CacheLevel2> {
	private final static Logger log = Logger.getLogger(CacheLevel2Handler.class);

	@Override
	public Data handle(CacheLevel2 annotation, Data request, ServiceHandlerContext context) throws Exception {
		String[] keys = annotation.keys();
		StringBuffer cacheKey = new StringBuffer(annotation.tag());
		if (keys != null) {
			for (String k : keys) {
				if (cacheKey.length() != 0)
					cacheKey.append(".");
				cacheKey.append(request.getString(k, k));
			}
		}
		Map<String, Object> cache = ServiceContext.getContext().getTempVariables();
		// read cache
		if (cache.containsKey(cacheKey.toString())) {
			Data d = (Data)cache.get(cacheKey.toString());
			log.debug("Level2CacheHandler,Get data from cache, key[" + cacheKey + "]");
			return d;
		} else {
			Data d = context.doChain(request);
			cache.put(cacheKey.toString(), d);
			log.debug("Level2CacheHandler,Set data to cache,key[" + cacheKey + "]");
			return d;
		}
	}

}
