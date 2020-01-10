package com.flying.common.remote.hessian;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.caucho.hessian.client.HessianProxyFactory;
import com.flying.framework.application.Application;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.remote.RemoteServiceInvoker;
import com.flying.framework.security.Principal;

/**
 * 远程调用客户端
 * @author wanghaifeng
 * 
 */
public class HessianRemoteServiceInvoker implements RemoteServiceInvoker {

	@Override
    public Data invoke(Principal principal, LocalModule localModule, String remoteModuleId, String serviceId, Data request)
    	throws Exception {
         if (localModule == null) {
             localModule = ServiceContext.getContext().getModule();
         }

         if (localModule != null) {
             Thread.currentThread().setContextClassLoader(localModule.getClassLoader());
         }
         if(request == null) request = new Data();

         String url = Application.getInstance().getModules().getRemoteModule(remoteModuleId).getPath();
         
    	 RemoteService remoteService = getRemoteService(url);
    	 //
    	 ServiceContext ctx = ServiceContext.getContext();
    	 RemoteRequest remoteRequest = new RemoteRequest(request, ctx.getUuid(), remoteModuleId, ctx.getModule() != null? ctx.getModule().getId(): "", ctx.getPrincipal());
    	 final RemoteRequest res = remoteService.invoke(principal, remoteModuleId, serviceId, remoteRequest);
         if (res.getException() != null) {
             throw (Exception) res.getException();
         } else {
             return res.getResponse();
         }
    }

    private static Map<String,RemoteService> remoteServiceMap = new ConcurrentHashMap<String, RemoteService>();
    private static Object lock = new Object();
    
    private RemoteService getRemoteService(String url) throws Exception {
    	RemoteService remoteService = remoteServiceMap.get(url);
    	if(remoteService==null){
    		synchronized (lock) {
    			remoteService = remoteServiceMap.get(url);
    			if(remoteService==null){
    				HessianProxyFactory factory = new HessianProxyFactory(Thread.currentThread().getContextClassLoader());
    				HttpClientHessianConnectionFactory hessianConnectionFactory = HttpClientHessianConnectionFactory.getInstance();
    		        //hessianConnectionFactory.addHeader("authorization", "admin");
    		        factory.setConnectionFactory(hessianConnectionFactory);
    		        
		        	final Data config = Application.getInstance().getConfigs("hessian");
    		        if (config != null) {
    		            factory.setConnectTimeout(config.getLong("connectTimeout", 10000l));
    		            factory.setReadTimeout(config.getLong("readTimeout", 10000l));
    		            factory.setHessian2Reply(config.getBoolean("hessian2Reply", true));
    		            factory.setHessian2Request(config.getBoolean("hesian2Request", false));
    		            factory.setChunkedPost(config.getBoolean("chunkedPost", true));
    		            factory.setDebug(config.getBoolean("debug", false));
    		        }
    		        remoteService = (RemoteService) factory.create(RemoteService.class, url);
    		        remoteServiceMap.put(url, remoteService);
    			}
			}
    	}
    	
        return remoteService;
    }
}
