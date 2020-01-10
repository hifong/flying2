package com.flying.project.model;

import java.util.Date;

import com.flying.framework.data.Data;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.ServiceField;
import com.flying.framework.annotation.ServiceFieldParam;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="pm_project_compile_log",title="项目版本编译记录", table="pm_project_compile_log", primaryKeys={"log_id"})
public class ProjectCompileLog extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "pm_project_compile_log";
	
	public final static String LOG_ID = "log_id";
	public final static String VERSION_ID = "version_id";
	public final static String COMPILE_DATE = "compile_date";
	public final static String COMPILE_NUM = "compile_num";
	public final static String STATE = "state";
	public final static String URL = "url";
	public final static String CODE_BASE = "code_base";
	public final static String PROJECT_ID = "project_id";
	public final static String INFO = "info";
	public final static String MD5 = "md5";
	public final static String CREATOR = "creator";
	
	
	@Param(value=LOG_ID,   required=true,   maxlength=0,	desc="编译日志ID" , generator="auto_increment")
	private Long logId;
	
	@Param(value=VERSION_ID,   required=false,   maxlength=0,	desc="版本ID" )
	private Long versionId;
	
	@Param(value=COMPILE_DATE,   required=false,   maxlength=0,	desc="编译时间" )
	private Date compileDate;
	
	@Param(value=COMPILE_NUM,   required=false,   maxlength=0,	desc="编译次数" )
	private Integer compileNum;
	
	@Param(value=STATE,   required=false,   maxlength=0,	desc="编译状态（0：编译中；1：编译成功；2：编译失败）" )
	private Integer state;
	
	@Param(value=URL,   required=false,   maxlength=200,	desc="发布文件URL" )
	private String url;
	
	@Param(value=CODE_BASE,   required=false,   maxlength=200,	desc="代码库" )
	private String codeBase;
	
	@Param(value=PROJECT_ID,   required=false,   maxlength=0,	desc="项目ID" )
	private Long projectId;
	
	@Param(value=INFO,   required=false,   maxlength=200,	desc="编译信息" )
	private String info;
	
	@Param(value=MD5,   required=false,   maxlength=100,	desc="发布文件指纹" )
	private String md5;
	
	@Param(value=CREATOR,   required=false,   maxlength=100,	desc="创建人" )
	private String creator;

	@ServiceField(serviceId="ProjectService:findById", lazyLoad=false, params={@ServiceFieldParam(param=Project.PROJECT_ID, value="$"+PROJECT_ID)})
	@Param(value="project",   required=false,   maxlength=400,	desc="项目" )
	private Project project;

	@ServiceField(serviceId="VersionService:findById", lazyLoad=false, params={@ServiceFieldParam(param=Version.VERSION_ID, value="$"+VERSION_ID)})
	@Param(value="version",   required=false,   maxlength=400,	desc="版本" )
	private Version version;
	
	
	public Long getLogId() {
		return this.logId;
	}
	
	public void setLogId(Long logId) {
		this.logId = logId;
	}
	
	public Long getVersionId() {
		return this.versionId;
	}
	
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}
	
	public Date getCompileDate() {
		return this.compileDate;
	}
	
	public void setCompileDate(Date compileDate) {
		this.compileDate = compileDate;
	}
	
	public Integer getCompileNum() {
		return this.compileNum;
	}
	
	public void setCompileNum(Integer compileNum) {
		this.compileNum = compileNum;
	}
	
	public Integer getState() {
		return this.state;
	}
	
	public void setState(Integer state) {
		this.state = state;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCodeBase() {
		return this.codeBase;
	}
	
	public void setCodeBase(String codeBase) {
		this.codeBase = codeBase;
	}
	
	public Long getProjectId() {
		return this.projectId;
	}
	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public String getInfo() {
		return this.info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getMd5() {
		return this.md5;
	}
	
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	public String getCreator() {
		return this.creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public Project getProject() {
		return this.project;
	}
	
	public String getProjectName() {
		Project p = this.getProject();
		return p == null?null:p.getName();
	}
	
	public Version getVersion() {
		return this.version;
	}
	
	public String getVersionName() {
		Version v = this.getVersion();
		return v == null?null: v.getName();
	}
}