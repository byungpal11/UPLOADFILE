package com.doro.itf;

import java.io.IOException;

import com.doro.itf.control.WatchDog;


public class UpLoadFile {

	private UpLoadFile() {


	}

	public void go() throws IOException {
		WatchDog watchDog = new WatchDog();
		watchDog.doStart();

	}

	public static void main(String args[]) {

		try {
			UpLoadFile uploadfile = new UpLoadFile();
			uploadfile.go();

		} catch (IOException e) {
			System.out.println(e.toString());
		}

	}

}
