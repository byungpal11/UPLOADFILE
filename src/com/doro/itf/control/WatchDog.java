package com.doro.itf.control;

import com.doro.itf.job.Imagedown;
import com.doro.itf.job.Service;
import com.doro.itf.job.Service2;
import com.doro.itf.log.LogMgr;

public class WatchDog extends Thread {

	private long lAlarmTime = 1 * 60 * 1000;
	private boolean runnable = true;
	public Service service = null;
	public Service2 service2 = null;
	public Imagedown imagedown = null;
	public LogMgr log =null;

	public WatchDog() {
		log =LogMgr.getInstance();
	}

	public boolean isRunnable() {
		return runnable;
	}

	public void doStart() {
		runnable = true;
		this.start();
	}

	public void stopThread() {
		runnable = false;
	}

	public void run() {

		while (runnable) {
			System.gc();
			try {
				if (service == null) {
					service = new Service();
				}
				if (!service.isAlive() || !service.isRunnable()) {
					log.writeLog("service start", true);
					service.dostart();
					
				}

				if (service2 == null) {
					service2 = new Service2();
				}
				if (!service2.isAlive() || !service2.isRunnable()) {
					log.writeLog("service2 start", true);
					service2.dostart();
				}

				// if (imagedown == null) {
				// 	imagedown = new Imagedown();
				// }
				// if (!imagedown.isAlive() || !imagedown.isRunnable()) {
				// 	log.writeLog("imagedown start", true);
				// 	imagedown.dostart();
				// }

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					Thread.sleep(lAlarmTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
