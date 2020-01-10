package com.flying.framework.web.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;

import com.flying.framework.application.Application;

public class InitListener implements ServletContextListener {
	
    public void contextDestroyed(ServletContextEvent arg0) {
        
    }

    public void contextInitialized(ServletContextEvent event) {
    	//webHome
    	final String webHome = event.getServletContext().getRealPath("");
    	System.setProperty("webHome", webHome);
    	System.out.println(">>>>webHome:" + webHome);
    	//---------------------------------------------------------
    	String configHome = System.getenv("configHome");
    	if(StringUtils.isBlank(configHome)){
    		configHome = System.getProperty("configHome");
    	}
    	if(StringUtils.isBlank(configHome)){
    		configHome = event.getServletContext().getInitParameter("configHome");
    	}
    	if(StringUtils.isBlank(configHome)){
    		configHome = event.getServletContext().getRealPath("WEB-INF/configs");
    		File configdir = new File(configHome);
    		if(!configdir.exists()) configHome = "";
    	}
    	if(StringUtils.isBlank(configHome)){
    		configHome = event.getServletContext().getRealPath("WEB-INF/classes");
    	}
    	System.out.println(">>>>configHome:" + configHome);
    	//---------------------------------------------------------
    	String logRoot = event.getServletContext().getInitParameter("logRoot");
    	if(StringUtils.isBlank(logRoot)){
    		logRoot = event.getServletContext().getInitParameter("logRoot");
    	}
    	if(StringUtils.isBlank(logRoot)){
    		logRoot = event.getServletContext().getRealPath("logs");
    	}
    	if(StringUtils.isBlank(logRoot)){
    		logRoot = System.getProperty("logs");
    	}
    	File logdir = new File(logRoot);
    	if(!logdir.exists()) logdir.mkdirs();
    	
    	System.setProperty("LOGROOT", logRoot);
    	//
		String configFile = configHome + File.separator + "log4j.xml";
		if(new File(configFile).exists()){
			DOMConfigurator.configureAndWatch(configFile);
		}
    	System.out.println(">>>>logRoot:" + logRoot);
    	//---------------------------------------------------------
    	Application app = Application.initApplication(configHome);
    }

}
