package com.flying.framework.module;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang3.StringUtils;

import com.flying.common.log.Logger;
import com.flying.framework.annotation.scanner.ClassAnnotationScanner;

public class LocalModuleClassPathScanner {
	private final static Logger log = Logger.getLogger(LocalModuleClassPathScanner.class);
	
	private final LocalModule module;
	private final String classPath;
	
	public LocalModuleClassPathScanner(LocalModule module) {
		this.module = module;
		this.classPath = module.getModuleConfig().getClassesPath();
	}
	
	public void scan() {
		this.scan(new File(this.classPath));
	}
	
	private void scan(final File dir) {
		File[] files = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".class") && name.indexOf("$") < 0 || name.indexOf(".") < 0;
			}
			
		});
		for(File f: files) {
			if(f.isFile()) {
				//get class full name
				String fp = f.getAbsolutePath().substring(this.classPath.length());
				fp = fp.substring(0, fp.lastIndexOf("."));
				fp = StringUtils.replace(fp, File.separator, ".");
				//load class and check service annotation
				try {
					Class<?> cls = module.getClassLoader().loadClass(fp);
					ClassAnnotationScanner.scanClass(module, cls);
				} catch (ClassNotFoundException e) {
					log.warn("LocalModuleClassPathScanner load class "+fp +" fail ,because of " + e);
				}
			} else if(f.isDirectory()) {
				scan(f);
			}
		}
	}
}
