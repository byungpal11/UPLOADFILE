package com.doro.itf.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
import java.util.Date;

//LOG
public class LogMgr {

	private volatile static LogMgr instance = null;
	public static LogMgr g;

	public LogMgr() {

	}

	public static LogMgr getInstance() {
		if (instance == null) {
			synchronized (LogMgr.class) {
				if (instance == null) {
					instance = new LogMgr();
				}
			}
		}
		return instance;
	}

	public void writeLog(String strlog, boolean print) throws IOException {
		String path = logPath();
		String description = logDescription();
		File dir = new File("./log");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		try (FileWriter writer = new FileWriter(path, true)) {
			writer.write(description);
			writer.write(strlog);
			writer.write("\r\n");
			if (print) {
				System.out.println(strlog);
			}
		}
	}

	public static String logPath() {
		String strlogpath;

		Date currenttime =new Date(System.currentTimeMillis());
		SimpleDateFormat format_currenttime =new SimpleDateFormat("yyyyMMdd");
		// LocalDateTime currentdate = LocalDateTime.now();
		// DateTimeFormatter fomat_currentdate = DateTimeFormatter.ofPattern("yyyyMMdd");
		strlogpath = "./log/" + format_currenttime.format(currenttime) + "UPLOADFILE.log";

		return strlogpath;

	}

	public static String logDescription() {
		String logDescription;
		Date currenttime =new Date(System.currentTimeMillis());
		SimpleDateFormat format_currenttime =new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
		// LocalDateTime currentdate = LocalDateTime.now();
		// DateTimeFormatter fomat_currentdate = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss.SSS");

		logDescription = "[" + format_currenttime.format(currenttime) + "]:";

		return logDescription;
	}



}
