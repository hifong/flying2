package com.flying.framework.module;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.flying.common.config.ConfigUtils;
import com.flying.common.http.DownloadUtils;
import com.flying.common.log.Logger;
import com.flying.common.util.StringUtils;
import com.flying.common.util.Utils;
import com.flying.common.util.zip.UnZip;
import com.flying.framework.data.Data;
import com.flying.framework.exception.ModuleNotFoundException;

/**
 * 管理Module的生命周期
 * @author wanghaifeng
 *
 */
public class Modules {
	private final static Logger logger = Logger.getLogger(Modules.class);
	
	private Map<String, Module> modules = Utils.newHashMap();
	
	public Modules(Data moduleConfigs) {
		synchronized(this) {
			modules.clear();
			new Thread(new ModulesLoader(moduleConfigs)).start();
		}
	}
	
	private final String getRealPath(String path) {
		if(path.indexOf("$") < 0) return path;
		
		path = StringUtils.replace(path, "\\", "/");
		String[] items = StringUtils.split(path, "/");
		for(int i=0; i< items.length; i++) {
			if(items[i].startsWith("$")) {
				final String name = items[i].substring(1);
				String x = System.getenv(name);
				if(StringUtils.isBlank(x)) 
					x = System.getProperty(name);
				if(!StringUtils.isBlank(x))
					items[i] = x;
			}
		}
		path = "";
		for(String p: items) {
			path += p;
		}
		return path;
	}
	
	public void loadModule(String id, String locate, String path, Data configs) throws Exception {
		Module module = load(id, locate, getRealPath(path), configs);
		//
		logger.debug("Module " + module.getId() + " registered success!");
		if(modules.containsKey(module.getId())) {
			unloadModule(module.getId());
		}
		modules.put(module.getId(), module);
		//
		module.fireEvents("load");
	}
	
	private void loadModule(ModuleInfo m) {
		try {
			loadModule(m.id, m.locate, m.path, m.configs);
		} catch (Exception e) {
			logger.error("load module " + m.id + "(path:" + m.path + ") fail, because of :" + e, e);
		}
	}
	
	public void unloadModule(String id) {
		if(!modules.containsKey(id)) return;
		logger.debug("Module " + id + " unregistered success!");
		Module module = modules.get(id);
		if(module instanceof LocalModule) {
			((LocalModule)module).release();
		} else {
			module.fireEvents("unload");
		}
		try {
			Thread.sleep(3000);
		} catch (Exception e) {}
		modules.remove(id);
	}

	private Module load(String id, String locate, String path, Data configs) throws Exception {
		if("remote".equals(locate)) {
			return RemoteModule.newInstance(id, path, configs);
		} else {
			File f = new File(path);
			if(f.isDirectory()) {
				return LocalModule.newInstance(id, path, configs);
			} else if(f.isFile() && (path.toLowerCase().endsWith(".mar") || path.toLowerCase().endsWith(".zip") )) {
				String mdir = ConfigUtils.getConfigHome() + "/temp/" + id + System.currentTimeMillis();
				File fmdir = new File(mdir);
				fmdir.mkdirs();
				//
				logger.debug("unzip " + path +" to " + mdir);
				UnZip.unzip(path, mdir);
				//
				return LocalModule.newInstance(id, mdir, configs);
			} else if(path.toLowerCase().startsWith("http")) {
				String mdir = ConfigUtils.getConfigHome() + "/temp/" + id + System.currentTimeMillis();
				String mfile = mdir +".mar";
				//
				logger.debug("downloading " + path +" to " + mfile);
				DownloadUtils.downloadFile(path, mfile);
				//
				File fmdir = new File(mdir);
				fmdir.mkdirs();
				//
				logger.debug("unzip " + mfile +" to " + mdir);
				UnZip.unzip(mfile, mdir);
				//
				return LocalModule.newInstance(id, mdir, configs);
			} else {
				throw new Exception("module[id:" + id +", path:" + path + "] load error!");
			}
		}
	}

	public Map<String, LocalModule> getLocalModules() {
		Map<String, LocalModule> ms = Utils.newHashMap();
		for(Module m: modules.values()) {
			if(m instanceof LocalModule) {
				ms.put(m.getId(), (LocalModule)m);
			}
		}
		return ms;
	}
	
	public Module getModule(String id) {
		if(!modules.containsKey(id)) throw new ModuleNotFoundException(id);
		Module m = modules.get(id);
		return m;
	}
	
	public LocalModule getLocalModule(String id) {
		Module m = getModule(id);
		if(!(m instanceof LocalModule)) throw new ModuleNotFoundException(id);
		return (LocalModule)m;
	}
	
	public RemoteModule getRemoteModule(String id) {
		Module m = getModule(id);
		if(!(m instanceof RemoteModule)) throw new ModuleNotFoundException(id);
		return (RemoteModule)m;
	}
	
	public boolean exists(String id) {
		return modules.containsKey(id);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(String k: modules.keySet()) {
			sb.append(k + "@[" + modules.get(k).getPath() + "]\n");
		}
		return sb.toString();
	}
	
	class ModulesLoader implements Runnable {
		private Data moduleConfigs;
		
		ModulesLoader(Data moduleConfigs) {
			this.moduleConfigs = moduleConfigs;
		}
		
		@Override
		public void run() {
			modules.clear();
			
			List<ModuleInfo> minfos = Utils.newArrayList();
			for(String id: moduleConfigs.keys()) {
				Data m = moduleConfigs.getValue(id);
				String locate = m.getString("locate");
				String path = m.getString("path");
				int sort = m.getInt("sort", 0);
				minfos.add(new ModuleInfo(id, locate, path, sort, m.getValue("configs")));
			}
			minfos.stream().sorted().forEach(m -> loadModule(m));
		}
	}
	
	class ModuleInfo implements Comparable<ModuleInfo> {
		private final String id;
		private final String locate;
		private final String path;
		private final int sort;
		private final Data configs;
		
		ModuleInfo(String id, String locate, String path, int sort, Data configs) {
			this.id = id;
			this.locate = locate;
			this.path = path;
			this.sort = sort;
			this.configs = configs;
		}
		
		@Override
		public int compareTo(ModuleInfo o) {
			return this.sort - o.sort;
		}
		
	}
	
	class ModuleLoader implements Runnable {
		private String id;
		private String locate;
		private String path;
		private Data configs;
		
		ModuleLoader(String id, String locate, String path, Data configs) {
			this.id = id;
			this.locate = locate;
			this.path = path;
			this.configs = configs;
		}
		
		@Override
		public void run() {
			try {
				loadModule(id, locate, path, configs);
			} catch (Exception e) {
				logger.error("load module " + id + "(path:" + path + ") fail, because of :" + e, e);
			}
		}
		
	}
}
