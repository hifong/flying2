package com.flying.project.model;

import java.util.Date;
import java.util.List;

import com.flying.framework.annotation.ConstantEnum;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.ServiceField;
import com.flying.framework.annotation.ServiceFieldParam;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="pm_project",title="项目", table="pm_project", primaryKeys={"project_id"})
public class Project extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "pm_project";
	
	public final static String PROJECT_ID = "project_id";
	public final static String PARENT_ID = "parent_id";
	public final static String NAME = "name";
	public final static String CODE = "code";
	public final static String REMARK = "remark";
	public final static String OWNER = "owner";
	public final static String CREATE_DATE = "create_date";
	public final static String LAST_VERSION = "last_version";
	public final static String CODE_BASE = "code_base";
	public final static String COMPILER = "compiler";
	
	public enum Compiler implements ConstantEnum<String> {
		JAR("jar", "JAR包"), WAR("war", "WAR包");
		
		public final String compiler;
		public final String remarks;
		
		Compiler(String compiler, String remarks) {
			this.compiler = compiler;
			this.remarks = remarks;
		}
		@Override
		public String value() {
			return compiler;
		}
		@Override
		public String text() {
			return remarks;
		}
	}
	
	@Param(value=PROJECT_ID,   required=true,   maxlength=0,	desc="项目ID" , generator="auto_increment")
	private Long projectId;
	
	@Param(value=PARENT_ID,   required=false,   maxlength=0,	desc="上级项目" )
	private Long parentId;
	
	@Param(value=NAME,   required=true,   maxlength=50,	desc="名称" )
	private String name;
	
	@Param(value=CODE,   required=true,   maxlength=10,	desc="代码" )
	private String code;
	
	@Param(value=REMARK,   required=false,   maxlength=200,	desc="说明" )
	private String remark;
	
	@Param(value=OWNER,   required=false,   maxlength=50,	desc="责任人" )
	private String owner;
	
	@Param(value=CREATE_DATE,   required=false,   maxlength=0,	desc="创建时间" )
	private Date createDate;
	
	@Param(value=LAST_VERSION,   required=false,   maxlength=50,	desc="最新版本" )
	private String lastVersion;
	
	@Param(value=CODE_BASE,   required=false,   maxlength=150,	desc="代码库" )
	private String codeBase;
	
	@Param(value=COMPILER,   required=false,   maxlength=100,	desc="COMPILER", enumClass=Compiler.class )
	private String compiler;

	@ServiceField(serviceId="ProjectService:findChildren", lazyLoad=false, valueAttribute="rows", params={@ServiceFieldParam(param=Project.PARENT_ID, value="$"+PROJECT_ID)})
	@Param(value="children",   required=false,   maxlength=400,	desc="子项目" )
	private List<Project> children;
	
	public Long getProjectId() {
		return this.projectId;
	}
	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public Long getParentId() {
		return this.parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getRemark() {
		return this.remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public Date getCreateDate() {
		return this.createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getLastVersion() {
		return this.lastVersion;
	}
	
	public void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
	}
	
	public String getCodeBase() {
		return this.codeBase;
	}
	
	public void setCodeBase(String codeBase) {
		this.codeBase = codeBase;
	}
	
	public String getCompiler() {
		return this.compiler;
	}
	
	public void setCompiler(String compiler) {
		this.compiler = compiler;
	}
	
	public Long getId() {
		return this.getProjectId();
	}
	
	public String getText() {
		return this.getName();
	}
	
	public List<Project> getChildren() {
		return this.children;
	}
}