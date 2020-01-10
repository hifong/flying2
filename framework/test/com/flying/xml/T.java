package com.flying.xml;

import java.io.File;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

public class T {
	public static void main(String[] args) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File("C:\\work\\mocp\\frameworksrc\\WebRoot\\1.xml"));
		System.out.println(doc);
	}
}
