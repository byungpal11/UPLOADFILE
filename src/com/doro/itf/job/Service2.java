package com.doro.itf.job;

import java.io.IOException;
import java.text.SimpleDateFormat;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.doro.itf.image.ImageRename;
import com.doro.itf.log.LogMgr;

public class Service2 extends Thread {

	private boolean runnable = false;
	public LogMgr log = null;
	public ImageRename imagerename = null;

	public Service2() {

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

	public void ImagerenameStart() throws IOException {

		Date currettime = new Date(System.currentTimeMillis());
		SimpleDateFormat format_HOUR= new SimpleDateFormat("HHmm");
		// LocalDateTime currenttime = LocalDateTime.now();
		// DateTimeFormatter format_HOUR = DateTimeFormatter.ofPattern("HHmm");
		String HOUR = format_HOUR.format(currettime);

		if (HOUR.equals("0030")) {

			imagerename = new ImageRename();
			log.writeLog("Image ReName START", true);
			imagerename.doStart();

		} else {
			System.out.println(HOUR);
		}

	}

	public void run() {

		while (runnable) {
			try {
				Thread.sleep(1000);
				ImagerenameStart();
			} catch (InterruptedException | IOException e1) {

				e1.printStackTrace();
			}
		}

	}

}
