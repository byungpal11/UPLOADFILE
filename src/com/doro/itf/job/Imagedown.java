package com.doro.itf.job;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Vector;

import com.doro.itf.log.LogMgr;
import com.doro.itf.properties.Property;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Imagedown extends Thread {

	private boolean runnable = true;

	private Session Sftpsession = null;
	private ChannelSftp channelsftp = null;
	public Property property = null;
	private JSch jsch = null;
	private String sftpuser = "";
	private String sftppassword = "";
	private String sftpurl = "";
	private int sftpport = 0;
	private LogMgr log = null;

	public Imagedown() {

		property = new Property();
		jsch = new JSch();
		log = LogMgr.getInstance();
		try {
			sftpuser = property.ReadConfig("SFTPUSER");
			sftppassword = property.ReadConfig("SFTPPASSWORD");
			sftpurl = property.ReadConfig("SFTPURL");
			sftpport = Integer.parseInt(property.ReadConfig("SFTPPORT"));

		} catch (IOException e) {

			e.printStackTrace();
		}
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
	
	public void sftpdisconnction() throws IOException {
		if (channelsftp != null) {
			channelsftp.disconnect();	
		}
		if (Sftpsession != null) {
			Sftpsession.disconnect();
		}
		//log.writeLog("SFTP disConnection ", false);
		//System.out.println("SFTP disConnection ");
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void sftpdownload() throws IOException {

		Date currettime = new Date(System.currentTimeMillis());
		SimpleDateFormat format_YY= new SimpleDateFormat("yyyy");
		SimpleDateFormat format_MM = new SimpleDateFormat("MM");
		SimpleDateFormat format_DD= new SimpleDateFormat("dd");
		String year = format_YY.format(currettime);
		String month = format_MM.format(currettime);
		String day = format_DD.format(currettime);
		
		// LocalDateTime currentdate = LocalDateTime.now();
		// DateTimeFormatter formatter_month = DateTimeFormatter.ofPattern("MM");
		// String year = Integer.toString(currentdate.getYear());
		// String month = currentdate.format(formatter_month);
		// String day = Integer.toString(currentdate.getDayOfMonth());

		String remotedownloadpath = "";
		String localdownloadpath = "";
		try {
			remotedownloadpath = property.ReadConfig("REMOTEPATH");
			localdownloadpath = property.ReadConfig("LOCALPATH");
		} catch (IOException e) {
			log.writeLog("Read fail property ", false);
			e.printStackTrace();
		}

		remotedownloadpath += year + "/" + month + "/" + day + "/";

		localdownloadpath += year + "/";
		File localdownpath = new File(localdownloadpath);
		if (!localdownpath.exists())
			localdownpath.mkdir();
		localdownloadpath += month + "/";
		localdownpath = new File(localdownloadpath);
		if (!localdownpath.exists())
			localdownpath.mkdir();
		localdownloadpath += day + "/";
		localdownpath = new File(localdownloadpath);
		if (!localdownpath.exists())
			localdownpath.mkdir();

		try {
			Sftpsession = jsch.getSession(sftpuser, sftpurl, sftpport);
			Sftpsession.setPassword(sftppassword);
			Sftpsession.setConfig("StrictHostKeyChecking", "no");
			//og.writeLog("Sftp setconfig", false);
			System.out.println("Sftp setconfig ");

			Sftpsession.connect();
			//log.writeLog("Session Connection Success", false);
			System.out.println("Session Connection Success");

			channelsftp = (ChannelSftp) Sftpsession.openChannel("sftp");
			channelsftp.connect();
			//log.writeLog("channel Connection Success", false);
			System.out.println("channel Connection Success ");

			channelsftp.cd(remotedownloadpath);
			System.out.println(remotedownloadpath);

			Vector<ChannelSftp.LsEntry> files = channelsftp.ls(remotedownloadpath);

			for (int i = 0; i < files.size(); i++) {

				if (!files.get(i).getFilename().equals(".") && !files.get(i).getFilename().equals("..")
						&& !files.get(i).getFilename().startsWith("X") && files.get(i).getFilename().endsWith(".jpg")) {
					String strname = localdownloadpath + files.get(i).getFilename().substring(0, 4) + "/";
					File downdirectory = new File(strname);
					if (!downdirectory.exists())
						downdirectory.mkdir();
					//System.out.println(files.get(i).getFilename());
					// SFTP FILE DOWNLOAD
					channelsftp.get(remotedownloadpath + files.get(i).getFilename(),
							strname + files.get(i).getFilename());

					try {
						channelsftp.cd(remotedownloadpath);
						channelsftp.mkdir("SUCCESS");
						channelsftp.cd(remotedownloadpath + "SUCCESS/");
					} catch (Exception e) {
						channelsftp.cd(remotedownloadpath + "SUCCESS/");
					}
					// SFTP FILE MOVE
					channelsftp.rename(remotedownloadpath + files.get(i).getFilename(),
							remotedownloadpath + "SUCCESS/" + files.get(i).getFilename());
					// SFTPFILE DELETE
					// Channelsftp.rm(downloadpath+files.get(i).getFilename()); 
				}

			}

			sftpdisconnction();

		} catch (JSchException e) {

			e.printStackTrace();
			log.writeLog(e.toString(), false);
		} catch (SftpException e) {

			e.printStackTrace();
			log.writeLog(e.toString(), false);
		} catch (IOException e1) {

			e1.printStackTrace();
			log.writeLog(e1.toString(), false);

		} finally {
			sftpdisconnction();
		}
	}

	public void run() {

		while (runnable) {

			try {
				// Thread.sleep(lAlarmTime);
				Thread.sleep(5000);
				sftpdownload();
			} catch (InterruptedException | IOException e) {

				e.printStackTrace();

			}

		}

	}

}
