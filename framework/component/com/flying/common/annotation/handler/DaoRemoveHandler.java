package com.flying.common.annotation.handler;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.annotation.DaoRemove;
import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.data.Data;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceDescriptor.ServiceMethodParameter;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class DaoRemoveHandler implements ServiceHandler<DaoRemove> {
	private final static Logger log = Logger.getLogger(DaoRemoveHandler.class);
	private final static Logger sqllog = Logger.getLogger("sql");
	
	@Override
	public Data handle(DaoRemove annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		
		Data result = null;

		if (annotation.position() == DaoRemove.Position.after_body) {
			result = context.doChain(request);
			request.merge(result, true);
			if (result != null && result.contains(Codes.CODE))
				return result;
		}
		if(result == null) result = ModelFactory.createModelInstance(module, context.getReturnType());
		
		//
		final Metadata metadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final List<ServiceMethodParameter> methodParams = context.getMethodParams();
		
		final StringBuffer entitySql = new StringBuffer("DELETE FROM " + metadata.getTable() + " WHERE ");
		final List<String> params = Utils.newArrayList();
		for(ServiceMethodParameter mp: methodParams) {			//fields from method params
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;
			if(!params.isEmpty()) entitySql.append(" AND ");
			entitySql.append(mp.getParam().value()).append("=? ");
			params.add(mp.getParam().value());
		}
		if(params.isEmpty()) {						//delete rows by primary key
			Set<String> pks = metadata.getPrimaryKeys();
			int c = 0;
			for(String pk: pks) {
				if(c > 0) entitySql.append(" AND ");
				entitySql.append(pk).append("=? ");
				params.add(pk);
				c ++;
			}
		}
		//
		final String configSql = module.getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName);
		final String sql = !StringUtils.isEmpty(annotation.sql()) ? annotation.sql(): !StringUtils.isEmpty(configSql)?configSql: entitySql.toString();
		
		//
		final Object[] values = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			Object val = request.get(params.get(i));
			if(val != null && val.getClass().isArray()) {
				val = Array.get(val, 0);
			}
			values[i] = val;
		}
		//
		sqllog.info(serviceId+":"+methodName+"-->[" + sql+"] with [" + StringUtils.join(values, ",") +"]");
		//
		try {
			JdbcTemplate jdbcTemplate = (JdbcTemplate) module.getSpringBeanFactory().getBean(annotation.connectionTag());
			final int effectRows = jdbcTemplate.update(sql, values);
			//
			result = ModelFactory.createModelInstance(module, context.getReturnType());
			result.put(Codes.CODE, effectRows > 0? Codes.SUCCESS: Codes.OBJECT_NOT_EXISTS);
			result.put(Codes.EFFECT_ROWS, effectRows);
			//
			if (annotation.position() == DaoRemove.Position.before_body) {
				result.putAll(context.doChain(request));
				request.merge(result, true);
			}
		}catch(Exception e) {
			log.error(serviceId +":"+methodName + " --> DaoRemoveHandler", e);
			throw new DaoException(e, "Remove Object Fail!");
		}
		return result;
	}
}
