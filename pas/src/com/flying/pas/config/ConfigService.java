package com.flying.pas.config;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

@Service("ConfigService")
public class ConfigService  extends AbstractService{

	@DaoCreate(entity="config.config")
	public Data create(
			@Param(value="category_code", required=true)String category_code,
			@Param(value="name", required=true)String name,
			@Param(value="config_key", required=true)String config_key,
			@Param(value="config_value")String config_value,
			@Param(value="remarks")String remarks) throws Exception {
		return null;
	}

	@DaoUpdate(entity="config.config")
	public Data update(
			@Param(value="category_code", required=true)String category_code,
			@Param(value="name",required=true)String name,
			@Param(value="config_key", required=true)String config_key,
			@Param(value="config_value")String config_value,
			@Param(value="remarks")String remarks,
			@Param(value="config_id",required=true)long config_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="config.config")
	public Data remove(
			@Param(value="config_id",required=true)long config_id) throws Exception {
		return null;
	}

	@DaoQuery(entity="config.config")
	public Data findAll(Data request) throws Exception {
		return request;
	}
}
