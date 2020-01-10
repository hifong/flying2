package com.flying.common.annotation.handler;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.annotation.DaoUpdate;
import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.Data;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceDescriptor.ServiceMethodParameter;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class DaoUpdateHandler implements ServiceHandler<DaoUpdate> {
	private final static Logger log = Logger.getLogger(DaoUpdateHandler.class);
	private final static Logger sqllog = Logger.getLogger("sql");
	
	@Override
	public Data handle(DaoUpdate annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		
		Data result = null;

		if (annotation.position() == DaoUpdate.Position.after_body) {
			result = context.doChain(request);
			request.merge(result, true);
			if (result != null && result.contains(Codes.CODE))
				return result;
		}
		if(result == null) result = ModelFactory.createModelInstance(module, context.getReturnType());
		//
		final Metadata metadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final List<ServiceMethodParameter> methodParams = context.getMethodParams();
		
		final List<MethodParam> updateParams = Utils.newArrayList();
		final List<MethodParam> whereParams = Utils.newArrayList();
		
		final Set<String> fields = Utils.newHashSet();
		for(ServiceMethodParameter mp: methodParams) {			//搜索修改字段
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;
			if("fields".equalsIgnoreCase(mp.getParam().tag())) {
				Object fv = request.get(mp.getParam().value());
				String[] fs;
				if(fv instanceof String) {
					fs = StringUtils.split(request.getString(mp.getParam().value()), ';');
				} else {
					fs = request.getStrings(mp.getParam().value());
				}
				if(fs != null) {
					for(String f: fs) fields.add(f);
				}
			}
		}
		
		for(ServiceMethodParameter mp: methodParams) {			//fields from method params
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;
			if(metadata.getField(mp.getParam().value()) == null) continue;
			
			if(!StringUtils.isEmpty(mp.getParam().tag()) || metadata.isPrimaryKey(mp.getParam().value())) {
				whereParams.add(new MethodParam(mp.getParam(), request.get(mp.getParam().value())));
			} else {
				if(!fields.isEmpty() && !fields.contains(mp.getParam().value())) continue;	//如果指定了字段，并且操作字段不在指定字段之内，则忽略
				updateParams.add(new MethodParam(mp.getParam(), request.get(mp.getParam().value())));
			}
		}
		//
		final String configSql = module.getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName);
		final String sql;
		if(StringUtils.isEmpty(annotation.sql()) && StringUtils.isEmpty(configSql)) {	//build entity sql
			final StringBuffer entitySql = new StringBuffer("UPDATE " + metadata.getTable() + " SET ");
			int c = 0;
			for(MethodParam mp: updateParams) {
				if(c > 0) entitySql.append(" , ");
				entitySql.append(mp.param.value()).append("=?");
				c ++;
			}
			//
			c = 0;
			if(!StringUtils.isEmpty(annotation.wsql())) {
				c ++;
				entitySql.append(" WHERE ");
				entitySql.append(" ").append(annotation.wsql()).append(" ");
			}
			for(MethodParam mp: whereParams) {
				if(c == 0) {
					entitySql.append(" WHERE ");
				} else {
					entitySql.append(" AND ");
				}
				if(StringUtils.isEmpty(mp.param.tag())) {
					entitySql.append(mp.param.value()).append("=? ");
				} else {
					entitySql.append(mp.param.value()).append(" ").append(mp.param.tag()).append(" ? ");
				}
				c ++;
			}
			sql = entitySql.toString();
		} else {
			sql = !StringUtils.isEmpty(annotation.sql()) ? annotation.sql():configSql;
		}
		//
		final Object[] values = new Object[updateParams.size() + whereParams.size()];
		for (int i = 0; i < updateParams.size() + whereParams.size(); i++) {
			Object val = request.get(i < updateParams.size() ? updateParams.get(i).param.value(): whereParams.get(i - updateParams.size()).param.value());
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
			if(annotation.maxEffectRows() > 0 && effectRows > annotation.maxEffectRows()) {
				throw new DaoException("Just allowed effect "+annotation.maxEffectRows()+" rows, but " + effectRows +" rows effected!");
			}
			//
			result = ModelFactory.createModelInstance(module, context.getReturnType());
			result.put(Codes.CODE, effectRows > 0? Codes.SUCCESS: Codes.OBJECT_NOT_EXISTS);
			result.put(Codes.EFFECT_ROWS, effectRows);
			
			for(int i=0; i< updateParams.size(); i++) {
				result.put(updateParams.get(i).param.value(), values[i]);
			}
			for(int i=0; i< whereParams.size(); i++) {
				result.put(whereParams.get(i).param.value(), values[i + updateParams.size()]);
			}
			//
			if (annotation.position() == DaoUpdate.Position.before_body) {
				result.putAll(context.doChain(request));
				request.merge(result, true);
			}
		} catch (Exception e) {
			log.error(serviceId +":"+methodName + " --> DaoUpdateHandler", e);
			throw new DaoException(e, "Update object fail!");
		}
		return result;
	}
	
	class MethodParam{
		Param param;
		Object value;
		
		public MethodParam(Param param, Object value) {
			this.param = param;
			this.value = value;
		}
	}
}
