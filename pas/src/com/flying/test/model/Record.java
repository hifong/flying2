package com.flying.test.model;

import java.util.Date;

import com.flying.framework.annotation.Param;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@Meta(id="test_record",title="", table="test_record", primaryKeys={"record_id"})
public class Record implements DataSerializable {
	public final static String ENTITY_NAME = "test_record";
	
	public final static String RECORD_ID = "record_id";
	public final static String FLOW_INSTANCE_ID = "flow_instance_id";
	public final static String FLOW_NODE_INSTANCE_ID = "flow_node_instance_id";
	public final static String CASE_ID = "case_id";
	public final static String SERVICE = "service";
	public final static String STATE = "state";
	public final static String CREATE_DATE = "create_date";
	public final static String PLAN_EXECUTE_DATE = "plan_execute_date";
	public final static String ACTUAL_EXECUTE_DATE = "actual_execute_date";
	public final static String CONSUME = "consume";
	public final static String INPUT = "input";
	public final static String OUTPUT = "output";
	public final static String RESULT = "result";
		
	
	public enum Field {
		
		record_id      (Record.RECORD_ID,   "Integer",	"RECORD ID"),
		flow_instance_id(Record.FLOW_INSTANCE_ID,   "Integer",	"流程实例ID"),
		flow_node_instance_id(Record.FLOW_NODE_INSTANCE_ID,   "Integer",	"流程环节实例ID"),
		case_id        (Record.CASE_ID,   "Integer",	"用例ID"),
		service        (Record.SERVICE,   "String",	"执行接口"),
		state          (Record.STATE,   "Integer",	"执行状态（0：未执行；1：已执行）"),
		create_date    (Record.CREATE_DATE,   "Date",	"创建时间"),
		plan_execute_date(Record.PLAN_EXECUTE_DATE,   "Date",	"计划执行时间"),
		actual_execute_date(Record.ACTUAL_EXECUTE_DATE,   "Date",	"实际执行时间"),
		consume        (Record.CONSUME,   "Integer",	"时长（毫秒）"),
		input          (Record.INPUT,   "String",	"实际输入"),
		output         (Record.OUTPUT,   "String",	"实际输出"),
		result         (Record.RESULT,   "Integer",	"执行结果（0：失败；1：成功）");
		
		public final String fieldName;
		public final String fieldType;
		public final String fieldComment;
	
		Field(String name, String dataType, String comment) {
			this.fieldName 		= name;
			this.fieldType		= dataType;
			this.fieldComment	= comment;
		}
	}
	
	
	@Param(value="record_id",   required=true,   maxlength=0,	desc="RECORD ID")
	private Integer recordId;
	
	@Param(value="flow_instance_id",   required=false,   maxlength=0,	desc="流程实例ID")
	private Integer flowInstanceId;
	
	@Param(value="flow_node_instance_id",   required=false,   maxlength=0,	desc="流程环节实例ID")
	private Integer flowNodeInstanceId;
	
	@Param(value="case_id",   required=false,   maxlength=0,	desc="用例ID")
	private Integer caseId;
	
	@Param(value="service",   required=false,   maxlength=80,	desc="执行接口")
	private String service;
	
	@Param(value="state",   required=false,   maxlength=0,	desc="执行状态（0：未执行；1：已执行）")
	private Integer state;
	
	@Param(value="create_date",   required=false,   maxlength=0,	desc="创建时间")
	private Date createDate;
	
	@Param(value="plan_execute_date",   required=false,   maxlength=0,	desc="计划执行时间")
	private Date planExecuteDate;
	
	@Param(value="actual_execute_date",   required=false,   maxlength=0,	desc="实际执行时间")
	private Date actualExecuteDate;
	
	@Param(value="consume",   required=false,   maxlength=0,	desc="时长（毫秒）")
	private Integer consume;
	
	@Param(value="input",   required=false,   maxlength=400,	desc="实际输入")
	private String input;
	
	@Param(value="output",   required=false,   maxlength=2000,	desc="实际输出")
	private String output;
	
	@Param(value="result",   required=false,   maxlength=0,	desc="执行结果（0：失败；1：成功）")
	private Integer result;
	
	
	public Integer getRecordId() {
		return this.recordId;
	}
	
	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}
	
	public Integer getFlowInstanceId() {
		return this.flowInstanceId;
	}
	
	public void setFlowInstanceId(Integer flowInstanceId) {
		this.flowInstanceId = flowInstanceId;
	}
	
	public Integer getFlowNodeInstanceId() {
		return this.flowNodeInstanceId;
	}
	
	public void setFlowNodeInstanceId(Integer flowNodeInstanceId) {
		this.flowNodeInstanceId = flowNodeInstanceId;
	}
	
	public Integer getCaseId() {
		return this.caseId;
	}
	
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}
	
	public String getService() {
		return this.service;
	}
	
	public void setService(String service) {
		this.service = service;
	}
	
	public Integer getState() {
		return this.state;
	}
	
	public void setState(Integer state) {
		this.state = state;
	}
	
	public Date getCreateDate() {
		return this.createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public Date getPlanExecuteDate() {
		return this.planExecuteDate;
	}
	
	public void setPlanExecuteDate(Date planExecuteDate) {
		this.planExecuteDate = planExecuteDate;
	}
	
	public Date getActualExecuteDate() {
		return this.actualExecuteDate;
	}
	
	public void setActualExecuteDate(Date actualExecuteDate) {
		this.actualExecuteDate = actualExecuteDate;
	}
	
	public Integer getConsume() {
		return this.consume;
	}
	
	public void setConsume(Integer consume) {
		this.consume = consume;
	}
	
	public String getInput() {
		return this.input;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getOutput() {
		return this.output;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public Integer getResult() {
		return this.result;
	}
	
	public void setResult(Integer result) {
		this.result = result;
	}
	

}