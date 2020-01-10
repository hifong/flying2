package com.flying.framework.server;
import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {
	/**
	 * @param args;
	 * args[0]:port, default 8080
	 * args[1]:logHome,default webHome/logs/
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final int port = args.length > 0?Integer.parseInt(args[0]): 8080;
		final String webHome = getWebHomeFromClass();
		final String logHome = args.length > 1? args[1]: webHome + "/logs/";
		System.setProperty("logs", logHome);
		
		Server server = new Server(port);

		WebAppContext context = new WebAppContext();
		context.setDescriptor(webHome + "/WEB-INF/web.xml");
		context.setResourceBase(webHome);
		context.setParentLoaderPriority(true);
		server.setHandler(context);

		server.start();
		server.join();
	}
	
	private static String getWebHomeFromClass() throws Exception {
		File file = new File(JettyServer.class.getClassLoader().getResource("").toURI());
		final String path = file.getAbsolutePath();
		final int webi = path.lastIndexOf("WEB-INF");
		return path.substring(0, webi);
	}
}
