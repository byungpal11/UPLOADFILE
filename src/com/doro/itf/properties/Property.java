package com.doro.itf.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Property {
	public Property() {

	}

	public String ReadConfig(String str) throws IOException {
		String msg;

		String propFile = "./conf/Uploadfileconfig.properties";

		Properties props = new Properties();

		FileInputStream fis = new FileInputStream(propFile);

		props.load(new java.io.BufferedInputStream(fis));

		msg = props.getProperty(str);

		return msg;

	}

}
