package com.czq.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.czq.entitiy.Sewage1;
import com.czq.util.DBHelp;

/**
 * @author apple configure-Sewage CUR(D no)
 */
public class SewageConfigServlet extends HttpServlet {
	private int sewageID;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
 		String flag = req.getParameter("requestFlag");
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		switch (flag.charAt(0)) {
		case 'C':// 新增
			printWriter.print(createRecord(req));
			try {
				System.out.println(sewageID);
				sendSewageid(sewageID);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 'U'://更新
			printWriter.print(updateRecord(req));
			try {
				System.out.println(sewageID);
				sendSewageid(sewageID);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 'R':// 查询所有站点信息
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonValue = new JSONObject();
			String station = req.getParameter("stationName");
			if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
				station = new String(station.getBytes("iso-8859-1"),"utf-8");
			}
			jsonArray = JSONArray.fromObject(readArrayList(station));//将数组对象转化成JSON数组对象
			jsonValue.element("result", jsonArray);//将Json数组转换成json对象
			printWriter.print(jsonValue);
			break;
		case 'D':
			break;
		}
		printWriter.flush();
		printWriter.close();
	}

	public static void sendSewageid(int sewageid) throws InterruptedException {
		try {
			Socket socket = new Socket("115.159.52.175", 8991);
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			byte[] req = new byte[3];
			int a = sewageid / 127;
			int b = sewageid % 127;
			req[0] = (byte) 35;
			req[1] = (byte) a;
			req[2] = (byte) b;
			// 接收服务器的相应
			byte[] reply = new byte[2];
			for (int j = 0; j < 2; j++) {
				os.write(req);
				os.flush();
				// 读两次
				is.read(reply);
				Thread.sleep(100L);
				is.read(reply);
				if (reply[0] == 35 || reply[1] == 35) {
					// 重新再发一次，否则
					System.out.println(reply[0]+" "+reply[1]);
					break;
				} else {
					Thread.sleep(10000L);
				}
			}
			socket.shutdownOutput();
			is.close();
			os.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String createRecord(HttpServletRequest req) throws IOException {
		String sql = "insert into dbo.sewage("; // 新增加站点
		String key = null;
		String value = null;
		Map map = new HashMap();
		String countyName = req.getParameter("countyName").trim();// 所属地区
		String controlID = req.getParameter("controlId").trim();// 控制系统ID
		sewageID = Integer.parseInt(req.getParameter("stationID").trim());
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			countyName = new String(countyName.getBytes("iso-8859-1"), "utf-8");
		}
		map.put("areaID", querySewageId(countyName));// 简介
		map.put("sewageID", sewageID);// 简介
		map.put("controlID", controlID);// 简介
		map.put("short_title", req.getParameter("shortTitle").trim());// 简介
		map.put("name", req.getParameter("name").trim());// 名称
		map.put("address", req.getParameter("address").trim());// 地址
		map.put("operationnum", req.getParameter("opNum").trim());// 运营编号
		map.put("coordinateX", req.getParameter("coordinateX").trim());
		map.put("coordinateY", req.getParameter("coordinateY").trim());
		map.put("detection1DL", req.getParameter("detection1dl".trim()));// T下限
		map.put("detection1UL", req.getParameter("detection1ul").trim());// T上限
		map.put("detection2DL", req.getParameter("detection2dl").trim());// PH下限
		map.put("detection2UL", req.getParameter("detection2ul".trim()));// PH上限
		map.put("detection3DL", req.getParameter("detection3dl").trim());// ORP下限
		map.put("detection3UL", req.getParameter("detection3ul").trim());// ORP上限
		map.put("detection5dl", req.getParameter("detection5dl").trim());// DO下限
		map.put("detection5ul", req.getParameter("detection5ul".trim()));// DO上限
		map.put("reduceNH3N", req.getParameter("reduceNH3N").trim());//
		map.put("reduceCOD", req.getParameter("reduceCOD").trim());
		map.put("reduceP", req.getParameter("reduceP").trim());
		map.put("runtimeperiod1", req.getParameter("runtimeperiod1").trim());// 风机运行时间
		map.put("stoptimeperiod1", req.getParameter("stoptimeperiod1").trim());// 风机停止时间
		map.put("runtimeperiod2", req.getParameter("runtimeperiod2").trim());// 混合液回流泵运行时间
		map.put("stoptimeperiod2", req.getParameter("stoptimeperiod2").trim());// 混合液回流泵停止时间
		map.put("runtimeperiod3", req.getParameter("runtimeperiod3").trim());// 污泥回流泵运行时间
		map.put("stoptimeperiod3", req.getParameter("stoptimeperiod3").trim());// 污泥回流泵停止时间时间
		map.put("runtimeperiod4", req.getParameter("DCF11").trim());// 电磁阀1运行时间
		map.put("stoptimeperiod4", req.getParameter("DCF12").trim());// 电磁阀1停止时间
		map.put("runtimeperiod5", req.getParameter("DCF21").trim());// 电磁阀2运行时间
		map.put("stoptimeperiod5", req.getParameter("DCF22").trim());// 电磁阀2停止时间
		map.put("runtimeperiod6", req.getParameter("DCF31").trim());// 电磁阀3运行时间
		map.put("stoptimeperiod6", req.getParameter("DCF32").trim());// 电磁阀3停止时间
		map.put("runtimeperiod7", req.getParameter("DCF41").trim());// 电磁阀4运行时间
		map.put("stoptimeperiod7", req.getParameter("DCF42").trim());// 电磁阀4停止时间
		map.put("tonnage", req.getParameter("tonnage").trim());// 吨位
		map.put("videourl", req.getParameter("videourl").trim());// 视屏地址
		if (map.get("name") == null || "".equals(map.get("name").toString())
				|| map.get("short_title") == null
				|| "".equals(map.get("short_title").toString())
				|| countyName == null || "".equals(countyName)) {
			return "Unavailable key add";
		}

		if (isExisted(map.get("sewageID").toString())) {
			return "Have already existed";
		}

		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			String tempString = new String(req.getParameter("name").getBytes(
					"iso-8859-1"), "utf-8");
			map.put("name", tempString.trim());
			tempString = new String(req.getParameter("shortTitle").getBytes(
					"iso-8859-1"), "utf-8");
			map.put("short_title", tempString.trim());
			tempString = new String(req.getParameter("address").getBytes(
					"iso-8859-1"), "utf-8");
			map.put("address", tempString.trim());

		}

		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if (key == null) {
				key = entry.getKey().toString();
				value = "'" + entry.getValue().toString() + "'";
			} else {
				key += "," + entry.getKey().toString();
				value += ",'" + entry.getValue().toString() + "'";
			}

		}
		sql += key + " ) values (" + value + ")";
		System.out.println("add sql-->" + sql);

		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			return "Add successfully";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed";
		} finally {
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}

	private String updateRecord(HttpServletRequest req) throws IOException {
		String sql = "update dbo.sewage set "; // 修改站点
		String key = null;
		String value = null;
		Map map = new HashMap();
		String countyName = req.getParameter("countyName").trim();// 所属地区
		String controlID = req.getParameter("controlId").trim();// 控制系统ID

		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			countyName = new String(countyName.getBytes("iso-8859-1"), "utf-8");

		}
		String sewageID = req.getParameter("stationID").trim();
		map.put("areaID", querySewageId(countyName));// 简介
		//map.put("sewageID", sewageID);// 简介
		map.put("controlID", controlID);// 简介
		map.put("short_title", req.getParameter("shortTitle").trim());// 简介
		map.put("name", req.getParameter("name").trim());// 名称
		map.put("address", req.getParameter("address").trim());// 地址
	
		map.put("operationnum", req.getParameter("opNum").trim());// 运营编号
	
		map.put("coordinateX", req.getParameter("coordinateX").trim());
		map.put("coordinateY", req.getParameter("coordinateY").trim());
		map.put("detection1DL", req.getParameter("detection1dl".trim()));// T下限
		map.put("detection1UL", req.getParameter("detection1ul").trim());// T上限
		map.put("detection2DL", req.getParameter("detection2dl").trim());// PH下限
		map.put("detection2UL", req.getParameter("detection2ul".trim()));// PH上限
		map.put("detection3DL", req.getParameter("detection3dl").trim());// ORP下限
		map.put("detection3UL", req.getParameter("detection3ul").trim());// ORP上限
		map.put("detection5dl", req.getParameter("detection5dl").trim());// DO下限
		map.put("detection5ul", req.getParameter("detection5ul".trim()));// DO上限

		map.put("reduceNH3N", req.getParameter("reduceNH3N").trim());//
		map.put("reduceCOD", req.getParameter("reduceCOD").trim());
		map.put("reduceP", req.getParameter("reduceP").trim());


		map.put("runtimeperiod1", req.getParameter("runtimeperiod1").trim());// 风机运行时间
		map.put("stoptimeperiod1", req.getParameter("stoptimeperiod1").trim());// 风机停止时间

		map.put("runtimeperiod2", req.getParameter("runtimeperiod2").trim());// 混合液回流泵运行时间
		map.put("stoptimeperiod2", req.getParameter("stoptimeperiod2").trim());// 混合液回流泵停止时间

		map.put("runtimeperiod3", req.getParameter("runtimeperiod3").trim());// 污泥回流泵运行时间
		map.put("stoptimeperiod3", req.getParameter("stoptimeperiod3").trim());// 污泥回流泵停止时间时间

		map.put("runtimeperiod4", req.getParameter("DCF11").trim());// 电磁阀1运行时间
		map.put("stoptimeperiod4", req.getParameter("DCF12").trim());// 电磁阀1停止时间

		map.put("runtimeperiod5", req.getParameter("DCF21").trim());// 电磁阀2运行时间
		map.put("stoptimeperiod5", req.getParameter("DCF22").trim());// 电磁阀2停止时间

		map.put("runtimeperiod6", req.getParameter("DCF31").trim());// 电磁阀3运行时间
		map.put("stoptimeperiod6", req.getParameter("DCF32").trim());// 电磁阀3停止时间
		map.put("runtimeperiod7", req.getParameter("DCF41").trim());// 电磁阀4运行时间
		map.put("stoptimeperiod7", req.getParameter("DCF42").trim());// 电磁阀4停止时间
		map.put("tonnage", req.getParameter("tonnage").trim());// 吨位
		map.put("videourl", req.getParameter("videourl").trim());// 视屏地址
		if (map.get("name") == null || "".equals(map.get("name").toString())
				|| map.get("short_title") == null
				|| "".equals(map.get("short_title").toString())
				|| countyName == null || "".equals(countyName)) {
			return "Unavailable key add";
		}

		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			String tempString = new String(req.getParameter("name").getBytes(
					"iso-8859-1"), "utf-8");
			map.put("name", tempString.trim());

			tempString = new String(req.getParameter("shortTitle").getBytes(
					"iso-8859-1"), "utf-8");
			map.put("short_title", tempString.trim());

			tempString = new String(req.getParameter("address").getBytes(
					"iso-8859-1"), "utf-8");
			map.put("address", tempString.trim());

		}
		
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if (key == null) {
				key = entry.getKey().toString();
				value = "'" + entry.getValue().toString() + "'";
				sql += key + " = " + value;
			} else {
				key = entry.getKey().toString();
				value = "'" + entry.getValue().toString() + "'";
				sql += "," + key + " = " + value;
			}
		}
		String sell=sql;
		sql += "where sewageID = '" + sewageID+"'";
		System.out.println("update sql-->" + sql);

		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			return "Update successfully";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed";
		} finally {
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}

	}

	private ArrayList<Sewage1> readArrayList(String stationName) {
		ArrayList<Sewage1> list = new ArrayList<Sewage1>();
		String sql = "SELECT X.coordinateX,X.detection5ul,X.coordinateY,X.detection1UL,"// 4
				+ "X.runtimeperiod6,X.runtimeperiod5,X.detection1DL,X.runtimeperiod2,"// 4
				+ "X.runtimeperiod1,X.runtimeperiod4,X.name,X.runtimeperiod3,X.detection2DL,"// 5
				+ "X.sewageID,X.reduceCOD,X.reduceP,Y.name,X.detection5dl,X.reduceNH3N,X.operationnum,"// 6
				+ "X.controlID,X.detection3DL,X.stoptimeperiod6,X.short_title,X.stoptimeperiod5,"// 5
				+ "X.detection3UL,X.stoptimeperiod4,X.stoptimeperiod3,X.address,X.stoptimeperiod2,"// 5
				+ "X.detection2UL,X.stoptimeperiod1,X.runtimeperiod7,X.stoptimeperiod7,X.tonnage,X.videourl FROM sewage X , area Y WHERE Y.id=X.areaID AND X.short_title='"+stationName+"'";// 2

		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					Sewage1 temp = new Sewage1();

					temp.setCoordinateX(resultSet.getFloat(1));
					temp.setDetection5ul(resultSet.getFloat(2));
					temp.setCoordinateY(resultSet.getFloat(3));
					temp.setDetection1ul(resultSet.getFloat(4));

					temp.setDCF31(resultSet.getInt(5));
					temp.setDCF21(resultSet.getInt(6));
					temp.setDetection1dl(resultSet.getFloat(7));
					temp.setRuntimeperiod2(resultSet.getInt(8));

					// gratingDays

					temp.setRuntimeperiod1(resultSet.getInt(9));
					temp.setDCF11(resultSet.getInt(10));
					temp.setName(resultSet.getString(11));
					temp.setRuntimeperiod3(resultSet.getInt(12));
					temp.setDetection2dl(resultSet.getFloat(13));

					temp.setStationID(resultSet.getInt(14));
					temp.setReduceCOD(resultSet.getFloat(15));
					temp.setReduceP(resultSet.getFloat(16));
					temp.setCountyName(resultSet.getString(17));
					temp.setDetection5dl(resultSet.getFloat(18));
					temp.setReduceNH3N(resultSet.getFloat(19));
					temp.setOpNum(resultSet.getString(20));

					temp.setControlId(resultSet.getInt(21));
					temp.setDetection2dl(resultSet.getFloat(22));
					temp.setDCF32(resultSet.getInt(23));
					temp.setShortTitle(resultSet.getString(24));
					temp.setDCF22(resultSet.getInt(25));

					temp.setDetection3ul(resultSet.getFloat(26));
					temp.setDCF12(resultSet.getInt(27));
					temp.setStoptimeperiod3(resultSet.getInt(28));
					temp.setAddress(resultSet.getString(29));
					temp.setStoptimeperiod2(resultSet.getInt(30));

					temp.setDetection2ul(resultSet.getFloat(31));
					temp.setStoptimeperiod1(resultSet.getInt(32));
					temp.setDCF41(resultSet.getInt(33));
					temp.setDCF42(resultSet.getInt(34));
					temp.setTonnage(resultSet.getFloat(35));
					temp.setVideourl(resultSet.getString(36));
					list.add(temp);
				}
			}
		} catch (SQLException e) {
			e.getStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		// System.out.println("station detail-->" + list.toString());
		return list;
	}

	private int querySewageId(String stationname) {

		String sql = "SELECT id FROM area WHERE name='" + stationname + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int id = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					id = resultSet.getInt(1);
					// return id;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return id;
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return id;
	}

	private boolean isExisted(String keyValue1/* ,String keyValue2 */) {
		String sql = "select count(*) from dbo.sewage where sewageID = '"
				+ keyValue1 + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					if (0 == resultSet.getInt(1))
						return false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return true;
	}

	private int isAreaExisted(String keyValue) {
		String sql = "select areaId from area where name = '" + keyValue + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					return resultSet.getInt(1);
				}
			} else {
				return -1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return -1;
	}

	private int isAdminExisted(String keyValue1, String keyValue2) {
		String sql = "select administratorId from dbo.administrator "
				+ "where areaId = '" + keyValue1 + "' and name = '" + keyValue2
				+ "' order by administratorId";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					return resultSet.getInt(1);
				}
			} else {
				return -1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return -1;
	}

}
