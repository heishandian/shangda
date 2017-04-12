package servlet;

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
import util.DBHelp;
import beans.AlertMessage;

public class AlertQueryServlet extends HttpServlet {
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
		String flag = req.getParameter("requestFlag");
		String start_time = req.getParameter("start_time");// 传入查询开始时间
		String end_time = req.getParameter("end_time");// 传入查询的结束时间
		String county = req.getParameter("county");
		String short_title = req.getParameter("short_title");
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			county = new String(county.getBytes("iso-8859-1"), "utf-8");
			short_title = new String(short_title.getBytes("iso-8859-1"),
					"utf-8");
		}
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = new JSONArray();
		JSONArray jsonArray2 = new JSONArray();
		if ("RW".equalsIgnoreCase(flag)) {// 获取水质参数报警信息 注意报警查询现在是按区县+站点+时间段查询
			jsonArray1 = JSONArray.fromObject(DtectionDataAbnormal(pagingnum1,
					items1, county, short_title, start_time, end_time));
			jsonArray2 = JSONArray.fromObject(getDtectionDataAbnormalCount(
					pagingnum1, items1, county, short_title, start_time,
					end_time));
			temp = 0;
		}
		if ("RE".equalsIgnoreCase(flag))// 获取设备状况报警信息 按区域查询
		{
			jsonArray1 = JSONArray.fromObject(RundataAbnormal(pagingnum1,
					items1, county, short_title, start_time, end_time));
			jsonArray2 = JSONArray.fromObject(getRundataAbnormalCount(
					pagingnum1, items1, county, short_title, start_time,
					end_time));

		}
		if ("RP".equalsIgnoreCase(flag)) {// 获取断电断线报警信息
			jsonArray1 = JSONArray.fromObject(PowerOff(pagingnum1, items1,
					county, short_title, start_time, end_time));
			jsonArray2 = JSONArray.fromObject(getPowerOffCount(pagingnum1,
					items1, county, short_title, start_time, end_time));
		}

		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result1", jsonArray1);
		jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		temp = 0;
		printWriter.flush();
		printWriter.close();
	}

	private ArrayList<AlertMessage> DtectionDataAbnormal(int pagingnum,
			int items, String area, String short_title, String start_time,
			String end_time) {
		if (pagingnum < 0) {
			pagingnum = 1;
		}
		int areaid = getAreaID(area);// 用区域名获取区域ID
		ArrayList<AlertMessage> list = new ArrayList<AlertMessage>();
		String sql = null;
		// 如果直接点查询,将会查询所有区县的水质异常记录
		if ("".equals(area) && "".equals(short_title) && "".equals(start_time)
				&& "".equals(end_time)) {
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.detectionno,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120) FROM detection_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.detectionID NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") detectionID FROM detection_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage   ) )AND x.sewageid IN (SELECT sewageID FROM sewage )  ORDER BY X.testingtime  desc";
		}
		// 如果只指定了区县，站点和时间段都没有指定，那么按区县查询
		else if (!"".equals(area) && "".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 注意testingtime为检测时间，lasttestingtime为最后一条故障时间，不知道是啥鸟意思
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.detectionno,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120) FROM detection_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.detectionID NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") detectionID FROM detection_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid
					+ "' )  ) AND x.sewageid IN (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid + "' )  ORDER BY X.testingtime  desc";
		} else if (!"".equals(area) && "".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 查询指定区域，指定时间段的信息
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.detectionno,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120)  FROM detection_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.detectionID NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") detectionID FROM detection_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid
					+ "' ) and testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='"
					+ end_time
					+ "' ) AND x.sewageid IN (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid + "' ) and testingtime>='" + start_time
					+ "' and lasttestingtime<='" + end_time
					+ "' ORDER BY X.testingtime  desc";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 区域非空，站点非空，开始时间和结束时间是空的，查询指定站点
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.detectionno,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120)  FROM detection_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.detectionID NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") detectionID FROM detection_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage  WHERE short_title='"
					+ short_title
					+ "')  ) AND x.sewageid IN (SELECT sewageID FROM sewage WHERE short_title='"
					+ short_title + "' )  ORDER BY X.testingtime  desc";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& !"".equals("+start_time+") && !"".equals("+start_time+"))// 区域非空，站点非空，开始时间和结束时间非空的空，即查询指定区县，指定站点，指定时间段的记录
		{
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.detectionno,x.testingtime,x.lasttestingtime FROM detection_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.detectionID NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") detectionID FROM detection_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage  WHERE short_title='"
					+ short_title
					+ "' ) and testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='"
					+ end_time
					+ "' ) AND  x.testingtime>='"
					+ start_time
					+ "' and x.lasttestingtime<='"
					+ end_time
					+ "' AND x.sewageid IN (SELECT sewageID FROM sewage WHERE short_title='"
					+ short_title + "' )  ORDER BY X.testingtime  desc";
		} else {// 返回错误
			System.out.println("输入的查询条件有错误");
		}
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					temp++;
					AlertMessage temp = new AlertMessage();
					temp.setShort_title(resultSet.getString(1));// 污水站名称
					temp.setOperationnum(resultSet.getString(2));// 站点运营编码
					temp.setAlertInfo(resultSet.getString(3));//
					temp.setTestingtime(resultSet.getString(4));
					temp.setLasttestingtime(resultSet.getString(5));
					list.add(temp);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private ArrayList<AlertMessage> RundataAbnormal(int pagingnum, int items,
			String area, String short_title, String start_time, String end_time) {
		if (pagingnum < 0) {
			pagingnum = 1;
		}
		int sewageid = 0;// getSewageID(stationName);
		int areaid = getAreaID(area);// 用区域名获取区域ID
		ArrayList<AlertMessage> list = new ArrayList<AlertMessage>();
		// 查询指定区域的设备故障情况
		String sql_equipment = null;
		// 查询所有区域的设备故障
		if ("".equals(area) && "".equals(short_title) && "".equals(start_time)
				&& "".equals(end_time)) {
			sql_equipment = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.equipmentno,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120) FROM run_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.runid NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") runid FROM run_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage )  ) AND x.sewageid IN (SELECT sewageID FROM sewage)  ORDER BY X.testingtime  desc";
		} else if (!"".equals(area) && "".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 注意testingtime为检测时间，lasttestingtime为最后一条故障时间，不知道是啥鸟意思
			sql_equipment = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.equipmentno,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120) FROM run_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.runid NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") runid FROM run_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid
					+ "' )  ) AND x.sewageid IN (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid + "' )  ORDER BY X.testingtime  desc";
		} else if (!"".equals(area) && "".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 查询指定区域，指定时间段的信息
			sql_equipment = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.equipmentno,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120)  FROM run_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.runid NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") runid FROM run_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid
					+ "' ) and testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='"
					+ end_time
					+ "' ) AND x.sewageid IN (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid + "' ) and testingtime>='" + start_time
					+ "' and lasttestingtime<='" + end_time
					+ "' ORDER BY X.testingtime  desc";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 区域非空，站点非空，开始时间和结束时间是空的，查询指定站点
			sql_equipment = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.equipmentno,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120)  FROM run_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.runid NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") runid FROM run_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage  WHERE short_title='"
					+ short_title
					+ "')  ) AND x.sewageid IN (SELECT sewageID FROM sewage WHERE short_title='"
					+ short_title + "' )  ORDER BY X.testingtime  desc";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& !"".equals("+start_time+") && !"".equals("+start_time+"))// 区域非空，站点非空，开始时间和结束时间非空的空，即查询指定区县，指定站点，指定时间段的记录
		{
			sql_equipment = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,x.equipmentno,x.testingtime,x.lasttestingtime FROM run_data_abnormal x,sewage y WHERE x.sewageID=y.sewageID AND x.runID NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") runid FROM run_data_abnormal where sewageid"
					+ " IN (SELECT sewageID FROM sewage  WHERE short_title='"
					+ short_title
					+ "' ) and testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='"
					+ end_time
					+ "' ) AND  x.testingtime>='"
					+ start_time
					+ "' and x.lasttestingtime<='"
					+ end_time
					+ "' AND x.sewageid IN (SELECT sewageID FROM sewage WHERE short_title='"
					+ short_title + "' )  ORDER BY X.testingtime  desc";
		} else {// 返回错误
			System.out.println("输入的查询条件有错误");
		}
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql_equipment);
			if (resultSet != null) {
				while (resultSet.next()) {
					temp++;
					AlertMessage temp = new AlertMessage();
					temp.setShort_title(resultSet.getString(1));// 污水站名称
					temp.setOperationnum(resultSet.getString(2));// 站点运营编码
					temp.setAlertInfo(resultSet.getString(3));//
					temp.setTestingtime(resultSet.getString(4));
					temp.setLasttestingtime(resultSet.getString(5));
					list.add(temp);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private ArrayList<AlertMessage> PowerOff(int pagingnum, int items,
			String area, String short_title, String start_time, String end_time) {//
		int areaID = getAreaid(area);
		if (pagingnum < 0) {
			pagingnum = 1;
		}
		int sewageid = 0;//
		int areaid = getAreaID(area);// 用区域名获取区域ID
		ArrayList<AlertMessage> list = new ArrayList<AlertMessage>();
		// String sql =
		// "SELECT TOP "+items+"  A.short_title,A.operationnum FROM (SELECT sewageID,short_title,operationnum FROM sewage WHERE areaID="+areaID+" AND sewageID IN(SELECT DISTINCT sewageID FROM sewage EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) < CONVERT(varchar(10),'"+time+"',120))))AS A WHERE sewageID not in (SELECT TOP (("+
		// pagingnum+"-1)*"+ items+
		// ") sewageID  FROM (SELECT  sewageID,short_title FROM sewage WHERE areaID="+areaID+" AND sewageID IN((SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120)) )) AS B )";
		String sql = null;
		if ("".equals(area) && "".equals(short_title) && "".equals(start_time)
				&& "".equals(end_time)) {
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,110 as withoutelectricity,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120)FROM withoutElectric_data_abnormal x,sewage y WHERE x.sewageid in (SELECT sewageID FROM sewage  ) and x.sewageid = y.sewageID  order by x.testingtime desc";
		}
		// 按指定区域查询
		else if (!"".equals(area) && "".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,110 as withoutelectricity,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120)FROM withoutElectric_data_abnormal x,sewage y WHERE x.sewageid in (SELECT sewage.sewageID FROM sewage  WHERE sewage.areaID="
					+ areaid
					+ " ) and x.sewageid = y.sewageID  order by x.testingtime desc";
		} else if (!"".equals(area) && "".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 查询指定时间段的断电断线
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,110 as withoutelectricity,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120) FROM withoutElectric_data_abnormal x,sewage y WHERE x.sewageid in (SELECT sewage.sewageID FROM sewage  WHERE sewage.areaID="
					+ areaid
					+ " ) and x.sewageid = y.sewageID AND testingtime>='"
					+ start_time + "' and lasttestingtime<='" + end_time
					+ "'  order by x.testingtime desc";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 查询指定站点的断电断线
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,110 as withoutelectricity,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120) FROM withoutElectric_data_abnormal x,sewage y WHERE x.sewageid in (SELECT sewageID FROM sewage  WHERE sewage.short_title="
					+ short_title
					+ " ) and x.sewageid = y.sewageID order by x.testingtime desc ";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 查询指定站点指定时间段的断电断线
			sql = "SELECT TOP "
					+ items
					+ " y.short_title,y.operationnum ,110 as withoutelectricity,CONVERT(varchar(19),x.testingtime,120),CONVERT(varchar(19),x.lasttestingtime,120) FROM withoutElectric_data_abnormal x,sewage y WHERE x.sewageid in (SELECT sewageID FROM sewage  WHERE sewage.short_title="
					+ short_title
					+ " ) and x.sewageid = y.sewageID AND testingtime>='"
					+ start_time + "' and lasttestingtime<='" + end_time
					+ "' order by x.testingtime desc";
		}
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					temp++;
					AlertMessage temp = new AlertMessage();
					temp.setShort_title(resultSet.getString(1));// 污水站名称
					temp.setOperationnum(resultSet.getString(2));// 站点运营编码
					temp.setAlertInfo(resultSet.getString(3));//
					temp.setTestingtime(resultSet.getString(4));
					temp.setLasttestingtime(resultSet.getString(5));
					list.add(temp);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private int getAreaID(String area) {
		String sql = "SELECT id FROM area WHERE name='" + area + "'";
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
		String sql = "SELECT id FROM area WHERE name = '" + area + "'";
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

	private PadingUtil getPowerOffCount(int pagingnum, int items, String area,
			String short_title, String start_time, String end_time) {// 返回断电断线信息总条数
		int areaid = getAreaID(area);// 用区域名获取区域ID
		String sql = null;
		if ("".equals(area) && "".equals(short_title) && "".equals(start_time)
				&& "".equals(end_time)) {// 查询指定区域的断电断线信息总条数
			sql = "SELECT COUNT(*) itemscount FROM withoutElectric_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage)";
		} else if (!"".equals(area) && "".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 查询指定区域的断电断线信息总条数
			sql = "SELECT COUNT(*) itemscount FROM withoutElectric_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid + "' )";
		} else if (!"".equals(area) && "".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 指定区域，指定时间段
			sql = "SELECT COUNT(*) itemscount FROM withoutElectric_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid
					+ "' ) AND testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='" + end_time + "'";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 查询指定站点的水质异常信息总条数
			sql = "SELECT COUNT(*) itemscount FROM withoutElectric_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage WHERE short_title = '"
					+ short_title + "' ) ";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 查询指定站点，指定时间段
			sql = "SELECT COUNT(*) itemscount FROM withoutElectric_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage WHERE short_title =' "
					+ short_title
					+ "' ) AND testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='" + end_time + "'";
		} else {
			System.out.println("输入条件有误,请检查");
		}
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {

					int a = resultSet.getInt(1);
					padingUtil.setItemscount(a);// 总条数
					padingUtil.setItems(temp);// 当前条数
					padingUtil.setPagingnum(pagingnum);// 当前页页码
					if ((a % items) == 0) {
						padingUtil.setPagecount((int) (Math.ceil(a / items)));
					} else {
						padingUtil
								.setPagecount((int) (Math.ceil(a / items) + 1));
					}
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

	private PadingUtil getRundataAbnormalCount(int pagingnum, int items,
			String area, String short_title, String start_time, String end_time) {
		int areaid = getAreaID(area);// 用区域名获取区域ID
		String sql = null;
		if ("".equals(area) && "".equals(short_title) && "".equals(start_time)
				&& "".equals(end_time)) {
			sql = "SELECT COUNT(*) itemscount FROM run_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage)";
		}
		if (!"".equals(area) && "".equals(short_title) && "".equals(start_time)
				&& "".equals(end_time)) {// 查询指定区域的水质异常报警信息总条数
			sql = "SELECT COUNT(*) itemscount FROM run_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid + "' )";
		} else if (!"".equals(area) && "".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 指定区域，指定时间段
			sql = "SELECT COUNT(*) itemscount FROM run_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid
					+ "' ) AND testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='" + end_time + "'";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 查询指定站点的水质异常信息总条数
			sql = "SELECT COUNT(*) itemscount FROM run_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage WHERE short_title = '"
					+ short_title + "' ) ";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 查询指定站点，指定时间段
			sql = "SELECT COUNT(*) itemscount FROM run_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage WHERE short_title = '"
					+ short_title
					+ "' ) AND testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='" + end_time + "'";
		} else {
			System.out.println("输入条件有误,请检查");
		}
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					int a = resultSet.getInt(1);
					padingUtil.setItemscount(a);// 总条数
					padingUtil.setItems(temp);// 当前条数
					padingUtil.setPagingnum(pagingnum);// 当前页页码
					if ((a % items) == 0) {
						padingUtil.setPagecount((int) (Math.ceil(a / items)));
					} else {
						padingUtil
								.setPagecount((int) (Math.ceil(a / items) + 1));
					}
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

	// int pagingnum, int items, String area, String short_title,String
	// start_time,String end_time
	private PadingUtil getDtectionDataAbnormalCount(int pagingnum, int items,
			String area, String short_title, String start_time, String end_time) {
		int areaid = getAreaID(area);// 用区域名获取区域ID
		String sql = null;
		if ("".equals(area) && "".equals(short_title) && "".equals(start_time)
				&& "".equals(end_time)) {
			sql = "SELECT COUNT(*) itemscount FROM detection_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage )";
		} else if (!"".equals(area) && "".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 查询指定区域的水质异常报警信息总条数
			sql = "SELECT COUNT(*) itemscount FROM detection_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid + "' )";
		} else if (!"".equals(area) && "".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 指定区域，指定时间段
			sql = "SELECT COUNT(*) itemscount FROM detection_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage,area WHERE sewage.areaID = area.id and area.id = '"
					+ areaid
					+ "' ) AND testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='" + end_time + "'";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& "".equals(start_time) && "".equals(end_time)) {// 查询指定站点的水质异常信息总条数
			sql = "SELECT COUNT(*) itemscount FROM detection_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage WHERE short_title = '"
					+ short_title + "' ) ";
		} else if (!"".equals(area) && !"".equals(short_title)
				&& !"".equals(start_time) && !"".equals(end_time)) {// 查询指定站点，指定时间段
			sql = "SELECT COUNT(*) itemscount FROM detection_data_abnormal WHERE  sewageID in (SELECT sewageID FROM sewage WHERE short_title = '"
					+ short_title
					+ "' ) AND testingtime>='"
					+ start_time
					+ "' and lasttestingtime<='" + end_time + "'";
		} else {
			System.out.println("输入条件有误,请检查");
		}
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					int a = resultSet.getInt(1);
					padingUtil.setItemscount(a);// 总条数
					padingUtil.setItems(temp);// 当前条数
					padingUtil.setPagingnum(pagingnum);// 当前页页码
					if ((a % items) == 0) {
						padingUtil.setPagecount((int) (Math.ceil(a / items)));
					} else {
						padingUtil
								.setPagecount((int) (Math.ceil(a / items) + 1));
					}

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
