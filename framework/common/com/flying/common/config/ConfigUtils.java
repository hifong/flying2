package com.flying.common.config;

import java.io.File;

import com.flying.framework.exception.AppException;

public class ConfigUtils {
	public final static String fileSeparator = System.getProperty("file.separator");
    public final static String LOG_BACKUP_SUFFIX = ".bak";
    
    private static boolean hadInitialized = false;
	
    public synchronized static void setConfigHome(String home) {
    	assert home != null: "Config home cann't be null!";
    	assert new File(home).isDirectory() && new File(home).exists() : "Config home must be a valid file path!";
    	assert !hadInitialized: "Config home had been initialized as " + System.getProperty("APP_CONFIG_HOME");
		if(home != null && !home.endsWith(fileSeparator)) {
			home += fileSeparator;
		}
		hadInitialized = true;
		System.setProperty("APP_CONFIG_HOME", home);
    }
    
	public static String getConfigHome() {
		String home = System.getProperty("APP_CONFIG_HOME");
		if(home == null && !hadInitialized) {
			synchronized(ConfigUtils.class) {
				try {
					File f = new File(Thread.currentThread().getContextClassLoader().getResource("/").toURI());
					home = f.getAbsolutePath();
					if(home != null && !home.endsWith(fileSeparator)) {
						home += fileSeparator;
					}
					System.setProperty("APP_CONFIG_HOME", home);
				} catch (Exception e) {
					try {
						File f = new File(Thread.currentThread().getContextClassLoader().getResource(".").toURI());
						home = f.getAbsolutePath();
						if(home != null && !home.endsWith(fileSeparator)) {
							home += fileSeparator;
						}
						System.setProperty("APP_CONFIG_HOME", home);
					} catch (Exception e2) {
						throw new AppException(e, "APP_CONFIG_HOME not configured!");
					}
				}
				hadInitialized = true;
			}
		}
		return home;
	}
	
	public static String getFilePath(String file) {
		return getConfigHome() + file;
	}
	
	public static void main(String[] args) {
		ConfigUtils.setConfigHome("E:\\a");
	}
}
