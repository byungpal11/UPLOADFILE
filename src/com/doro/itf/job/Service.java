package com.doro.itf.job;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.doro.itf.log.LogMgr;
import com.doro.itf.txt.Maketxtfile;

public class Service extends Thread {

	private boolean runnable = false;
	public LogMgr log = null;
	public Maketxtfile makefile = null;

	public Service() {

		log = LogMgr.getInstance();

	}

	public boolean isRunnable() {
		return runnable;
	}

	public void dostart() {
		runnable = true;
		this.start();
	}

	public void stopThread() {
		runnable = false;
	}

	public void MaketxtStart() throws IOException {


		LocalDateTime currenttime = LocalDateTime.now();
		DateTimeFormatter format_HOUR = DateTimeFormatter.ofPattern("HHmm");
		String HOUR = currenttime.format(format_HOUR);

		if (HOUR.equals("0030")) {

			makefile = new Maketxtfile();
			log.writeLog("MAKE TXT FILE START", true);
			makefile.doStart();

		} else {

			// makefile = new Maketxtfile();
			// log.writeLog("MAKE TXT FILE START", true);
			// makefile.doStart();
			System.out.println(HOUR +"..");
		}

	}

	public void run() {

		while (runnable) {
			try {
				Thread.sleep(1000);
				MaketxtStart();
			} catch (InterruptedException | IOException e1) {

				e1.printStackTrace();
			}
		}

	}

}
