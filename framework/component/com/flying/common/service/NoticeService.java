package com.flying.common.service;

import java.net.InetAddress;
import java.util.Date;

import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;
import com.flying.framework.service.AbstractService;

@Service("$Notice")
public class NoticeService extends AbstractService {

	@MethodInfo("安装模块成功通知")
	public Data installNotice() throws Exception{
		if(Application.getInstance().getModules().exists("admin"))
			return ServiceHelper.invoke("admin", "MonitorService:installNotice", new Data(
					"appId", 	Application.getInstance().getId(),
					"moduleId",	module.getId(),
					"version", 	module.getModuleConfig().getVersion(),
					"path", 	module.getPath(),
					"hostAddress",	InetAddress.getLocalHost().getHostAddress(),
					"hostName",		InetAddress.getLocalHost().getHostName(),
					Codes.CODE,	Codes.SUCCESS
				));
		else
			return new Data(Codes.CODE, Codes.FAIL);
	}
	
	@MethodInfo("卸载模块成功通知")
	public Data uninstallNotice() throws Exception {
		if(Application.getInstance().getModules().exists("admin"))
			return ServiceHelper.invoke("admin", "monitor:uninstallNotice", new Data(
					"appId", 	Application.getInstance().getId(),
					"moduleId",	module.getId(),
					Codes.CODE,	Codes.SUCCESS
				));
		else
			return new Data(Codes.CODE, Codes.FAIL);
	}
    
	@MethodInfo("创建事件记录")
	public Data invokeNotice(

			@Param(value="event_date",        required=false,	desc="服务时间") Date event_date,
			@Param(value="app_id",            required=false,	desc="应用ID") String app_id,
			@Param(value="module_id",         required=false,	desc="模块ID") String module_id,
			@Param(value="service_id",        required=false,	desc="服务ID") String service_id,
			@Param(value="service_method",    required=false,	desc="服务接口") String service_method,
			@Param(value="principle",         required=false,	desc="服务用户") Principal principle,
			@Param(value="tid",               required=false,	desc="交易ID") long tid,
			@Param(value="consume",           required=false,	desc="消耗时长") long consume,

			@Param(value="input",             required=false,	desc="输入") Data input,
			@Param(value="output",            required=false,	desc="输出") Data output,
			@Param(value="error",             required=false,	desc="错误") Throwable error
		
		) throws Exception {
		if("true".equals(this.module.getModuleConfig().getConfig("send_log"))) {
			String return_code = output == null?null:output.getString(Codes.CODE);
			//
			Data d = new Data(
					"event_date", 	event_date,
					"app_id", 		app_id,
					"module_id", 	module_id,
					"service_id", 	service_id,
					"service_method", 	service_method,
					"principle", 	principle,
					"tid", 			tid,
					"consume", 		consume,
					"return_code", 	return_code,
					"input", 		input,
					"output", 		output,
					"error", 		error
				);
			//
			final String invokeNoticeModule = this.serviceConfig.getConfig("invoke_notice_module");
			final String invokeNoticeService = this.serviceConfig.getConfig("invoke_notice_service");
			ServiceHelper.invokeAsync(invokeNoticeModule, invokeNoticeService, d);
		}
		return new Data(Codes.CODE, Codes.SUCCESS);
	}
}
