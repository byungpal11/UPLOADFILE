package com.doro.itf.image;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.doro.itf.db.DbconnectManager;
import com.doro.itf.log.LogMgr;
import com.doro.itf.properties.Property;
import com.doro.itf.zip.Imagezip;

public class ImageRename extends Thread {

	private Connection dbConn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private HashMap<String, String> imagename = new HashMap<String, String>();
	private Property property = null;
	private Imagezip imagezip = null;
	private DbconnectManager dbconnection = null;
	private boolean runnable = false;
	private LogMgr log = null;

	public ImageRename() {

		property = new Property();
		dbconnection = DbconnectManager.getInstance();
		log = LogMgr.getInstance();

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

	public void dbstart() throws IOException {

		try {
			dbConn = dbconnection.getConnection();
			log.writeLog("Database Connetion", false);
			System.out.println("Database Connetion");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Database Connetion error");
		}
	}

	public void pstmtclose() {
		try {
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public void rsclose() {

		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public void dbclose() {
		try {
			if (!dbConn.isClosed()) {
				dbConn.close();
				System.out.println("Database disConnetion");
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public void getimagename() {

		try {
			dbstart();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "";

		sql = "\n" + "SELECT \n" + "CASE WHEN  nvl(INSTR(SAVE_PHTG_NM, ';'), 0) =0 \n" + "        THEN SAVE_PHTG_NM \n"
				+ "        ELSE substr(SAVE_PHTG_NM,0,INSTR(SAVE_PHTG_NM, ';')-1)END a, \n"
				+ "CASE WHEN INSTR(SAVE_PHTG_NM, ';') =0 THEN NULL\n"
				+ "				ELSE substr(SAVE_PHTG_NM,INSTR(SAVE_PHTG_NM, ';')+1)END b ,\n"
				+ "                   CASE WHEN SAVE_PHTG_NM IS NULL THEN ''\n" + "                   ELSE\n"
				+ "				CASE WHEN INSTR(SAVE_PHTG_NM, ';') =0 \n"
				+ "				THEN DEPT_CODE||substr(SAVE_PHTG_NM,5,16)||DSCS_CRGW_NM||substr(SAVE_PHTG_NM,23,12)\n"
				+ "				ELSE DEPT_CODE||substr(SAVE_PHTG_NM,5,16)||DSCS_CRGW_NM||substr(SAVE_PHTG_NM,23,12)\n"
				+ "                END\n" + "				END aa,                \n"
				+ "                CASE WHEN INSTR(SAVE_PHTG_NM, ';') =0 THEN NULL\n" + "				ELSE \n"
				+ "                CASE WHEN SAVE_PHTG_NM IS NULL THEN ''\n" + "                ELSE\n"
				+ "                DEPT_CODE||substr(SAVE_PHTG_NM,40,16)||DSCS_CRGW_NM||substr(SAVE_PHTG_NM,58)\n"
				+ "                END\n" + "                END bb\n"
				+ "				FROM  T_ITAC_LDNG_WIGT_MSRMT01D1  T_WIGT,ITBA_ICINFO_MST T_ICCODE\n"
				+ "				WHERE  WOUTP_DETL_DATES LIKE TO_CHAR(SYSDATE-1, 'YYYYMMDD')||'%'\n"
				+ "				AND T_ICCODE.IC_CODE = T_WIGT.TOLOF_CD";

		try {
			pstmt = dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				imagename.put(rs.getString(1), rs.getString(3));
				imagename.put(rs.getString(2), rs.getString(4));

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pstmtclose();
		rsclose();

		dbclose();
	}

	public void Imagerename() throws IOException {
		String str = property.ReadConfig("IMAGERENAMEPATH");
		String FileName = null;

		Date dDate = new Date();
		Date date_Y = new Date(dDate.getTime() + (1000 * 60 * 60 * 24 * -1));
		SimpleDateFormat fourteen_format_Y = new SimpleDateFormat("yyyy");
		SimpleDateFormat fourteen_format_M = new SimpleDateFormat("MM");
		SimpleDateFormat fourteen_format_d = new SimpleDateFormat("dd");

		str += fourteen_format_Y.format(date_Y) + "/";
		str += fourteen_format_M.format(date_Y) + "/";
		str += fourteen_format_d.format(date_Y) + "/";
		File path = new File(str);

		File[] fList;
		fList = path.listFiles();

		for (int i = 0; i < fList.length; i++) {

			FileName = fList[i].getName();

			String strPath = str + FileName + "/";
			String strPaths = strPath + "RENAME/";

			File filePath = new File(strPaths);
			if (!filePath.exists())
				filePath.mkdir();

			File ImagePath = new File(strPath);
			File[] fList1;
			fList1 = ImagePath.listFiles();

			for (int j = 0; j < fList1.length; j++) {
				if (fList1[j].isDirectory())
					continue;
				String oriFilePath = strPath + fList1[j].getName();
				String copyFilePath = strPaths + imagename.get(fList1[j].getName());
				File oriFile = new File(oriFilePath);
				oriFile.renameTo(new File(copyFilePath));

			}

		}

		log.writeLog("Start make image zip", false);
		imagezip = new Imagezip();
		imagezip.makeimagezip();
	}

	public void run() {

		try {
			getimagename();
			Imagerename();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
