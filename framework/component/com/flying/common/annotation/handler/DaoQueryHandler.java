package com.flying.common.annotation.handler;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flying.common.annotation.DaoQuery;
import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.Utils;
import com.flying.framework.config.ServiceConfig;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.exception.ObjectNotFoundException;
import com.flying.framework.metadata.Field;
import com.flying.framework.metadata.MetaUtils;
import com.flying.framework.metadata.Metadata;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceDescriptor.ServiceMethodParameter;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

public class DaoQueryHandler implements ServiceHandler<DaoQuery> {
	private final static Logger log = Logger.getLogger(DaoQueryHandler.class);
	private final static Logger sqllog = Logger.getLogger("sql");
	
	private ServiceConfig serviceConfig;

	public void setServiceConfig(ServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Data handle(DaoQuery annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		
		Data result = null;

		if (annotation.position() == DaoQuery.Position.after_body) {
			result = context.doChain(request);
			request.merge(result, true);
			if (result != null && result.contains(Codes.CODE))
				return result;
		}
		if(result == null) result = ModelFactory.createModelInstance(module, context.getReturnType());
		
		JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getModule().getSpringBeanFactory().getBean(annotation.connectionTag());
		//
		final Metadata viewMetadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final List<ServiceMethodParameter> methodParams = context.getMethodParams();
		
		final int page = request.getInt("page", 0) < 0? 0: request.getInt("page", 0);
		int size = request.getInt("rows", this.getDefaultPageSize(methodParams));
		size = (size == 0? 10:size);
		//buildSQLAndParams
		QueryObject query = this.buildSqlAndParams(annotation, request, context);
		//build Params
		final Object[] queryParams = new Object[query.params.size() + (query.pageable?2:0)];
		final Object[] countParams = new Object[query.params.size()];
		for(int i=0; i < query.params.size(); i++) {
			queryParams[i] = request.get(query.params.get(i));
			countParams[i] = request.get(query.params.get(i));
		}
		if(annotation.pageable() && !annotation.single()) {
			queryParams[queryParams.length - 2] = page * size;
			queryParams[queryParams.length - 1] = size;
		}
		//
		try {
			sqllog.info(serviceId+":"+methodName+"-->[" + query.querySql+"] with [" + StringUtils.join(queryParams, ",") +"]");
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(query.querySql.toString(), queryParams);
			this.processServiceField(annotation, viewMetadata, rows);
			//
			if (annotation.single()) {
				if(rows.isEmpty())  {
					if(annotation.throwsNotFoundException()) {
						throw new ObjectNotFoundException(annotation.entity(), StringUtils.join(queryParams, ","));
					} else {
						result = ModelFactory.createModelInstance(module, context.getReturnType());
						result.put(Codes.CODE, Codes.OBJECT_NOT_EXISTS);
					}
				} else {
					this.mapToMetadata(viewMetadata, rows);
					result = ModelFactory.createModelInstance(module, context.getReturnType());
					result.put(Codes.CODE, Codes.SUCCESS);
					result.putAll((Map<String, Object>) (rows.get(0)));
				}
			} else {
				final int total;
				if(StringUtils.isEmpty(query.countSql) || !annotation.pageable() || annotation.single()){
					total = rows.size();
				} else {
					sqllog.info(serviceId+":"+methodName+"-->[" + query.countSql+"] with [" + StringUtils.join(countParams, ",") +"]");
					total = jdbcTemplate.queryForInt(query.countSql, countParams);
				}
				result = ModelFactory.createModelInstance(module, context.getReturnType());
				result.put(Codes.CODE, Codes.SUCCESS);
				result.put(Codes.TOTAL_ROWS, total);
				result.merge(request, false);
				
				int pageCount = total % size == 0?total/size: (total/size + 1);
				result.put("pageNum", page + 1);
				result.put("pageSize", size);
				result.put("pageCount", pageCount);
				//
				Class<? extends Data> modelClass = annotation.modelClass() == Object.class? null: 
					(Class<? extends Data>)annotation.modelClass();
				if(modelClass == null) {
					result.put(annotation.resultsets(), this.mapToMapObject(module, viewMetadata, rows));
				} else {
					List<? extends Data> modelRows = this.mapToModelRows(module, viewMetadata, rows, modelClass);
					result.put(annotation.resultsets(), modelRows);
				}
			}
			if(viewMetadata != null && this.serviceConfig.getConfigs().getBoolean("showMetadata", false))
				result.put("metadata", viewMetadata.getFields());
			if (annotation.position() == DaoQuery.Position.before_body) {
				result.putAll(context.doChain(request));
				request.merge(result, true);
			}
		} catch (Exception e) {
			if(e instanceof AppException) {
				throw (AppException)e;
			}
			log.error(serviceId +":"+methodName + " --> DaoQueryHandler", e);
			result = ModelFactory.createModelInstance(module, context.getReturnType());
			result.put(Codes.CODE, Codes.FAIL);
			result.put(Codes.MSG, e.getMessage());
		}
		return result;
	}

	private void mapToMetadata(Metadata viewMetadata, List<Map<String, Object>> rows) throws Exception {
		for(Map<String, Object> row: rows) {
			MetaUtils.mapping(row, viewMetadata);
		}
	}

	private <T extends Data> List<T> mapToModelRows(LocalModule module, Metadata viewMetadata, List<Map<String, Object>> rows, Class<T> modelClass) throws Exception {
		List<T> result = Utils.newArrayList();
		for(Map<String, Object> row: rows) {
			T item = ModelFactory.createModelInstance(module, modelClass, row);
			result.add(item);
		}
		return result;
	}
	
	private List<Map<String, Object>> mapToMapObject(LocalModule module, Metadata viewMetadata, List<Map<String, Object>> rows) {
		if(rows == null) return null;
		List<Field> fields = viewMetadata.getAliasField();
		for(Map<String, Object> row: rows) {
			for(Field f: fields) {
				row.put(f.getAlias(), row.get(f.getName()));
			}
		}
		return rows;
	}
	
	private void processServiceField(DaoQuery annotation, Metadata viewMetadata, List<Map<String, Object>> rows) throws Exception {
		final List<Field> serviceFields = viewMetadata.getServiceFields();
		
		if(serviceFields.isEmpty() || rows == null || rows.isEmpty()) return ;

		for(Map<String, Object> row: rows) {
			for(Field f: serviceFields) {
				//invoke service
				@SuppressWarnings("unchecked")
				Map<String, Object> serviceParamsMap = (Map<String, Object>)f.get(Field.SERVICE_PARAMS);
				Data invokeParams = new Data();
				for(String pn: serviceParamsMap.keySet()) {
					Object pv = serviceParamsMap.get(pn);
					if(pn instanceof String && ((String)pv).startsWith("$")) {
						pv = row.get(((String)pv).substring(1));
					}
					invokeParams.put(pn, pv);
				}
				Data serviceFieldData = ServiceHelper.invoke(f.getModuleId(), f.getServiceId(), invokeParams);
				//如果结果返回的是集合，则字段值也为集合，反之，如果范围单对象，结果也为单对象；
				//如果定义了值字段，对于集合返回集合中每个值字段结果的集合，对于单对象，返回单对象的属性呢
				final String valueField = (String)f.get(Field.SERVICE_VALUE_FIELD);
				final boolean mergeProperty = "true".equalsIgnoreCase((String)f.get("mergeProperty"));
				if(serviceFieldData.contains(annotation.resultsets())) {
					List<Map<String, Object>> drows = serviceFieldData.get(annotation.resultsets());
					if(StringUtils.isEmpty(valueField)) {
						row.put(f.getName(), drows);
					} else {
						List<Object> list = Utils.newArrayList();
						for(Map<String, Object> dr: drows) {
							Object o = dr.get(valueField);
							if(o != null)
								list.add(o);
						}
						row.put(f.getName(), list);
					}
				} else {
					serviceFieldData.remove(Codes.CODE);
					if(mergeProperty) {
						if(StringUtils.isEmpty(valueField) || "*".equals(valueField.trim())) {
							row.putAll(serviceFieldData.getValues());
						} else {
							if(valueField.indexOf(",") > 0) {
								String[] valueFields = StringUtils.split(valueField, ",");
								Map<String, Object> v = Utils.newHashMap();
								for(String vf: valueFields) {
									v.put(vf, serviceFieldData.get(vf));
								}
								row.putAll(v);
							} else {
								row.put(valueField, serviceFieldData.get(valueField));
							}
						}
						
					} else {
						if(StringUtils.isEmpty(valueField) || "*".equals(valueField.trim())) {
							row.put(f.getName(), serviceFieldData);
						} else {
							if(valueField.indexOf(",") > 0) {
								String[] valueFields = StringUtils.split(valueField, ",");
								Map<String, Object> v = Utils.newHashMap();
								for(String vf: valueFields) {
									v.put(vf, serviceFieldData.get(valueField));
								}
								row.put(f.getName(), v);
							} else {
								row.put(f.getName(), serviceFieldData.get(valueField));
							}
						}
					}	// end asProperty
				}
			}
		}
	}
	
	private int getDefaultPageSize(List<ServiceMethodParameter> methodParams) {
		for(ServiceMethodParameter mp: methodParams) {
			if(mp.getParam() != null && "rows".equalsIgnoreCase(mp.getParam().value())) {
				return 15;	//方法有rows参数，使用15默认
			}
		}
		return 10000;	//对于没有携带rows参数，默认提取所有数据（暂定10000条）
	}
	
	private QueryObject buildSqlAndParams(DaoQuery annotation, Data request, ServiceHandlerContext context) {
		final LocalModule module = context.getModule();
		final String serviceId = context.getServiceId();
		final String methodName = context.getMethodName();
		final List<ServiceMethodParameter> methodParams = context.getMethodParams();
		
		final Metadata viewMetadata = module.getMetadataRepository().getMetadata(annotation.entity());
		final Metadata tableMetadata = module.getMetadataRepository().getMetadataAllowNull(viewMetadata.getTable());

		final String qsql = StringUtils.isEmpty(annotation.qsql()) ? module.getModuleConfig().getServiceConfig(serviceId).getConfig("qsql." + methodName) : annotation.qsql();
		final String csql = StringUtils.isEmpty(annotation.csql()) ? module.getModuleConfig().getServiceConfig(serviceId).getConfig("csql." + methodName) : annotation.csql();
		
		//find reference columns
		StringBuffer rsql = new StringBuffer();
		for(Field f: viewMetadata.getFields()) {
			if(Field.DICTIONARY_TYPE.equalsIgnoreCase((String)f.get("type"))) {
				rsql.append(",");
				final String fieldName 			= f.getName();
				final String referenceEntity 	= f.getRefEntity();
				final String refValueField 		= f.getValueField();
				final String refRelField 		= f.getRefRelField();
				final String relField 			= f.getRelField();
				Metadata rmd = module.getMetadataRepository().getMetadata(referenceEntity);
				rsql.append(" (SELECT f.").append(refValueField).append(" FROM ").append(rmd.getTable()).append(" f ");
				rsql.append(" WHERE f.").append(refRelField).append("=t.").append(relField).append(") AS ").append(fieldName);
			}
		}
		//find fields
		StringBuffer fsql = new StringBuffer();
		for(Field f: viewMetadata.getSimpleFields()) {
			if(fsql.length() > 0) fsql.append(",");
			fsql.append("t.").append(f.getName());
		}
		//
		final StringBuffer querySql = new StringBuffer( StringUtils.isEmpty(qsql) ? ("SELECT "+ fsql + rsql+"   FROM " + viewMetadata.getTable() +" t "): qsql );
		final StringBuffer countSql = new StringBuffer( StringUtils.isEmpty(csql) ? ("SELECT COUNT(1) AS c FROM " + viewMetadata.getTable()): csql );

		int conditionCount = 0;
		if(!StringUtils.isEmpty(annotation.wsql())) {
			querySql.append(" WHERE ").append(annotation.wsql());
			countSql.append(" WHERE ").append(annotation.wsql());
			conditionCount ++;
		}
		
		final List<String> params = Utils.newArrayList();
		for(ServiceMethodParameter mp: methodParams) {			//fields FROM method params
			if(mp.getParam() == null || mp.getParameter().getType() == Data.class) continue;				//忽略Data类型参数
			if(viewMetadata.getField(mp.getParam().value()) == null && 
					tableMetadata != null && tableMetadata.getField(mp.getParam().value()) == null) continue;	//对应表不存在该字段，忽略
			if(StringUtils.isEmpty(request.getString(mp.getParam().value()))) continue;						//请求中的数据为空，忽略
			
			if(StringUtils.isEmpty(annotation.wsql()) && conditionCount == 0) {
				querySql.append(" WHERE ");
				countSql.append(" WHERE ");
			}
			//
			if("BETWEEN".equalsIgnoreCase(mp.getParam().tag())) {
				if(conditionCount > 0){
					querySql.append(" AND ");
					countSql.append(" AND ");
				}
				conditionCount ++;
				//
				final Object value = request.get(mp.getParam().value());
				Object from = null;
				Object to = null;
				if(value.getClass().isArray() && Array.getLength(value) == 2) {
					from = Array.get(value, 0);
					to = Array.get(value, 1);
					request.put(mp.getParam().value()+"_from", from);
					request.put(mp.getParam().value()+"_to", to);
				}
				if(from == null || to == null) {
					throw new java.lang.IllegalArgumentException("Bewteen args's length must be 2 and can not be null!");
				}
				querySql.append(mp.getParam().value()).append(" BETWEEN ").append("? AND ? ");
				countSql.append(mp.getParam().value()).append(" BETWEEN ").append("? AND ? ");
				params.add(mp.getParam().value()+"_from");
				params.add(mp.getParam().value()+"_to");
			} else if("in".equalsIgnoreCase(mp.getParam().tag())) {
				if(conditionCount > 0){
					querySql.append(" AND ");
					countSql.append(" AND ");
				}
				conditionCount ++;
				//
				Object value = request.get(mp.getParam().value());
				querySql.append(mp.getParam().value()).append(" IN ").append(" (").append(join(value)).append(") ");
				countSql.append(mp.getParam().value()).append(" IN ").append(" (").append(join(value)).append(") ");
				//params.add(mp.getParam().value());
			} else if("replace".equalsIgnoreCase(mp.getParam().tag())) {
				final String name = mp.getParam().value();
				final String value = request.getString(mp.getParam().value());
				
				int start = querySql.indexOf(":" + name);
				while(querySql.indexOf(":"+name) >0){
					querySql.replace(start, start + (":"+name).length(), value);
					start = querySql.indexOf(":" + name);
				}
				//
				start = countSql.indexOf(":" + name);
				while(countSql.indexOf(":"+name) >0){
					countSql.replace(start, start + (":"+name).length(), value);
					start = countSql.indexOf(":" + name);
				}
			} else {
				if(conditionCount > 0){
					querySql.append(" AND ");
					countSql.append(" AND ");
				}
				conditionCount ++;
				//
				querySql.append(mp.getParam().value()).append(" ").append(StringUtils.isEmpty(mp.getParam().tag()) ? "=":mp.getParam().tag()).append(" ? ");
				countSql.append(mp.getParam().value()).append(" ").append(StringUtils.isEmpty(mp.getParam().tag()) ? "=":mp.getParam().tag()).append(" ? ");
				params.add(mp.getParam().value());
			}
		}
		//
		boolean pageable = annotation.pageable() && !annotation.single();
		//
		if(!StringUtils.isEmpty(annotation.osql()))
			querySql.append(" ").append(annotation.osql());
		if(!StringUtils.isEmpty(annotation.gsql()))
			querySql.append(" ").append(annotation.gsql());
		if(pageable)
			querySql.append(" limit ?, ? ");
		
		return new QueryObject(querySql.toString(), countSql.toString(), params, pageable);
	}
	
	@SuppressWarnings("rawtypes")
	private String join(Object obj) {
		StringBuffer sb = new StringBuffer();
		if(obj.getClass().isArray()) {
			for(int i=0; i< Array.getLength(obj); i++) {
				if(i > 0) sb.append(",");
				Object o = Array.get(obj, i);
				if(o instanceof Integer || o instanceof Long || o instanceof Character || o instanceof Byte) {
					sb.append(o);
				} else {
					sb.append("'").append(o).append("'");
				}
			}
		} else if(obj instanceof Collection) {
			Collection coll = (Collection)obj;
			int i = 0;
			for(Iterator it = coll.iterator(); it.hasNext(); ) {
				//
				if(i > 0) sb.append(",");
				Object o = it.next();
				if(o instanceof Integer || o instanceof Long || o instanceof Character || o instanceof Byte) {
					sb.append(o);
				} else {
					sb.append("'").append(o).append("'");
				}
				//
				i ++;
			}
		} else {
			if(obj instanceof Integer || obj instanceof Long || obj instanceof Character || obj instanceof Byte) {
				sb.append(obj);
			} else {
				sb.append("'").append(obj).append("'");
			}
		}
		return sb.toString();
	}
	
	class QueryObject {
		private final String querySql;
		private final String countSql;
		private final List<String> params;
		private final boolean pageable;
		
		QueryObject(String querySql, String countSql, List<String> params, boolean pageable) {
			this.querySql = querySql;
			this.countSql = countSql;
			this.params = params;
			this.pageable = pageable;
		}
	}
}
