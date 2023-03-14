package com.doro.itf.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.doro.itf.log.LogMgr;
import com.doro.itf.properties.Property;

public class Imagezip {

	private Property property = null;
	private LogMgr log =null;

	public Imagezip() {
		property = new Property();
		log = LogMgr.getInstance();
	}

	public void makeimagezip() {
		ZipOutputStream zipout = null;
		FileInputStream in = null;

		//String strPath = "../../../WIGT_DATA/DownLoad/";
		String strPath ="";
		try {
			strPath = property.ReadConfig("IMAGEZIP");
		} catch (IOException e) {
	
			e.printStackTrace();
		}

		LocalDateTime currenttime = LocalDateTime.now();
		LocalDateTime yesterday = currenttime.minusDays(1);
		DateTimeFormatter format_year = DateTimeFormatter.ofPattern("yyyy");
		DateTimeFormatter format_month = DateTimeFormatter.ofPattern("MM");
		DateTimeFormatter format_day = DateTimeFormatter.ofPattern("dd");
		DateTimeFormatter format_yesterday = DateTimeFormatter.ofPattern("yyyyMMdd");

		strPath += yesterday.format(format_year) + "/";
		strPath += yesterday.format(format_month) + "/";
		strPath += yesterday.format(format_day)+ "/";
		File Path = new File(strPath);
		File[] ListPath;
		ListPath = Path.listFiles();

		for (int j = 0; j < ListPath.length; j++) {

			System.out.println(ListPath[j].getName());

			String ZipPath = strPath + ListPath[j].getName() + "/RENAME/";
			File filePath = new File(ZipPath);
			System.out.println(ZipPath);
			String MakezipPath = "../../../WIGT_DATA/file/send/CG161363503438/";

			byte[] buf = new byte[4096];
			File[] fList;
			fList = filePath.listFiles();
			try {

				int FileNum = 1;
				String s = String.format("_%03d", FileNum);
				// String Sendstr = "rev1212$";
				String zipName = "PVFUTR$" + fList[0].getName().substring(0, 6) + "_"
						+ yesterday.format(format_yesterday) + s + ".zip";
				zipout = new ZipOutputStream(new FileOutputStream(MakezipPath + zipName));

				for (int i = 0; i < fList.length; i++) {

					if (fList[i].isDirectory())
						continue;

					in = new FileInputStream(fList[i]);
					zipout.putNextEntry(new ZipEntry(fList[i].getName()));
					int len = 0;
					while ((len = in.read(buf)) > 0) {
						zipout.write(buf, 0, len);
					}
					zipout.closeEntry();
					in.close();
					if (i % 2000 == 0 && i != 0) {
						zipout.close();
						FileNum++;
						s = String.format("_%03d", FileNum);
						zipName = "PVFUTR$" + fList[0].getName().substring(0, 6) + "_"
								+ yesterday.format(format_yesterday) + s + ".zip";
						zipout = new ZipOutputStream(new FileOutputStream(MakezipPath + zipName));
					}

				}
				zipout.close();
			} catch (Exception e) {
				System.out.println(e);
			}

		}
		try {
			log.writeLog("MAKE Image zip Complete", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
