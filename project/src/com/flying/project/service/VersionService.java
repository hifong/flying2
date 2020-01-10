package com.flying.project.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.common.util.HashFile;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.StringUtils;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.model.QueryResult;
import com.flying.framework.security.Principal;
import com.flying.framework.service.AbstractService;
import com.flying.framework.util.SmartyOutputUtils;
import com.flying.project.model.Project;
import com.flying.project.model.ProjectMember;
import com.flying.project.model.Version;
import com.flying.project.model.Version.State;

@Service(value="VersionService", desc="版本")
public class VersionService extends AbstractService{
	private ProjectService projectService;
	
	private ProjectCompileLogService logService;
	
	private ProjectMemberService memService;
	
	private SequenceService seqService;
	
	public void setProjectService(ProjectService s) {
		this.projectService = s;
	}
	
	public void setProjectCompileLogService(ProjectCompileLogService s) {
		this.logService = s;
	}
	
	public void setProjectMemberService(ProjectMemberService s) {
		this.memService = s;
	}
	
	public void setSequenceService(SequenceService s) {
		this.seqService = s;
	}
        
	@MethodInfo("新增")
	@DaoCreate(entity=Version.ENTITY_NAME)
	public Version create(
			@Param(value=Version.PROJECT_ID, required=true,	desc="项目ID") Long project_id,
			@Param(value=Version.NAME, required=true,	desc="名称") String name,
			@Param(value=Version.STATE, required=false,	desc="状态", enumClass=State.class ) Integer state,
			@Param(value=Version.CREATE_DATE, required=false,	desc="创建时间") Date create_date,
			@Param(value=Version.CREATOR, required=false,	desc="创建人") String creator,
			@Param(value=Version.REQUIREMENT_DATE, required=false,	desc="需求时间") Date requirement_date,
			@Param(value=Version.DEPLOY_DATE, required=false,	desc="发布时间") Date deploy_date,
			@Param(value=Version.PLAN_ONLINE_DATE, required=false,	desc="计划上线时间") Date plan_online_date,
			@Param(value=Version.ACTURE_ONLINE_DATE, required=false,	desc="实际上线时间") Date acture_online_date,
			@Param(value=Version.REQUIREMENT, required=false,	desc="需求描述") String requirement,
			@Param(value=Version.CODE_BASE, required=false,	desc="代码库") String code_base
		
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		Project pj = this.projectService.findById(project_id);
		return ModelFactory.createModelInstance(Version.class, 
				Version.CREATE_DATE, new Date(), 
				Version.STATE, state == null?State.DEVELOPING.state: state, 
				Version.CREATOR, p == null?null: p.getName(), 
				Version.CODE_BASE, code_base == null?pj.getCodeBase(): code_base);
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Version.ENTITY_NAME)
	public ActionResult update(
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id,
			@Param(value=Version.NAME, required=true,	desc="名称") String name,
			@Param(value=Version.REQUIREMENT_DATE, required=false,	desc="需求时间") Date requirement_date,
			@Param(value=Version.DEPLOY_DATE, required=false,	desc="发布时间") Date deploy_date,
			@Param(value=Version.PLAN_ONLINE_DATE, required=false,	desc="计划上线时间") Date plan_online_date,
			@Param(value=Version.ACTURE_ONLINE_DATE, required=false,	desc="实际上线时间") Date acture_online_date,
			@Param(value=Version.REQUIREMENT, required=false,	desc="需求描述") String requirement,
			@Param(value=Version.CODE_BASE, required=false,	desc="代码库") String code_base
			
		) throws Exception {
		
		return null;
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Version.ENTITY_NAME)
	public ActionResult updateState(
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id,
			@Param(value=Version.STATE, required=true,	desc="状态", enumClass=State.class) Integer state
		) throws Exception {
		
		return null;
	}

	@MethodInfo("废弃")
	public ActionResult discard(
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id
		) throws Exception {
		return this.updateState(version_id, State.DISCARD.state);
	}

	@MethodInfo("测试")
	public ActionResult test(
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id
		) throws Exception {
		return this.updateState(version_id, State.TESTING.state);
	}

	@MethodInfo("发布")
	public ActionResult publish(
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id
		) throws Exception {
		return this.updateState(version_id, State.PUBLISHED.state);
	}

	@MethodInfo("上线")
	public ActionResult deploy(
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id
		) throws Exception {
		return this.updateState(version_id, State.ONLINE.state);
	}

	@MethodInfo("编译")
	public ActionResult compile(
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		
		//find shell template
		final Version version = this.findById(version_id);
		if(version.getState() > State.TESTING.state) {
			throw new AppException("1", "开发和测试版本才能编译！");
		}
		//
		final Project projectModule = this.projectService.findById(version.getProjectId());
		final Project project = this.projectService.findById(projectModule.getParentId());
		
		final String projectCode = project.getCode();
		final String projectCompiler = projectModule.getCompiler();
		final String moduleCode = projectModule.getCode();
		final String versionName = version.getName();
		final String versionCodeBase = version.getCodeBase();
		//
		final String tagCodeBase = module.getModuleConfig().getConfig(projectCode+".tag_code_base");
		final String compileFolder = module.getModuleConfig().getConfig(projectCode+".compile_folder");
		final String compileDisk = module.getModuleConfig().getConfig(projectCode+".compile_disk");
		final String targetFolder = module.getModuleConfig().getConfig("target_folder");
		//
		int compileCount = this.seqService.nextValue("Version."+ moduleCode + "." + version.getName()).getInt("value", -1);
		String compileNum = compileCount < 10?"0" + compileCount: String.valueOf(compileCount);
		//
		final String tag_code_base = tagCodeBase + "/" + projectCode + versionName + "_" + moduleCode  +"_" + compileNum;
		final String compile_folder = compileFolder + "/" + projectCode + "." + moduleCode + "-" + versionName  +"-" + compileNum;
		final String target_file = targetFolder+"/" + projectCode + "/" + moduleCode + "-" + versionName  +"-" + compileNum + "." + projectCompiler ;
		final String target_desc = targetFolder+"/" + projectCode + "/" + moduleCode + "-" + versionName  +"-" + compileNum + ".txt" ;
		
		this.mkdirs(targetFolder+"/" + projectCode);
		final String downloadURL = module.getModuleConfig().getConfig("uploadURL") + "/" + projectCode + "/" + moduleCode + "-" + versionName  +"-" + compileNum + "." + projectCompiler;
		final String downloadDescURL = module.getModuleConfig().getConfig("uploadURL") + "/" + projectCode + "/" + moduleCode + "-" + versionName  +"-" + compileNum + ".txt";
		//
		final Data shellContext = new Data();
		shellContext.put("project", projectCode);
		shellContext.put("module", moduleCode);
		shellContext.put("version", versionName);
		shellContext.put("version_code_base", versionCodeBase);
		shellContext.put("compileNum", compileNum);
		shellContext.put("tag_code_base", tag_code_base);
		shellContext.put("compile_folder", compile_folder );
		shellContext.put("compile_disk", compileDisk );
		//
		final String shellFile = compileFolder + "/" + 
				projectCode + "." + moduleCode + "-" + versionName  +"-" + compileNum + ".bat" ;
		final FileWriter fw = new FileWriter(shellFile);
		SmartyOutputUtils.output(module, shellContext, "shell/"+projectCode+".tpl", fw);
		fw.flush();
		fw.close();
		//
		String info;
		try {
			info = runShell(shellFile);
		} catch (Exception e) {
			info = StringUtils.toString(e);
		}
		//解析结果
		final String tag = "[INFO]";
		int tagIndex = info.indexOf(tag);
		info = tagIndex > 0? info.substring(tagIndex): info;
		
		final String tag0 = "[INFO] BUILD SUCCESS";
		int state = info.indexOf(tag0) > 0? 1:2;
		//解析jar/war文件
		String tag1 = "[INFO] Building "+projectCompiler+":";
		int jarIndex1 = info.indexOf(tag1);
		int jarIndex2 = info.indexOf("[INFO]", jarIndex1 + tag1.length());
		final String jarFilePath = info.substring(jarIndex1 + tag1.length(), jarIndex2 - 1).trim();
		final String md5 = HashFile.getFileMD5(jarFilePath);
		
		final File jarFile = new File(jarFilePath);
		final File targetFile = new File(target_file);
		jarFile.renameTo(targetFile);
		//
		final FileOutputStream fos = new FileOutputStream(target_desc);
		fos.write(info.getBytes());
		fos.flush();
		fos.close();
		//删除临时目录
		final File compileDir = new File(compile_folder);
		rm(compileDir);
		//
		this.logService.create(projectModule.getProjectId(), version_id, new Date(), compileCount, state, 
				downloadURL, tag_code_base, downloadDescURL, md5, (p == null?null: p.getName()));
		//
		List<ProjectMember>  members = this.memService.findByProject(projectModule.getProjectId()).getRows();
		List<String> mails = members.stream().filter(x -> x.get("mail") !=null ).map(x -> (String)x.get("mail")).collect(Collectors.toList());
		if(!mails.isEmpty()) {
			final String title = projectCode + "." + moduleCode + "-" + versionName  +"-" + compileNum;
			ServiceHelper.invokeAsync("msg", "mail:sendSimpleMail", new Data(
					"to",mails,
					"subject", state == 1? title + "编译成功": title + "编译失败",
					"msg", state == 1? "下载地址："+downloadURL: info
			));
		}
		//
		return ModelFactory.createModelInstance(ActionResult.class, 
				"shellFile", shellFile, 
				"info", downloadDescURL, 
				"state", state == 1?"success":"fail", 
				"jarFile", jarFilePath, 
				Codes.CODE, Codes.SUCCESS);
		
	}
	
	private void rm(File file) {
		if(file == null || !file.exists()) return;
		if(file.isFile()) file.delete();
		if(file.isDirectory()) {
			File[] subs = file.listFiles();
			if(subs == null || subs.length == 0)
				file.delete();
			else {
				for(File f: subs) {
					rm(f);
				}
			}
		}
	}
	
	private void mkdirs(String dir) {
		File f = new File(dir);
		if(!f.exists())
			f.mkdirs();
	}
	
	private static String runShell(String shellFile) throws Exception {
		Process child = Runtime.getRuntime().exec(shellFile);
		try(InputStream in = child.getInputStream()) {
			String result = streamToString(in, "GBK");
			return result;
		}
	}

	public static String streamToString(InputStream is, String code) {
		StringBuilder builder = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(is, code))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	public static void main(String[] args) throws Exception {
		System.out.println(runShell("C:\\EFPS\\030-Coding\\010-Source\\011-txs\\devTrunk\\compile.bat"));
	}

	@MethodInfo("删除")
	@DaoRemove(entity=Version.ENTITY_NAME)
	public ActionResult remove(
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id
		) throws Exception {
		
		return null;
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=Version.ENTITY_NAME, pageable=true, modelClass=Version.class, osql="order by create_date desc")
	public QueryResult<Version> findAll(
			@Param(value=Version.PROJECT_ID, required=false,	desc="项目ID") Long project_id,
			@Param(value=Version.NAME, required=false,	desc="名称") String name,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return null;
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=Version.ENTITY_NAME, pageable=false, modelClass=Version.class, osql="order by create_date desc")
	public QueryResult<Version> findByVersionName(
			@Param(value=Version.NAME, required=true,	desc="名称") String name) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=Version.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public Version findById(
		
			@Param(value=Version.VERSION_ID, required=true,	desc="版本ID") Long version_id
		) throws Exception {
		return null;
	}

}