package com.czq.servlet;

//(SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'2016-04-15 08:00:00.000',120))

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.czq.entitiy.Alert;
import com.czq.entitiy.PadingUtil;
import com.czq.util.DBHelp;

public class AlertQueryServlet extends HttpServlet  {
	int temp = 0;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pagingnum = req.getParameter("pagingnum");
		String items = req.getParameter("items");
		String time = req.getParameter("time");// 传入查询开始时间
		//String end_time = req.getParameter("end_time");// 传入查询的结束时间
		String flag = req.getParameter("requestFlag");
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = new JSONArray();
		JSONArray jsonArray2 = new JSONArray();
		if ("RW".equalsIgnoreCase(flag)) {// 获取水质参数报警信息
			String station = req.getParameter("stationName");
			if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
				station = new String(station.getBytes("iso-8859-1"), "utf-8");
			}
			jsonArray1 = JSONArray.fromObject(DtectionDataAbnormal(pagingnum1,items1, station, time));
			jsonArray2 = JSONArray.fromObject(getDtectionDataAbnormalCount(station,pagingnum1,items1, time));
			 temp = 0;
		}
		if ("RE".equalsIgnoreCase(flag))// 获取设备状况报警信息
		{	String station = req.getParameter("stationName");
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			station = new String(station.getBytes("iso-8859-1"), "utf-8");
		}
			
			jsonArray1 = JSONArray.fromObject(RundataAbnormal(pagingnum1,items1, station, time));
			jsonArray2 = JSONArray.fromObject(getRundataAbnormalCount(station,pagingnum1,items1, time));
			
		}
		if ("RP".equalsIgnoreCase(flag)) {// 获取断电断线报警信息
			String area = req.getParameter("area");
			if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
				area = new String(area.getBytes("iso-8859-1"), "utf-8");
			}
			jsonArray1 = JSONArray.fromObject(PowerOff(area,pagingnum1,items1, time));
			jsonArray2 = JSONArray.fromObject(getPowerOffCount(area,pagingnum1,items1, time));
			
		}
		
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result1", jsonArray1);
		jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		 temp = 0;
		printWriter.flush();
		printWriter.close();
	}

	private ArrayList<Alert> DtectionDataAbnormal (int pagingnum, int items, String stationName, String time) {
		if (pagingnum < 0) {
			pagingnum = 1;
		}
		int sewageid = getSewageID(stationName);
		String admin = getAdmin(stationName);
		ArrayList<Alert> list = new ArrayList<Alert>();
		String sql = "SELECT TOP "
				+ items
				+ " CONVERT(varchar(19),testingtime,120),'"
				+ stationName
				+ "' AS short_title, detectionno,isrepaired,'"
				+ admin
				+ "' as name  FROM detection_data_abnormal WHERE detectionID NOT IN (select top (("
				+ pagingnum
				+ "-1)*"
				+ items
				+ ") detectionID FROM detection_data_abnormal where sewageid="
				+ sewageid
				+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
				+ time
				+ "',120) ORDER BY testingtime desc) AND sewageid="
				+ sewageid
				+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
				+ time + "',120) ORDER BY testingtime desc";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if (resultSet != null) {
					while (resultSet.next()) {
						temp++;
						Alert temp = new Alert();
						temp.setAlertTime(resultSet.getString(1));
						temp.setSewage_name(resultSet.getString(2));
						temp.setAlertInfo(resultSet.getInt(3));
						temp.setState(resultSet.getInt(4));
						temp.setAdmin(resultSet.getString(5));
						list.add(temp);
					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
         
     return list;  
	}
	
	private ArrayList<Alert> RundataAbnormal (int pagingnum, int items, String stationName, String time) {
		if (pagingnum < 0) {
			pagingnum = 1;
		}
		int sewageid = getSewageID(stationName);
		String admin = getAdmin(stationName);
		ArrayList<Alert> list = new ArrayList<Alert>();
		String sql = "SELECT TOP "
					+ items
					+ " CONVERT(varchar(19),testingtime,120),'"
					+ stationName
					+ "' AS short_title, equipmentno,isrepaired, '"
					+ admin
					+ "' as name  FROM run_data_abnormal WHERE runid NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") runid FROM run_data_abnormal where sewageid="
					+ sewageid
					+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
					+ time
					+ "',120) ORDER BY testingtime desc) AND sewageid="
					+ sewageid
					+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
					+ time + "',120) ORDER BY testingtime desc";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if (resultSet != null) {
					while (resultSet.next()) {
						temp++;
						Alert temp = new Alert();
						temp.setAlertTime(resultSet.getString(1));
						temp.setSewage_name(resultSet.getString(2));
						temp.setAlertInfo(resultSet.getInt(3));
						temp.setState(resultSet.getInt(4));
						temp.setAdmin(resultSet.getString(5));
						list.add(temp);
					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
         
     return list;  
	}


	
	private ArrayList<Alert> PowerOff (String area,int pagingnum, int items, String time) {
		int areaID = getAreaid(area);
		if (pagingnum < 0) {
			pagingnum = 1;
		}
		ArrayList<Alert> list = new ArrayList<Alert>();
		String sql = "SELECT TOP "+items+"  A.short_title FROM (SELECT sewageID,short_title FROM sewage WHERE areaID="+areaID+" AND sewageID IN(SELECT DISTINCT sewageID FROM sewage EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120))))AS A WHERE sewageID not in (SELECT TOP (("+ pagingnum+"-1)*"+ items+ ") sewageID  FROM (SELECT  sewageID,short_title FROM sewage WHERE areaID="+areaID+" AND sewageID IN((SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120)) )) AS B )";
		
		
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if (resultSet != null) {
					while (resultSet.next()) {
						temp++;
						Alert temp = new Alert();
						temp.setAlertTime(time);
						temp.setSewage_name(resultSet.getString(1));
						temp.setAlertInfo(110);
						temp.setAdmin(getAdmin(resultSet.getString(1)));
						list.add(temp);
					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
         
     return list;  
	}
	
	

	
       
	private int getSewageID(String name) {
		String sql = "SELECT sewageID FROM sewage WHERE short_title='" + name
				+ "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int sewage = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					sewage = resultSet.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return sewage;
	}

	private String getAbnormalID(String time) {
		String sql = "((SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
				+ time + "',120))) as A ORDER BY A.sewageID ASC";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		String admin = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					admin = resultSet.getString(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return admin;
	}

	private String getAdmin(String name) {
		String sql = "SELECT name from admin WHERE id IN (SELECT adminid FROM admin_area WHERE areaid IN(SELECT areaID FROM sewage WHERE short_title='"
				+ name + "') ) ";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		String admin = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					admin = resultSet.getString(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return admin;
	}

	private int getAreaid(String area) {
		String sql = "SELECT id FROM area WHERE name = '"+area+"'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int areaid = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					areaid = resultSet.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return areaid;
	}
	
	
	
	private PadingUtil getPowerOffCount( String area ,Integer pagingnum,
			Integer items, String time) {
		int areaID = getAreaid(area);
		String sql = "SELECT COUNT(*) FROM sewage WHERE areaID="+areaID+" AND sewageID IN((SELECT DISTINCT sewageID FROM sewage ) " +
				"EXCEPT (SELECT DISTINCT sewageID  FROM run_data  WHERE   CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120) )) ";
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					padingUtil.setItemscount(resultSet.getInt(1));
					padingUtil.setItems(temp);
					padingUtil.setPagingnum(pagingnum);
					padingUtil.setPagecount( (int) (Math.ceil(((double)(resultSet.getInt(1))/items))));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return padingUtil;

	}
	
	private PadingUtil getRundataAbnormalCount(String stationName, Integer pagingnum,
			Integer items, String time) {
		String sql = "SELECT COUNT(*) itemscount FROM run_data_abnormal WHERE CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
				+ time
				+ "',120) AND sewageID = (SELECT sewageID FROM sewage WHERE short_title='"
				+ stationName + "')";
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					padingUtil.setItemscount(resultSet.getInt(1));
					padingUtil.setItems(temp);
					padingUtil.setPagingnum(pagingnum);
					padingUtil.setPagecount( (int) (Math.ceil(((double)(resultSet.getInt(1))/items))));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return padingUtil;

	}
	
	private PadingUtil getDtectionDataAbnormalCount(String stationName, Integer pagingnum,
			Integer items, String time) {
		String sql = "SELECT COUNT(*) itemscount FROM detection_data_abnormal WHERE CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
				+ time
				+ "',120) AND sewageID = (SELECT sewageID FROM sewage WHERE short_title='"
				+ stationName + "')";
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					padingUtil.setItemscount(resultSet.getInt(1));
					padingUtil.setItems(temp);
					padingUtil.setPagingnum(pagingnum);
					padingUtil.setPagecount( (int) (Math.ceil(((double)(resultSet.getInt(1))/items))));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return padingUtil;

	}
	
	

}
