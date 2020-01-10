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

import com.flying.test.model.Record;

@Service(value="RecordService", desc="")
public class RecordService extends AbstractService{
        
	@MethodInfo("创建")
	@DaoCreate(entity=Record.ENTITY_NAME)
	public Data createRecord(
			Record test_record
		
		) throws Exception {
		
		return null;
	}
	
	@MethodInfo("修改")
	@DaoUpdate(entity=Record.ENTITY_NAME)
	public Data updateRecord(
			Record test_record
		
		) throws Exception {
		
		return null;
	}
        
	@MethodInfo("创建")
	@DaoCreate(entity=Record.ENTITY_NAME)
	public Data create(
		
			@Param(value=Record.RECORD_ID, required=true,	desc="RECORD ID") Integer record_id,
			@Param(value=Record.FLOW_INSTANCE_ID, required=false,	desc="流程实例ID") Integer flow_instance_id,
			@Param(value=Record.FLOW_NODE_INSTANCE_ID, required=false,	desc="流程环节实例ID") Integer flow_node_instance_id,
			@Param(value=Record.CASE_ID, required=false,	desc="用例ID") Integer case_id,
			@Param(value=Record.SERVICE, required=false,	desc="执行接口") String service,
			@Param(value=Record.STATE, required=false,	desc="执行状态（0：未执行；1：已执行）") Integer state,
			@Param(value=Record.CREATE_DATE, required=false,	desc="创建时间") Date create_date,
			@Param(value=Record.PLAN_EXECUTE_DATE, required=false,	desc="计划执行时间") Date plan_execute_date,
			@Param(value=Record.ACTUAL_EXECUTE_DATE, required=false,	desc="实际执行时间") Date actual_execute_date,
			@Param(value=Record.CONSUME, required=false,	desc="时长（毫秒）") Integer consume,
			@Param(value=Record.INPUT, required=false,	desc="实际输入") String input,
			@Param(value=Record.OUTPUT, required=false,	desc="实际输出") String output,
			@Param(value=Record.RESULT, required=false,	desc="执行结果（0：失败；1：成功）") Integer result
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Record.ENTITY_NAME)
	public Data update(
		
			@Param(value=Record.RECORD_ID, required=true,	desc="RECORD ID") Integer record_id,
			@Param(value=Record.FLOW_INSTANCE_ID, required=false,	desc="流程实例ID") Integer flow_instance_id,
			@Param(value=Record.FLOW_NODE_INSTANCE_ID, required=false,	desc="流程环节实例ID") Integer flow_node_instance_id,
			@Param(value=Record.CASE_ID, required=false,	desc="用例ID") Integer case_id,
			@Param(value=Record.SERVICE, required=false,	desc="执行接口") String service,
			@Param(value=Record.STATE, required=false,	desc="执行状态（0：未执行；1：已执行）") Integer state,
			@Param(value=Record.CREATE_DATE, required=false,	desc="创建时间") Date create_date,
			@Param(value=Record.PLAN_EXECUTE_DATE, required=false,	desc="计划执行时间") Date plan_execute_date,
			@Param(value=Record.ACTUAL_EXECUTE_DATE, required=false,	desc="实际执行时间") Date actual_execute_date,
			@Param(value=Record.CONSUME, required=false,	desc="时长（毫秒）") Integer consume,
			@Param(value=Record.INPUT, required=false,	desc="实际输入") String input,
			@Param(value=Record.OUTPUT, required=false,	desc="实际输出") String output,
			@Param(value=Record.RESULT, required=false,	desc="执行结果（0：失败；1：成功）") Integer result,
			@Param(value="fields", required=false, desc="更新字段", tag="fields") String[] fields
			
		) throws Exception {
		
		return null;
	}

	@MethodInfo("删除")
	@DaoRemove(entity=Record.ENTITY_NAME)
	public Data remove(
		
			@Param(value=Record.RECORD_ID, required=true,	desc="RECORD ID") Integer record_id
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("查询所有")
	@DaoQuery(entity=Record.ENTITY_NAME, pageable=true)
	public Data findAll(
			@Param(value="page", 	required=false, desc="分页页号，默认0") int page,
			@Param(value="rows", 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return null;
	}

	@MethodInfo("根据主键查询")
	@DaoQuery(entity=Record.ENTITY_NAME, single=true)
	public Data findById(
		
			@Param(value=Record.RECORD_ID, required=true,	desc="RECORD ID") Integer record_id
		
		) throws Exception {
		return null;
	}

}