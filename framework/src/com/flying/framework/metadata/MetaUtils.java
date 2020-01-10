package com.flying.framework.metadata;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.flying.framework.data.Data;

public final class MetaUtils {
	public static Map<String, Object> mapping(Map<String, Object> input, Metadata metadata) {
		final List<Field> fields = metadata.getFields();
		for(Field f: fields) {
			final String fn = f.getName();
			final String alias = StringUtils.isEmpty(f.getAlias())? fn: f.getAlias();
			if(!StringUtils.equals(fn, alias))
				input.put(alias, input.remove(fn));
		}
		return input;
	}
	
	public static Data mapping(Data input, Metadata metadata) {
		final List<Field> fields = metadata.getFields();
		for(Field f: fields) {
			final String fn = f.getName();
			final String alias = StringUtils.isEmpty(f.getAlias())? fn: f.getAlias();
			if(!StringUtils.equals(fn, alias))
				input.put(alias, input.remove(fn));
		}
		return input;
	}
}
