package com.flying.pas.config;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

@Service("ConfigCategoryService")
public class CategoryService  extends AbstractService{

	@DaoCreate(entity="config.category")
	public Data create(
			@Param(value="name", required=true)String name,
			@Param(value="code", required=true)String code,
			@Param(value="remarks",required=true)String remarks) throws Exception {
		return null;
	}

	@DaoUpdate(entity="config.category")
	public Data update(
			@Param(value="name",required=true)String name,
			@Param(value="code",required=true)String code,
			@Param(value="remarks",required=true)String remarks,
			@Param(value="category_id",required=true)long category_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="config.category")
	public Data remove(
			@Param(value="category_id",required=true)long category_id) throws Exception {
		return null;
	}

	@DaoQuery(entity="config.category")
	public Data findAll(Data request) throws Exception {
		return request;
	}

}
