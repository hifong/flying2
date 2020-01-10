package com.flying.common.annotation.handler;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.helper.SequenceHelper;
import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.metadata.Field;
import com.flying.framework.metadata.MetaUtils;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceDescriptor.ServiceMethodParameter;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class DaoCreateHandler implements ServiceHandler<DaoCreate> {
	private final static Logger log = Logger.getLogger(DaoCreateHandler.class);
	private final static Logger sqllog = Logger.getLogger("sql");
	
	@Override
	public Data handle(DaoCreate annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		
		Data result = null;

		if (annotation.position() == DaoCreate.Position.after_body) {
			result = context.doChain(request);
			request.merge(result, true);
			if (result != null && result.contains(Codes.CODE))
				return result;
		}
		if(result == null) result = ModelFactory.createModelInstance(module, context.getReturnType());
		
		//
		final Metadata metadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final List<Field> fields = metadata.getFields();
		final List<ServiceMethodParameter> methodParams = context.getMethodParams();
		
		final StringBuffer entitySql = new StringBuffer("INSERT INTO " + metadata.getTable() + " ( ");
		final List<String> params = Utils.newArrayList();
		for(ServiceMethodParameter mp: methodParams) {			//fields from method params
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;
			Field pf = metadata.getField(mp.getParam().value());
			if(pf == null) {
				throw new AppException("1", "Metadata error, field " + mp.getParam().value() +" not found in "+ annotation.entity()+"!");
			}
			if(pf.isAutoIncrement()) {	//自动增量类型主键
				continue;
			}
			if(!params.isEmpty()) entitySql.append(",");
			entitySql.append(mp.getParam().value());
			params.add(mp.getParam().value());
		}
		if(params.isEmpty()) {						//fields from entity
			for(Field f: fields) {
				String name = f.getName();
				
				if(!params.isEmpty()) entitySql.append(",");
				entitySql.append(name);
				params.add(name);
			}
		}
		Field autoIncreamentField = null;
		for(String pk: metadata.getPrimaryKeys()) {
			Field pf = metadata.getField(pk);
			if(pf.isAutoIncrement()) {				//自动增量类型主键，删除
				autoIncreamentField = pf;
				if(params.contains(pk)) params.remove(pk);
				continue;
			}
			if(!params.contains(pk)) {
				if(!params.isEmpty()) entitySql.append(",");
				entitySql.append(pk);
				params.add(pk);
			}
		}
		entitySql.append(" ) VALUES ( ");
		for(int i=0; i< params.size(); i++) {
			if(i > 0) entitySql.append(",");
			entitySql.append(" ? ");
		}
		entitySql.append(" ) ");
		//
		final String configSql = module.getModuleConfig().getServiceConfig(serviceId).getConfig("sql." + methodName);
		final String sql = !StringUtils.isEmpty(annotation.sql()) ? annotation.sql(): !StringUtils.isEmpty(configSql)?configSql: entitySql.toString();
		
		final Object[] values = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			Map<String, Object> field = null;
			for(Map<String, Object> f: fields) {
				if(StringUtils.equalsIgnoreCase((String)f.get("name"), params.get(i))) {
					field = f;
					break;
				}
			}
			//
			final String generator = (String)field.get("generator");
			final String defaultValue = (String)field.get("default-value");
			Object val = request.get(params.get(i));
			if(!StringUtils.isEmpty(generator)) {
				val = SequenceHelper.nextVal(module, generator);
			}
			if(val != null && val.getClass().isArray()) {
				val = Array.get(val, 0);
			}
			if(val == null) {
				val = defaultValue;
			}
			values[i] = val;
		}
		//
		sqllog.info(serviceId+":"+methodName+"-->[" + sql+"] with [" + StringUtils.join(values, ",") +"]");
		//
		try {
			JdbcTemplate jdbcTemplate = (JdbcTemplate) module.getSpringBeanFactory().getBean(annotation.connectionTag());
			if(autoIncreamentField != null) {
				KeyHolder key=new GeneratedKeyHolder();
				final int res = jdbcTemplate.update(new PreparedStatementCreator(){
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
						PreparedStatement pstmt=con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						for(int i=0; i< values.length; i++) {
							pstmt.setObject(i + 1, values[i]);
						}
						return pstmt;
					}

				},key);
				result.putAll(Codes.CODE, Codes.SUCCESS, Codes.EFFECT_ROWS, res, autoIncreamentField.getName(), key.getKey().longValue());
			} else {
				final int res = jdbcTemplate.update(sql, values);
				result.putAll(Codes.CODE, Codes.SUCCESS, Codes.EFFECT_ROWS, res);
			}
			//
			for(int i=0; i< params.size(); i++) {
				result.put(params.get(i), values[i]);
			}
			//
			if (annotation.position() == DaoCreate.Position.before_body) {
				result.putAll(context.doChain(request));
				request.merge(result, true);
			}
		}catch (Exception e) {
			log.error(serviceId +":"+methodName + " --> DaoCreateHandler; " + e);
			throw new DaoException(e, module.getId()+":"+serviceId);
		}
		return MetaUtils.mapping(result, metadata);
	}
}
