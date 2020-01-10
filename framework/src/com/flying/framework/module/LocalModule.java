package com.flying.framework.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.flying.common.config.ConfigUtils;
import com.flying.common.log.Logger;
import com.flying.common.util.ClassUtils;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.LoadOnStartup;
import com.flying.framework.annotation.Service;
import com.flying.framework.cache.CacheProvider;
import com.flying.framework.config.ModuleConfig;
import com.flying.framework.config.ServiceConfig;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataConverter;
import com.flying.framework.data.DataDecorator;
import com.flying.framework.exception.ModuleInitializeException;
import com.flying.framework.exception.ServiceNotFoundException;
import com.flying.framework.metadata.Repository;
import com.flying.framework.request.RequestFilter;
import com.flying.framework.request.RequestService;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceLoader;
import com.flying.framework.service.SpringBeanFactory;

/**
 * LocalModule
 * @author wanghaifeng
 *
 */
@SuppressWarnings("rawtypes")
public class LocalModule extends Module {
	private final static Logger logger = Logger.getLogger(LocalModule.class);
	
	private boolean workingStatus = true;

	private ModuleConfig moduleConfig;
	
	private ClassLoader classLoader;
	private ServiceLoader serviceLoader;
	
	private final Map<String, Object> beans = Utils.newHashMap();
	private final Map<String, Object> services = Utils.newHashMap();
	private final Map<String, String> classServices = Utils.newHashMap();
	private final Map<String, RequestService> requestServices = Utils.newHashMap();
	private final Map<String, DataConverter> dataConverters = Utils.newHashMap();
	private final Map<String, ServiceHandler> annotationHandlers = Utils.newHashMap();
	private final List<ModuleEvent> events = Utils.newArrayList();
	private final Map<String, List<RequestFilter>> filters = Utils.newHashMap();
	
	private final Repository metadataRepository;
	private final Map<String, Object> moduleVariables = Utils.newHashMap();
	//
	private CacheProvider cacheProvider;
	//
	public static LocalModule newInstance(String id, String path, Data configs) {
		LocalModule m = new LocalModule(id, path, configs) ;
		return m;
	}
	
	private LocalModule(String id, String path, Data configs) {
		super(id, path, configs);
		long start = System.currentTimeMillis();
		
		try {
			this.moduleConfig = new ModuleConfig(path);
			this.classLoader = new LocalModuleClassLoader(this.moduleConfig, ServiceLoader.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(this.classLoader);
			
			this.serviceLoader = new ServiceLoader(this);
			this.metadataRepository = new Repository(this);
			this.scanClassPathForRegisteredAnnotation();
			
			this.initBeans();
			this.initRequestServices();
			this.initDataConverters();
			this.initMethodAnnotationHandlers();
			this.initEvents();
			//
			//
			logger.info("Module load success, module id[" + id + "], path["+path+"], time:[" + (System.currentTimeMillis() - start)+"]");
		} catch (Exception e) {
			logger.error("Init bean(ID:"+id+"; Path:"+path+") fail!", e);
			if(e instanceof ModuleInitializeException) {
				throw (ModuleInitializeException)e;
			} else {
				throw new ModuleInitializeException(e, this.getId(), path);
			}
		}
	}
	
	private void scanClassPathForRegisteredAnnotation() {
		LocalModuleClassPathScanner classPathScanner = new LocalModuleClassPathScanner(this);
		classPathScanner.scan();
	}
	
	public void registerServiceClass(Service serviceAnnotation, Class<?> serviceClass) {
		final ServiceConfig serviceConfig = new ServiceConfig(serviceAnnotation, serviceClass);
		final String serviceId = serviceAnnotation.value();
		this.moduleConfig.addServiceConfig(serviceId, serviceConfig);
	}
	
	public void registerRequestType(String requestType, String serviceId) {
		this.moduleConfig.getRequests().put(requestType, serviceId);
	}

	private void initBeans() {
		logger.debug("LocalModule initBeans");
		Map<String, String> beanConfigs = this.moduleConfig.getBeans();
		for(Entry<String, String> e: beanConfigs.entrySet()) {
			final String beanId = e.getKey();
			final String beanClass = e.getValue();
			try {
				Class<?> clazz = newClass(beanClass);
				Constructor<?> c = ClassUtils.getConstructorIfAvailable(clazz, LocalModule.class);
				Object bean = null;
				if(c != null) {
					bean = c.newInstance(this);
				} else {
					c = ClassUtils.getConstructorIfAvailable(clazz);
					if(c != null )
						bean = c.newInstance();
				}
				
				//
				Method[] methods = clazz.getMethods();
				for(Method m: methods) {
					if(m.getAnnotation(LoadOnStartup.class) != null) {
						Class<?>[] pts = m.getParameterTypes();
						if(pts == null || pts.length == 0) 
							m.invoke(bean, new Object[]{});
					}
				}
				
				//
				beans.put(e.getKey(), bean);
				logger.debug("LocalModule initBeans " + e.getKey() + ":" + bean);
			}catch (Exception e2) {
				logger.error("LocalModule["+this.id+"] initBeans fail when init bean(id["+beanId+"],class["+beanClass+"]), because of ", e2);
				throw new ModuleInitializeException(e2, this.id, this.path);
			}
			if(beans.containsKey(CacheProvider.class.getName())) {
				this.cacheProvider = (CacheProvider)beans.get(CacheProvider.class.getName());
			}
		}
	}
	
	private void initRequestServices() {
		logger.debug("Module initRequestServices");
		for(Entry<String, String> entry: moduleConfig.getRequests().entrySet()) {
			String type = entry.getKey();
			String service = entry.getValue();
			this.requestServices.put(type, (RequestService)getService(service));
		}
	}
	
	private void initDataConverters() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		logger.debug("Module initDataConverters");
		for(Entry<String, String> entry: moduleConfig.getConverters().entrySet()) {
			String type = entry.getKey();
			String service = entry.getValue();
			this.dataConverters.put(type, (DataConverter<?>)getService(service));
		}
	}
	
	private void initMethodAnnotationHandlers() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		logger.debug("Module initMethodAnnotationHandlers");
		for(Entry<String, String> entry: moduleConfig.getAnnotations().entrySet()) {
			String type = entry.getKey();
			String service = entry.getValue();
			this.annotationHandlers.put(type, (ServiceHandler)getService(service));
		}
	}
	
	public void initEvents() {
		logger.debug("Module initEvents");
		List<String[]> eventConfigs = this.moduleConfig.getEvents();
		for(String[] ss: eventConfigs) {
			this.events.add(new ModuleEvent(ss[1], ss[0]));
		}
	}
	
	public void fireEvents(String eventType) {
		logger.info("Module["+this.id+"] fireEvents " + eventType);
		if(eventType == null) return;
		for(ModuleEvent me: this.events) {
			if(eventType.equalsIgnoreCase(me.getType()))
				me.fire();
		}
	}

	public String getPath() {
		return path.endsWith(ConfigUtils.fileSeparator)? path : path + ConfigUtils.fileSeparator;
	}

	public String getName() {
		return this.moduleConfig.getName();
	}

	public String getDesc() {
		return this.moduleConfig.getDesc();
	}
	
	public ModuleConfig getModuleConfig() {
		return this.moduleConfig;
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	
	public Map<String, Object> getBeans() {
		return this.beans;
	}
	
	public Map<String, RequestService> getRequestServices() {
		return this.requestServices;
	}
	
	public boolean canHandleRequest(String requestType) {
		return this.requestServices.containsKey(requestType);
	}
	
	public RequestService getRequestService(String requestType) {
		if(!this.requestServices.containsKey(requestType)) throw new ServiceNotFoundException(requestType);
		return this.requestServices.get(requestType);
	}
	
	public DataConverter getDataConverter(String requestType) {
		if(!this.dataConverters.containsKey(requestType)) throw new ServiceNotFoundException(requestType);
		return this.dataConverters.get(requestType);
	}

	public ServiceHandler getAnnotationHandler(String annotationClassName) {
		if(!this.annotationHandlers.containsKey(annotationClassName)) return null;
		return this.annotationHandlers.get(annotationClassName);
	}
	
	public List<RequestFilter> getFilters(String uri) {
		if(this.filters.containsKey(uri)) return filters.get(uri);
		synchronized(this) {
			if(this.filters.containsKey(uri)) return filters.get(uri);
			List<RequestFilter> filters = Utils.newArrayList();
			List<String> configList = this.moduleConfig.getFilters();
			if(configList != null)
			for(String item: configList) {
				ServiceConfig serviceConfig = this.moduleConfig.getServiceConfig(item);
				if(serviceConfig == null) throw new ServiceNotFoundException(item);
				try {
					RequestFilter filter = (RequestFilter)getService(item);
					if(filter.isMapping(uri))
						filters.add(filter);
				} catch (Exception e) {
					logger.warn("Initialize filter " + item + " fail, ignored!, reason is :", e);
				}
			}
			this.filters.put(uri, filters);
			return filters;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String id) {
		return (T)this.beans.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getService(String serviceId) {
		if(this.services.containsKey(serviceId)) {
			return (T)this.services.get(serviceId);
		} else {
			Object service = this.serviceLoader.loadService(serviceId);
			ServiceConfig config = this.moduleConfig.getServiceConfig(serviceId);
			if(config.isSingleInstance()) {
				this.services.put(serviceId, service);
				if(config.getType() == ServiceConfig.Type.Class) {
					String className = config.getTarget();
					this.classServices.put(className, serviceId);
				}
			}
			return (T)service;
		}
	}
	
	public <T> T getService(Class<?> cls) {
		return getService(cls.getName());
	}

	public DataDecorator getParameterDecorator() {
		return newBeanInstance(DataDecorator.class);
	}
	
	public SpringBeanFactory getSpringBeanFactory() {
		return newBeanInstance(SpringBeanFactory.class);
	}
	
	public Repository getMetadataRepository() {
		return metadataRepository;
	}
	
	public boolean containsVariable(String key) {
		return this.moduleVariables.containsKey(key);
	}
	
	public void setVariable(String key, Object val) {
		this.moduleVariables.put(key, val);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getVariable(String key) {
		return (T)this.moduleVariables.get(key);
	}

	public RequestService newRequestService(final String type) {
		return (RequestService)this.getBean("service.request." + type);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T newBeanInstance(Class<T> t) {
		try {
			return (T)getBean(t.getName());
		} catch (Exception e) {
			logger.error("newBeanInstance fail!", e);
			return null;
		}
	}

	private Map<String, Class<?>> classes = Utils.newHashMap();
	public Class<?> newClass(String className, boolean initialize) throws ClassNotFoundException {
		if(classes.containsKey(className))
			return classes.get(className);
		else {
			Class<?> c = Class.forName(className, initialize, classLoader);
			classes.put(className, c);
			return c;
		}
	}
	
	public Class<?> newClass(String className) throws ClassNotFoundException {
		return newClass(className, true);
	}
	
	public CacheProvider getCacheProvider() {
		return this.cacheProvider;
	}
	
	public void release() {
		this.workingStatus = false;
		this.fireEvents("unload");
	}
	
	public boolean isWorking() {
		return this.workingStatus;
	}
	
	@Override
	public Data invoke(String serviceId, Data request) {
		
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();

		Data response = null;
		try {
			Thread.currentThread().setContextClassLoader(this.getClassLoader());
			
			response = ModuleServiceInvoker.invoke(this, request, serviceId);
			
			return response;
		} finally {
			Thread.currentThread().setContextClassLoader(threadClassLoader);
		}
	}
	
	public String toString() {
		return "Module " + this.getId() + " @ " + this.path;
	}
	
	class ModuleEvent {
		private final String type;
		private final String serviceId;

		public ModuleEvent(String serviceId, String type) {
			this.serviceId = serviceId;
			this.type = type;
		}

		public String getServiceId() {
			return serviceId;
		}

		public String getType() {
			return type;
		}
		
		public void fire() {
			invoke(serviceId, null);
		}
		
		public String toString() {
			return "Event[" + type +"], Service:[" + serviceId + "@" + id + "]";
		}
	}
}
