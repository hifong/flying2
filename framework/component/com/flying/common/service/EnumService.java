package com.flying.common.service;

import java.util.List;

import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.ConstantEnum;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.model.QueryResult;
import com.flying.framework.service.AbstractService;

@Service("EnumService")
public class EnumService extends AbstractService {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@MethodInfo("查询枚举数据")
	public QueryResult<ConstantModel> findEnums(
			@Param(value = "class", required = true, desc = "枚举类名") String className)
			throws Exception {
		Class<? extends ConstantEnum> c = (Class<? extends ConstantEnum>)module.getClassLoader().loadClass(className);
		ConstantEnum[] enums = c.getEnumConstants();
		List<ConstantModel> list = Utils.newArrayList();
		for(ConstantEnum<?> con: enums) {
			list.add(new ConstantModel(con));
		}
		return ModelFactory.createModelInstance(QueryResult.class, Codes.ROWS, list);
	}

	@SuppressWarnings("serial")
	public class ConstantModel extends Data {
		private final String text;
		private final String value;

		@SuppressWarnings("rawtypes")
		public ConstantModel(ConstantEnum c) {
			this.text = c.text();
			this.value = c.value().toString();
		}

		public String getText() {
			return text;
		}

		public String getValue() {
			return value;
		}
	}
}
