package com.flying.project.model;

import java.util.Date;
import java.util.List;

import com.flying.framework.data.Data;
import com.flying.framework.annotation.ConstantEnum;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.ServiceField;
import com.flying.framework.annotation.ServiceFieldParam;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="pm_version",title="版本", table="pm_version", primaryKeys={"version_id"})
public class Version extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "pm_version";
	
	public final static String VERSION_ID = "version_id";
	public final static String PROJECT_ID = "project_id";
	public final static String NAME = "name";
	public final static String STATE = "state";
	public final static String CREATE_DATE = "create_date";
	public final static String REQUIREMENT_DATE = "requirement_date";
	public final static String DEPLOY_DATE = "deploy_date";
	public final static String PLAN_ONLINE_DATE = "plan_online_date";
	public final static String ACTURE_ONLINE_DATE = "acture_online_date";
	public final static String REQUIREMENT = "requirement";
	public final static String CODE_BASE = "code_base";
	public final static String CREATOR = "creator";
	
	public enum State implements ConstantEnum<Integer> {
		DISCARD(-1, "废除"), DEVELOPING(0, "开发中"), TESTING(1, "测试中"), PUBLISHED(8, "已发布"), ONLINE(9, "已上线");
		
		public final int state;
		public final String remarks;
		
		State(int state, String remarks) {
			this.state = state;
			this.remarks = remarks;
		}
		@Override
		public Integer value() {
			return state;
		}
		@Override
		public String text() {
			return remarks;
		}
	}
	
	@Param(value=VERSION_ID,   required=true,   maxlength=0,	desc="版本ID" , generator="auto_increment")
	private Long versionId;
	
	@Param(value=PROJECT_ID,   required=false,   maxlength=0,	desc="项目ID" )
	private Long projectId;
	
	@Param(value=NAME,   required=false,   maxlength=50,	desc="名称" )
	private String name;
	
	@Param(value=STATE,   required=false,   maxlength=0,	desc="状态", enumClass=State.class )
	private Integer state;
	
	@Param(value=CREATE_DATE,   required=false,   maxlength=0,	desc="创建时间" )
	private Date createDate;
	
	@Param(value=CREATOR,   required=false,   maxlength=0,	desc="创建人" )
	private String creator;
	
	@Param(value=REQUIREMENT_DATE,   required=false,   maxlength=0,	desc="需求时间" )
	private Date requirementDate;
	
	@Param(value=DEPLOY_DATE,   required=false,   maxlength=0,	desc="发布时间" )
	private Date deployDate;
	
	@Param(value=PLAN_ONLINE_DATE,   required=false,   maxlength=0,	desc="计划上线时间" )
	private Date planOnlineDate;
	
	@Param(value=ACTURE_ONLINE_DATE,   required=false,   maxlength=0,	desc="实际上线时间" )
	private Date actureOnlineDate;
	
	@Param(value=REQUIREMENT,   required=false,   maxlength=65535,	desc="需求描述" )
	private String requirement;
	
	@Param(value=CODE_BASE,   required=false,   maxlength=150,	desc="代码库" )
	private String codeBase;

	@ServiceField(serviceId="ProjectService:findById", lazyLoad=false, params={@ServiceFieldParam(param=Project.PROJECT_ID, value="$"+PROJECT_ID)})
	@Param(value="project",   required=false,   maxlength=400,	desc="项目" )
	private Project project;
	
	
	public Long getVersionId() {
		return this.versionId;
	}
	
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}
	
	public Long getProjectId() {
		return this.projectId;
	}
	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getRequirementDate() {
		return this.requirementDate;
	}
	
	public void setRequirementDate(Date requirementDate) {
		this.requirementDate = requirementDate;
	}
	
	public Date getDeployDate() {
		return this.deployDate;
	}
	
	public void setDeployDate(Date deployDate) {
		this.deployDate = deployDate;
	}
	
	public Date getPlanOnlineDate() {
		return this.planOnlineDate;
	}
	
	public void setPlanOnlineDate(Date planOnlineDate) {
		this.planOnlineDate = planOnlineDate;
	}
	
	public Date getActureOnlineDate() {
		return this.actureOnlineDate;
	}
	
	public void setActureOnlineDate(Date actureOnlineDate) {
		this.actureOnlineDate = actureOnlineDate;
	}
	
	public String getRequirement() {
		return this.requirement;
	}
	
	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}
	
	public String getCodeBase() {
		return this.codeBase;
	}
	
	public void setCodeBase(String codeBase) {
		this.codeBase = codeBase;
	}
	
	public Project getProject() {
		return this.project;
	}
	
	public String getProjectName() {
		Project p = this.getProject();
		return p == null?null: p.getName();
	}
}