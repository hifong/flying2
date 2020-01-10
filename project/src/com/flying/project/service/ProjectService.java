package com.flying.project.service;

import java.util.Date;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.common.util.StringUtils;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.service.AbstractService;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.model.QueryResult;

import com.flying.project.model.Project;
import com.flying.project.model.Project.Compiler;

@Service(value="ProjectService", desc="项目")
public class ProjectService extends AbstractService{
        
	@MethodInfo("新增")
	@DaoCreate(entity=Project.ENTITY_NAME)
	public Project create(
			@Param(value=Project.PARENT_ID, required=false,	desc="上级项目") Long parent_id,
			@Param(value=Project.NAME, required=true,	desc="名称") String name,
			@Param(value=Project.CODE, required=true,	desc="代码") String code,
			@Param(value=Project.REMARK, required=false,	desc="说明") String remark,
			@Param(value=Project.OWNER, required=false,	desc="责任人") String owner,
			@Param(value=Project.CREATE_DATE, required=false,	desc="创建时间") Date create_date,
			@Param(value=Project.LAST_VERSION, required=false,	desc="最新版本") String last_version,
			@Param(value=Project.CODE_BASE, required=false,	desc="代码库") String code_base,
			@Param(value=Project.COMPILER, required=false,	desc="COMPILER", enumClass=Compiler.class) String compiler
		
		) throws Exception {
		
		return ModelFactory.createModelInstance(Project.class, Project.CREATE_DATE, new Date());
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Project.ENTITY_NAME)
	public ActionResult update(
		
			@Param(value=Project.PROJECT_ID, required=true,	desc="项目ID") Long project_id,
			@Param(value=Project.PARENT_ID, required=false,	desc="上级项目") Long parent_id,
			@Param(value=Project.NAME, required=true,	desc="名称") String name,
			@Param(value=Project.CODE, required=true,	desc="代码") String code,
			@Param(value=Project.REMARK, required=false,	desc="说明") String remark,
			@Param(value=Project.OWNER, required=false,	desc="责任人") String owner,
			@Param(value=Project.LAST_VERSION, required=false,	desc="最新版本") String last_version,
			@Param(value=Project.CODE_BASE, required=false,	desc="代码库") String code_base,
			@Param(value=Project.COMPILER, required=false,	desc="COMPILER", enumClass=Compiler.class) String compiler
			
		) throws Exception {
		
		return null;
	}

	@MethodInfo("删除")
	@DaoRemove(entity=Project.ENTITY_NAME)
	public ActionResult remove(
		
			@Param(value=Project.PROJECT_ID, required=true,	desc="项目ID") Long project_id
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=Project.ENTITY_NAME, pageable=true, modelClass=Project.class)
	public QueryResult<Project> findAll(
			@Param(value=Project.NAME, required=false,	desc="名称", tag="like") String name,
			@Param(value=Project.CODE, required=false,	desc="代码", tag="like") String code,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return ModelFactory.createModelInstance(QueryResult.class, 
				Project.NAME, StringUtils.isEmpty(name)?null: "%"+name+"%", 
				Project.CODE, StringUtils.isEmpty(code)?null: "%"+code+"%");
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=Project.ENTITY_NAME, pageable=false, modelClass=Project.class)
	public QueryResult<Project> findChildren(
			@Param(value=Project.PARENT_ID, required=false,	desc="上级项目") Long parent_id) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=Project.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public Project findById(
		
			@Param(value=Project.PROJECT_ID, required=true,	desc="项目ID") Long project_id
		) throws Exception {
		return null;
	}

}