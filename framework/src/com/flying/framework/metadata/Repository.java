package com.flying.framework.metadata;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.JSONUtils;
import com.flying.common.util.Utils;
import com.flying.framework.exception.AppException;
import com.flying.framework.module.LocalModule;

public class Repository {
	private final static Logger logger = Logger.getLogger(Repository.class);
	
	private final Map<String, Metadata> repository = Utils.newHashMap();
	private LocalModule module;
	private String moduleHome;
	private final List<File> files = Utils.newArrayList();
	private final Map<String, Map<String, Object>> metadataData = Utils.newHashMap();
	
	public Repository(LocalModule module) {
		String metadata = module.getModuleConfig().getConfig("metadata-files");
		
		if(!StringUtils.isEmpty(metadata)) {
			this.module = module;
			this.moduleHome = module.getModuleConfig().getModuleHome();
			//
			String fs[] = StringUtils.split(metadata, ";");
			for(String f: fs) {
				files.addAll(scanFiles(this.moduleHome + "/" + f.trim()));
			}
			//
			this.load();
		}
	}
	
	public void buildMetadata(String key, Map<String, Object> data) {
		if(!data.containsKey(Metadata.ENTITY)) {	//对于Entity，加入自身后，扫码所有引用的View，构造Metadata
			this.repository.put(key, new Metadata(data, this));
			for(Iterator<String> it = metadataData.keySet().iterator(); it.hasNext();) {
				String metaId = it.next();
				Map<String, Object> mdata = metadataData.get(metaId);
				final String refEntity = (String)mdata.get(Metadata.ENTITY);
				if(key.equals(refEntity)) {
					it.remove();
					this.repository.put(metaId, new Metadata(mdata, this));
				}
			}
		} else {	//对于View，检查对应的Entity是否已经构造，没构造放入待处理中
			String entity = (String)data.get(Metadata.ENTITY);
			if(this.repository.containsKey(entity)) {
				this.repository.put(key, new Metadata(data, this));
			} else {
				this.metadataData.put(key, data);
			}
		}
	}
	
	private List<File> scanFiles(String source) {
		List<File> list = Utils.newArrayList();
		if(source == null) return list;
		
		File f = new File(source);
		if(!f.exists()) return list;
		
		if(f.isFile() && source.endsWith(".json")) {
			list.add(f);
			return list;
		} else if(f.isDirectory()){
			File[] fs = f.listFiles();
			for(File fi: fs){
				if(fi.isFile() && fi.getName().endsWith(".json")) {
					list.add(fi);
				} else if(fi.isDirectory()) {
					list.addAll(scanFiles(fi.getAbsolutePath()));
				} else {
					logger.warn("File " + fi.getAbsolutePath() +" not a valid metadata file!");
				}
			}
		} else {
			logger.warn("File " + source +" not a valid metadata file!");
		}
		
		return list;
	}
	
	public Map<String, Metadata> getRepository(){
		return this.repository;
	}
	
	public LocalModule getModule() {
		return this.module;
	}
	
	public Metadata getMetadata(String key) {
		if(!this.repository.containsKey(key))
			throw new AppException(String.valueOf(Codes.OBJECT_NOT_EXISTS), "Metadata not found for entity/view:" + key);
		return this.repository.get(key);
	}
	
	public Metadata getMetadataAllowNull(String key) {
		return this.repository.get(key);
	}
	
	public synchronized void load() {
		this.repository.clear();
		this.metadataData.clear();
		//
		if(this.files != null)
		for(File f: files) {
			try {
				loadFromFile(f);
			} catch (Exception e) {
				logger.error("Load metadata fail from file["+moduleHome + f +"]", e);
			}
		}
	}
	
	
	private void loadFromFile(File file) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		StringBuffer sb = new StringBuffer();
		String line = raf.readLine();
		while(line != null) {
			sb.append(line);
			line = raf.readLine();
		}
		raf.close();
		JSONObject jo = (JSONObject)JSONObject.parse(new String(sb.toString().getBytes("ISO-8859-1"), "UTF-8"));
		
		for(Iterator<String> it = jo.keySet().iterator(); it.hasNext();) {
			String key = it.next().toString();
			Map<String, Object> data = JSONUtils.toMap(jo.getJSONObject(key));
			this.buildMetadata(key, data);
		}
		//
		logger.info("Parse metadata from file [" + file +"] successfully!");
	}
}
