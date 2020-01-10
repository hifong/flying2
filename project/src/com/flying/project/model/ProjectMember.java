package com.flying.project.model;

import java.util.Date;

import com.flying.framework.data.Data;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="pm_project_member",title="项目成员", table="pm_project_member", primaryKeys={"project_id", "member_id"})
public class ProjectMember extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "pm_project_member";
	
	public final static String PROJECT_ID = "project_id";
	public final static String MEMBER_ID = "member_id";
	public final static String ROLE = "role";
	public final static String CREATE_DATE = "create_date";
	
	
	@Param(value=PROJECT_ID,   required=false,   maxlength=0,	desc="项目ID" )
	private Long projectId;
	
	@Param(value=MEMBER_ID,   required=false,   maxlength=0,	desc="成员ID" )
	private Long memberId;
	
	@Param(value=ROLE,   required=false,   maxlength=50,	desc="角色" )
	private String role;
	
	@Param(value=CREATE_DATE,   required=false,   maxlength=0,	desc="创建时间" )
	private Date createDate;
	
	
	public Long getProjectId() {
		return this.projectId;
	}
	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public Long getMemberId() {
		return this.memberId;
	}
	
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	public String getRole() {
		return this.role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public Date getCreateDate() {
		return this.createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	

}