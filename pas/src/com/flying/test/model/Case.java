package com.flying.test.model;

import java.util.Date;

import com.flying.framework.annotation.Param;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@Meta(id="test_case",title="", table="test_case", primaryKeys={"case_id"})
public class Case implements DataSerializable {
	public final static String ENTITY_NAME = "test_case";
	
	public final static String CASE_ID = "case_id";
	public final static String NAME = "name";
	public final static String REMARKS = "remarks";
	public final static String SERVICE = "service";
	public final static String INPUT = "input";
	public final static String OUTPUT = "output";
		
	
	public enum Field {
		
		case_id        (Case.CASE_ID,   "Integer",	"CASE ID"),
		name           (Case.NAME,   "String",	"用例名称"),
		remarks        (Case.REMARKS,   "String",	"用例说明"),
		service        (Case.SERVICE,   "String",	"执行接口"),
		input          (Case.INPUT,   "String",	"计划输入"),
		output         (Case.OUTPUT,   "String",	"期望输出");
		
		public final String fieldName;
		public final String fieldType;
		public final String fieldComment;
	
		Field(String name, String dataType, String comment) {
			this.fieldName 		= name;
			this.fieldType		= dataType;
			this.fieldComment	= comment;
		}
	}
	
	
	@Param(value="case_id",   required=true,   maxlength=0,	desc="CASE ID")
	private Integer caseId;
	
	@Param(value="name",   required=false,   maxlength=50,	desc="用例名称")
	private String name;
	
	@Param(value="remarks",   required=false,   maxlength=400,	desc="用例说明")
	private String remarks;
	
	@Param(value="service",   required=false,   maxlength=80,	desc="执行接口")
	private String service;
	
	@Param(value="input",   required=false,   maxlength=400,	desc="计划输入")
	private String input;
	
	@Param(value="output",   required=false,   maxlength=2000,	desc="期望输出")
	private String output;
	
	
	public Integer getCaseId() {
		return this.caseId;
	}
	
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getRemarks() {
		return this.remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getService() {
		return this.service;
	}
	
	public void setService(String service) {
		this.service = service;
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
	

}