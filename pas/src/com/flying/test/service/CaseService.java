package com.flying.test.service;

import java.util.Date;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

import com.flying.test.model.Case;

@Service(value="CaseService", desc="")
public class CaseService extends AbstractService{
        
	@MethodInfo("创建")
	@DaoCreate(entity=Case.ENTITY_NAME)
	public Data createCase(
			Case test_case
		
		) throws Exception {
		
		return null;
	}
	
	@MethodInfo("修改")
	@DaoUpdate(entity=Case.ENTITY_NAME)
	public Data updateCase(
			Case test_case
		
		) throws Exception {
		
		return null;
	}
        
	@MethodInfo("创建")
	@DaoCreate(entity=Case.ENTITY_NAME)
	public Data create(
		
			@Param(value=Case.CASE_ID, required=true,	desc="CASE ID") Integer case_id,
			@Param(value=Case.NAME, required=false,	desc="用例名称") String name,
			@Param(value=Case.REMARKS, required=false,	desc="用例说明") String remarks,
			@Param(value=Case.SERVICE, required=false,	desc="执行接口") String service,
			@Param(value=Case.INPUT, required=false,	desc="计划输入") String input,
			@Param(value=Case.OUTPUT, required=false,	desc="期望输出") String output
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Case.ENTITY_NAME)
	public Data update(
		
			@Param(value=Case.CASE_ID, required=true,	desc="CASE ID") Integer case_id,
			@Param(value=Case.NAME, required=false,	desc="用例名称") String name,
			@Param(value=Case.REMARKS, required=false,	desc="用例说明") String remarks,
			@Param(value=Case.SERVICE, required=false,	desc="执行接口") String service,
			@Param(value=Case.INPUT, required=false,	desc="计划输入") String input,
			@Param(value=Case.OUTPUT, required=false,	desc="期望输出") String output,
			@Param(value="fields", required=false, desc="更新字段", tag="fields") String[] fields
			
		) throws Exception {
		
		return null;
	}

	@MethodInfo("删除")
	@DaoRemove(entity=Case.ENTITY_NAME)
	public Data remove(
		
			@Param(value=Case.CASE_ID, required=true,	desc="CASE ID") Integer case_id
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("查询所有")
	@DaoQuery(entity=Case.ENTITY_NAME, pageable=true)
	public Data findAll(
			@Param(value="page", 	required=false, desc="分页页号，默认0") int page,
			@Param(value="rows", 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return null;
	}

	@MethodInfo("根据主键查询")
	@DaoQuery(entity=Case.ENTITY_NAME, single=true)
	public Data findById(
		
			@Param(value=Case.CASE_ID, required=true,	desc="CASE ID") Integer case_id
		
		) throws Exception {
		return null;
	}

}