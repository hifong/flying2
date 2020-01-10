package com.flying.project.service;

import java.util.Date;
import java.util.List;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.common.util.StringUtils;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.QueryResult;
import com.flying.framework.service.AbstractService;
import com.flying.project.model.ProjectCompileLog;
import com.flying.project.model.Version;

@Service(value="ProjectCompileLogService", desc="项目版本编译记录")
public class ProjectCompileLogService extends AbstractService{
	private VersionService verService;
	
	public void setVersionService(VersionService s) {
		this.verService = s;
	}
        
	@MethodInfo("新增")
	@DaoCreate(entity=ProjectCompileLog.ENTITY_NAME)
	public ProjectCompileLog create(
			@Param(value=ProjectCompileLog.PROJECT_ID, required=true,	desc="PROJECT ID") Long project_id,
			@Param(value=ProjectCompileLog.VERSION_ID, required=true,	desc="版本ID") Long version_id,
			@Param(value=ProjectCompileLog.COMPILE_DATE, required=false,	desc="编译时间") Date compile_date,
			@Param(value=ProjectCompileLog.COMPILE_NUM, required=false,	desc="编译次数") Integer compile_num,
			@Param(value=ProjectCompileLog.STATE, required=false,	desc="编译状态（0：编译中；1：编译成功；2：编译失败）") Integer state,
			@Param(value=ProjectCompileLog.URL, required=false,	desc="发布文件URL") String url,
			@Param(value=ProjectCompileLog.CODE_BASE, required=false,	desc="CODE BASE") String code_base,
			@Param(value=ProjectCompileLog.INFO, required=false,	desc="INFO") String info,
			@Param(value=ProjectCompileLog.MD5, required=false,	desc="MD5") String md5,
			@Param(value=ProjectCompileLog.CREATOR, required=false,	desc="CREATOR") String creator
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=ProjectCompileLog.ENTITY_NAME)
	public ActionResult update(
		
			@Param(value=ProjectCompileLog.LOG_ID, required=true,	desc="编译日志ID") Long log_id,
			@Param(value=ProjectCompileLog.VERSION_ID, required=false,	desc="版本ID") Long version_id,
			@Param(value=ProjectCompileLog.COMPILE_DATE, required=false,	desc="编译时间") Date compile_date,
			@Param(value=ProjectCompileLog.COMPILE_NUM, required=false,	desc="编译次数") Integer compile_num,
			@Param(value=ProjectCompileLog.STATE, required=false,	desc="编译状态（0：编译中；1：编译成功；2：编译失败）") Integer state,
			@Param(value=ProjectCompileLog.URL, required=false,	desc="发布文件URL") String url,
			@Param(value=ProjectCompileLog.CODE_BASE, required=false,	desc="CODE BASE") String code_base,
			@Param(value=ProjectCompileLog.PROJECT_ID, required=false,	desc="PROJECT ID") Long project_id,
			@Param(value=ProjectCompileLog.INFO, required=false,	desc="INFO") String info,
			@Param(value=ProjectCompileLog.MD5, required=false,	desc="MD5") String md5,
			@Param(value=ProjectCompileLog.CREATOR, required=false,	desc="CREATOR") String creator
			
		) throws Exception {
		
		return null;
	}

	@MethodInfo("删除")
	@DaoRemove(entity=ProjectCompileLog.ENTITY_NAME)
	public ActionResult remove(
		
			@Param(value=ProjectCompileLog.LOG_ID, required=true,	desc="编译日志ID") Long log_id
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=ProjectCompileLog.ENTITY_NAME, pageable=true, modelClass=ProjectCompileLog.class, osql="order by compile_date desc")
	public QueryResult<ProjectCompileLog> findAll2(
			@Param(value=ProjectCompileLog.PROJECT_ID, required=false,	desc="PROJECT ID") Long project_id,
			@Param(value=ProjectCompileLog.VERSION_ID, required=false,	desc="版本ID", tag="in") Long[] version_ids,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return null;
	}

	@MethodInfo("搜索")
	public QueryResult<ProjectCompileLog> findAll(
			@Param(value=ProjectCompileLog.PROJECT_ID, required=false,	desc="PROJECT ID") Long project_id,
			@Param(value=ProjectCompileLog.VERSION_ID, required=false,	desc="版本") String versionName,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		Long version_ids[] = null;
		if(!StringUtils.isEmpty(versionName)) {
			List<Version> vers = this.verService.findByVersionName(versionName).getRows();
			version_ids = new Long[vers.size()];
			for(int i=0; i< vers.size(); i++) {
				version_ids[i] = vers.get(i).getVersionId();
			}
		}
		return this.findAll2(project_id, version_ids, page, rows);
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=ProjectCompileLog.ENTITY_NAME, pageable=false, modelClass=ProjectCompileLog.class, osql="order by compile_date desc")
	public QueryResult<ProjectCompileLog> findByVersion(
			@Param(value=ProjectCompileLog.VERSION_ID, required=true,	desc="版本ID") Long version_id) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=ProjectCompileLog.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public ProjectCompileLog findById(
			@Param(value=ProjectCompileLog.LOG_ID, required=true,	desc="编译日志ID") Long log_id
		) throws Exception {
		return null;
	}

}