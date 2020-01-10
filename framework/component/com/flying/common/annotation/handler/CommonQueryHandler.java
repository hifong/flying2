package com.flying.common.annotation.handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.annotation.CommonQuery;
import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.data.Data;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class CommonQueryHandler implements ServiceHandler<CommonQuery> {
	private final static Logger log = Logger.getLogger(CommonQueryHandler.class);

	@SuppressWarnings("unchecked")
	@Override
	public Data handle(CommonQuery annotation, Data request, ServiceHandlerContext context) throws Exception {
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		
		Data result = null;

		if (annotation.position() == CommonQuery.Position.after_body) {
			result = context.doChain(request);
			request.merge(result, true);
			if (result != null && result.contains(Codes.CODE))
				return result;
		}
		if(result == null) result = new Data();
		
		JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getModule().getSpringBeanFactory().getBean("jdbcTemplate");
		final int page = request.getInt("page", 1) - 1;
		final int size = request.getInt("rows", 10);
		//
		final String[] params = annotation.params();
		final Object[] queryParams = new Object[params.length + (annotation.pageable() && !annotation.single()?2:0)];
		final Object[] totalParams = new Object[params.length];
		for (int i = 0; i < annotation.params().length; i++) {
			if (params[i].indexOf(":") < 0) {
				queryParams[i] = request.getString(params[i]);
				totalParams[i] = request.getString(params[i]);
			} else {
				String[] x = params[i].split(":");
				queryParams[i] = request.getString(x[0], x[1]);
				totalParams[i] = request.getString(x[0], x[1]);
			}
		}
		if(annotation.pageable() && !annotation.single()) {
			queryParams[queryParams.length - 2] = page * size;
			queryParams[queryParams.length - 1] = size;
		}
		//
		final String querySQL = StringUtils.isEmpty(annotation.qsql()) ? context.getModule().getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName)
						: annotation.qsql();
		final String countSQL = StringUtils.isEmpty(annotation.csql()) ? context.getModule().getModuleConfig().getServiceConfig(serviceId)
						.getConfig("sql." + methodName + ".count") : annotation.csql();
		
		log.debug(serviceId+":"+methodName+"-->[" + querySQL+"] with [" + queryParams +"]");
		log.debug(serviceId+":"+methodName+"-->[" + countSQL+"] with [" + totalParams +"]");
						//
		@SuppressWarnings("rawtypes")
		final List rows = jdbcTemplate.queryForList(querySQL, queryParams);
		if (annotation.single()) {
			result = rows.isEmpty() ? request : new Data((Map<String, Object>) (rows.get(0))).merge(request, false);
		} else {
			final int total = StringUtils.isEmpty(countSQL)?0: jdbcTemplate.queryForInt(countSQL, totalParams);
			result = new Data(Codes.CODE, Codes.SUCCESS, "rows", rows, "total", total).merge(request, false);
			
			int pageCount = total % size == 0?total/size: (total/size + 1);
			List<Integer> pages = Utils.newArrayList();
			for(int i= Math.max(1, page - 5); i <= Math.min(pageCount, page + 5); i++) {
				pages.add(i);
			}
			result.put("pageNums", pages);
			result.put("pageNum", page + 1);
			result.put("pageSize", size);
			result.put("pageCount", pageCount);
		}
		if (annotation.position() == CommonQuery.Position.before_body) {
			result.putAll(context.doChain(request));
			request.merge(result, true);
		}
		return result;
	}

}
