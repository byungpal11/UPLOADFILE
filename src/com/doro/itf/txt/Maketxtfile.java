package com.doro.itf.txt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.doro.itf.db.DbconnectManager;
import com.doro.itf.log.LogMgr;
import com.doro.itf.properties.Property;

public class Maketxtfile extends Thread {

	private Connection dbConn;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	private List<String> dataList = new ArrayList<String>();
	private List<String> iccode = new ArrayList<String>();
	private HashMap<String, String> deptcode = new HashMap<String, String>();

	private int ic_count = 0;
	private boolean runnable = false;

	private DbconnectManager dbconnection = null;
	private LogMgr log =null;
	private Property property =null;

	public Maketxtfile() {

		init();
		dbconnection = DbconnectManager.getInstance();
		log = LogMgr.getInstance();
		property =new Property();

	}

	public void init() {

		if (!dataList.isEmpty())
			dataList.clear();
		if (!iccode.isEmpty())
			iccode.clear();
		if (!deptcode.isEmpty())
			deptcode.clear();

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

	public void iccodein() throws IOException {

		String sql = "";

		
		try {
			sql = "SELECT COUNT (DISTINCT TOLOF_CD) FROM T_ITAC_LDNG_WIGT_MSRMT01D1";
			pstmt = dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			rs.next();
			ic_count = Integer.parseInt(rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	
		log.writeLog("IC_CODE COUNT :" +ic_count, false);


		try {
			
		sql = "SELECT DISTINCT TOLOF_CD FROM T_ITAC_LDNG_WIGT_MSRMT01D1";
			pstmt = dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {	
				iccode.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		
		log.writeLog("In IC_CODE Search complete", false);

		pstmtclose();
		rsclose();

	}

	public void officecode() throws IOException {
		String sql = "";
		sql = "SELECT IC_CODE, DEPT_CODE  FROM ITBA_ICINFO_MST";

		try {
			pstmt = dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				deptcode.put(rs.getString("IC_CODE"), rs.getString("DEPT_CODE"));
			}
		} catch (SQLException e) {		
			e.printStackTrace();
		}

		log.writeLog("In officecode Search complete", false);
		pstmtclose();
		rsclose();

	}

	public void filenameupdate() {

		HashMap<String, String> selmap = new HashMap<String, String>();
		HashMap<String, String> filemap = new HashMap<String, String>();
		HashMap<String, String> updatemap = new HashMap<String, String>();

		String key = "";
		String valTmp = "";
		String sql = "";
		String strdir ="";

		// DB File select	
		try {
			sql = "SELECT WOUTP_DETL_DATES,TOLOF_CD,SSCT_SQNO,SAVE_PHTG_NM from T_ITAC_LDNG_WIGT_MSRMT01D1 where WOUTP_DETL_DATES LIKE TO_CHAR(SYSDATE-1,'YYYYMMDD')||'%'";
			pstmt = dbConn.prepareStatement(sql);
			rs = pstmt.executeQuery();

		while (rs.next()) {

			String strkey = "";
			strkey = rs.getString("WOUTP_DETL_DATES") + ";" + rs.getString("TOLOF_CD") + ";" + rs.getString("SSCT_SQNO")
					+ ";";
			selmap.put(rs.getString("SAVE_PHTG_NM"), strkey);
		}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		
		// file map save


		try {
			strdir = property.ReadConfig("IMAGERENAMEPATH");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//String strdir = "D:\\TEST/DownLoad/";

		String FileName = "";

		LocalDateTime currenttime =LocalDateTime.now();
		LocalDateTime yesterday = currenttime.minusDays(1);
		DateTimeFormatter fomat_year = DateTimeFormatter.ofPattern("yyyy");
		DateTimeFormatter fomat_month = DateTimeFormatter.ofPattern("MM");
		DateTimeFormatter fomat_day = DateTimeFormatter.ofPattern("dd");

		strdir += yesterday.format(fomat_year) + "/";
		strdir += yesterday.format(fomat_month) + "/";
		strdir += yesterday.format(fomat_day) + "/";
		File path = new File(strdir);
		System.out.println(strdir);

		File[] fList;
		fList = path.listFiles();

		for (int i = 0; i < fList.length; i++) {

			FileName = fList[i].getName();
			// System.out.println(FileName);

			String strPath = strdir + FileName + "/";

			File ImagePath = new File(strPath);
			System.out.println(strPath);
			File[] fList1;
			fList1 = ImagePath.listFiles();

			for (int j = 0; j < fList1.length; j++) {
				if (fList1[j].isDirectory())
					continue;

				String strName = "";
				strName = fList1[j].getName();
				System.out.println(strName);

				filemap.put(strName, "");

			}

		}

		for (String keySet : selmap.keySet()) {
			key = null;

			if (keySet.indexOf(";") > 0) {
				valTmp = "";

				key = keySet.split(";")[0];
				if (filemap.get(key) != null) {
					valTmp = keySet.split(";")[0];

				}

				key = keySet.split(";")[1];
				if (filemap.get(key) != null) {
					if (!valTmp.equals("")) {
						valTmp += ";";
						valTmp += keySet.split(";")[1];
					} else {
						valTmp = keySet.split(";")[1];
					}

				}

				if (!keySet.equals(valTmp)) {
					updatemap.put(selmap.get(keySet), valTmp);
				}

			} else {
				if (filemap.get(keySet) == null) {
					updatemap.put(selmap.get(keySet), "");
				}
			}

		}

		
		try {
			sql = "UPDATE T_ITAC_LDNG_WIGT_MSRMT01D1 SET SAVE_PHTG_NM =? WHERE WOUTP_DETL_DATES=? AND TOLOF_CD=? AND SSCT_SQNO=?";
			pstmt = dbConn.prepareStatement(sql);
			for (String str : updatemap.keySet()) {

				String[] Array = str.split(";");
	
				dbConn.setAutoCommit(false);
	
				int i = 1;
				pstmt.setString(i++, updatemap.get(str));
				pstmt.setString(i++, Array[0]);
				pstmt.setString(i++, Array[1]);
				pstmt.setString(i++, Array[2]);
	
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			dbConn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	
		pstmtclose();
		rsclose();

	}

	public void dbselect() {

		String sql = "";
		String strdata = "";

		sql = " with lowta as \n" + "(SELECT T_WIGT.*,T_ICCODE.*,\n" + "    CASE WHEN SAVE_PHTG_NM IS NULL THEN ''\n"
				+ "    ELSE\n" + "        CASE WHEN nvl(INSTR(SAVE_PHTG_NM, ';'), 0) =0 \n"
				+ "            THEN DEPT_CODE||'_'||WOUTP_DETL_DATES||'_'||DSCS_CRGW_NM||'_'|| LPAD(SSCT_SQNO, 5, 0)||'_F.jpg' \n"
				+ "        ELSE DEPT_CODE||'_'||WOUTP_DETL_DATES||'_'||DSCS_CRGW_NM||'_'|| LPAD(SSCT_SQNO, 5, 0)||'_F.jpg;'||\n"
				+ "            DEPT_CODE||'_'||WOUTP_DETL_DATES||'_'||DSCS_CRGW_NM||'_'|| LPAD(SSCT_SQNO, 5, 0)||'_B.jpg'\n"
				+ "        END\n" + "    END SAVE_PHTG_NM_T\n"
				+ "FROM T_ITAC_LDNG_WIGT_MSRMT01D1 T_WIGT,ITBA_ICINFO_MST T_ICCODE\n"
				+ "where  WOUTP_DETL_DATES LIKE TO_CHAR(SYSDATE-1, 'YYYYMMDD')||'%' AND TOLOF_CD =?\n"
				+ "AND T_ICCODE.IC_CODE = T_WIGT.TOLOF_CD)\n"
				+ "select TO_CHAR(SYSDATE-1, 'YYYYMMDD')||'|'||count(1)||'|'||TO_CHAR(SYSDATE-1, 'YYYYMMDD')||'000000|'||TO_CHAR(SYSDATE-1, 'YYYYMMDD')||'235959|0|' a \n"
				+ "from lowta\n" + "union all\n"
				+ "SELECT WOUTP_DETL_DATES||'|'||DEPT_CODE||'|'||SSCT_SQNO||'|'||VHCL_NM||'|'||DSCS_CRGW_NM||'|'||KNCR_CLSS_CD||'|'||AXIS_CNT||'|'||ETRY_SPED||'|'||STNW_ID||'|'||TOT_WIGT_VAL\n"
				+ "||'|'||ODN1_ITM_CTNT||'|'||ODN2_ITM_CTNT||'|'||ODN3_ITM_CTNT||'|'||ODN4_ITM_CTNT||'|'||ODN5_ITM_CTNT||'|'||ODN6_ITM_CTNT||'|'||ODN7_ITM_CTNT||'|'||ODN8_ITM_CTNT||'|'||ODN9_ITM_CTNT||'|'||ODN10_ITM_CTNT\n"
				+ "||'|'||ODN11_ITM_CTNT||'|'||ODN12_ITM_CTNT||'|'||ODN13_ITM_CTNT||'|'||ODN14_ITM_CTNT||'|'||ODN15_ITM_CTNT||'|'||ODN16_ITM_CTNT||'|'||ODN17_ITM_CTNT||'|'||ODN18_ITM_CTNT||'|'||SPCC_EXCSS_YN||'|'\n"
				+ "||SAVE_PHTG_NM_T\n"
				+ "||'|'||MISG_YN||'|'||RPRM_DETL_DATES||'|'||ENRC_CRGW_NO||'|'||OBU_NO FROM lowta\n" + "";

		try {
			pstmt = dbConn.prepareStatement(sql);
			for (int i = 0; i < ic_count; i++) {
				pstmt.setString(1, iccode.get(i).toString());
	
				rs = pstmt.executeQuery();
	
				while (rs.next()) {
	
					strdata += rs.getString(1);
					strdata += "\r\n";
	
				}
				WriteFile(strdata, iccode.get(i).toString());
				strdata = "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		pstmtclose();
		rsclose();

	}

	public void WriteFile(String str, String Iccode) {

		String Path = "";

		LocalDateTime curretdate = LocalDateTime.now();
		DateTimeFormatter fomat_curretndate = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		FileWriter writer = null;

		String FileName = "";
		FileName += "PVFUTR$";
		FileName += "ex";
		FileName += "_" + deptcode.get(Iccode);
		FileName += "_" + curretdate.format(fomat_curretndate);
		FileName += ".txt";
		try {
			Path = property.ReadConfig("WRITEPATH") + FileName;
		} catch (IOException e) {

			e.printStackTrace();
		}
		//Path = "D:/TEST/" + FileName;

		try {
			writer = new FileWriter(Path, true);
			writer.write(str);
			writer.write("\r\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public void run() {
		
		try {
			dbstart();
			//iccodein();
			//officecode();
			//filenameupdate();
			//dbselect();
			dbclose();
			log.writeLog("MAKE TXT FILE COMPLETE...", false);		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
