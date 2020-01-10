package com.flying.framework.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.flying.common.util.Utils;
import com.flying.framework.annotation.ConstantEnum;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Property;
import com.flying.framework.annotation.scanner.RegisteredAnnotationHandler;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;

public class MetaAnnotationHandler implements RegisteredAnnotationHandler<Meta> {

	@Override
	public void handle(LocalModule module, Meta annotation, Class<?> cls) {
		Map<String, Object> data = Utils.newHashMap();
		if(!StringUtils.isEmpty(annotation.entity()))
			data.put(Metadata.ENTITY, annotation.entity());
		if(annotation.primaryKeys() != null && annotation.primaryKeys().length > 0) {
			StringBuilder sb = new StringBuilder(annotation.primaryKeys()[0]);
			for(int i=1; i< annotation.primaryKeys().length - 1; i++) {
				sb.append(",").append(annotation.primaryKeys()[i]);
			}
			data.put(Metadata.PRIMARY_KEY, sb.toString());
		}
		data.put(Metadata.TITLE, annotation.title());
		data.put(Metadata.TABLE_NAME, annotation.table());
		if(annotation.props() != null && annotation.props().length > 0) {
			for(Property p: annotation.props()) {
				if(!data.containsKey(p.name())) {
					data.put(p.name(), p.value());
				}
			}
		}
		//
		data.put(Metadata.FIELDS, toFieldsData(cls));
		module.getMetadataRepository().buildMetadata(annotation.id(), data);
	}
	
	private List<Map<String, Object>> toFieldsData(Class<?> cls) {
		java.lang.reflect.Field[] fields = cls.getDeclaredFields();
		List<Map<String, Object>> fieldList = Utils.newArrayList();
		for(java.lang.reflect.Field field:fields) {
			if(Collection.class.isAssignableFrom(field.getType()) || Data.class.isAssignableFrom(field.getType())) continue;
			Param param = field.getAnnotation(Param.class);
			if(param != null) {
				fieldList.add(toFieldData(field,param));
			}
		}
		return fieldList;
	}

	private Map<String, Object> toFieldData(java.lang.reflect.Field field, Param param) {
		Map<String, Object> data = Utils.newHashMap();
		data.put(Field.FIELD_NAME, param.value());
		data.put(Field.ALIAS, StringUtils.isEmpty(param.alias())? field.getName(): param.alias());
		data.put(Field.TITLE, param.desc());
		data.put(Field.DATA_TYPE, field.getType().getSimpleName());
		if(!StringUtils.isEmpty(param.generator())) {
			data.put(Field.GENERATOR, param.generator());
		}
		if(param.enumClass() != ConstantEnum.class) {
			@SuppressWarnings("rawtypes")
			ConstantEnum[] enums = param.enumClass().getEnumConstants();
			List<Map<String, Object>> options = Utils.newArrayList();
			for(ConstantEnum e: enums) {
				Map<String, Object> option = Utils.newHashMap();
				option.put(Field.OPTION_TEXT_FIELD, e.text());
				option.put(Field.OPTION_VALUE_FIELD, e.value());
				options.add(option);
			}
			data.put(Field.FIELD_OPTIONS, options);
		}
		if(param.props() != null && param.props().length > 0) {
			for(Property p: param.props()) {
				if(!data.containsKey(p.name())) {
					data.put(p.name(), p.value());
				}
			}
		}
		return data;
	}
}
