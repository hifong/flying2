package com.flying.project.service;

import java.util.Date;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.service.AbstractService;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.QueryResult;

import com.flying.project.model.ProjectMember;

@Service(value="ProjectMemberService", desc="项目成员")
public class ProjectMemberService extends AbstractService{
        
	@MethodInfo("新增")
	@DaoCreate(entity=ProjectMember.ENTITY_NAME)
	public ProjectMember create(
		
			@Param(value=ProjectMember.PROJECT_ID, required=false,	desc="项目ID") Long project_id,
			@Param(value=ProjectMember.MEMBER_ID, required=false,	desc="成员ID") Long member_id,
			@Param(value=ProjectMember.ROLE, required=false,	desc="角色") String role,
			@Param(value=ProjectMember.CREATE_DATE, required=false,	desc="创建时间") Date create_date
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=ProjectMember.ENTITY_NAME)
	public ActionResult update(
		
			@Param(value=ProjectMember.PROJECT_ID, required=false,	desc="项目ID") Long project_id,
			@Param(value=ProjectMember.MEMBER_ID, required=false,	desc="成员ID") Long member_id,
			@Param(value=ProjectMember.ROLE, required=false,	desc="角色") String role,
			@Param(value=ProjectMember.CREATE_DATE, required=false,	desc="创建时间") Date create_date
			
		) throws Exception {
		
		return null;
	}

	@MethodInfo("删除")
	@DaoRemove(entity=ProjectMember.ENTITY_NAME)
	public ActionResult remove(
		
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=ProjectMember.ENTITY_NAME, pageable=true, modelClass=ProjectMember.class)
	public QueryResult<ProjectMember> findAll(
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return null;
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=ProjectMember.ENTITY_NAME, pageable=false, modelClass=ProjectMember.class)
	public QueryResult<ProjectMember> findByProject(
			@Param(value=ProjectMember.PROJECT_ID, required=false,	desc="项目ID") Long project_id) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=ProjectMember.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public ProjectMember findById(
		
		) throws Exception {
		return null;
	}

}