package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DBHelp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import beans.MapInfo;

public class MapServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonValue = new JSONObject();
		String County = null;
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			 County = new String(req.getParameter("county").getBytes("iso-8859-1"),"utf-8");
		}
		jsonArray = JSONArray.fromObject(getMapInfo(County));
		jsonValue.element("result", jsonArray);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}

	private int getAreaid(String county) {// 预警查询
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int AreaID = 0;
		String sql = "SELECT id FROM area WHERE name = '" + county + "'";
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					AreaID = resultSet.getInt(1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return AreaID;
	}

	private ArrayList<MapInfo> getMapInfo(String county) {// 预警查询
		ArrayList<MapInfo> list1 = new ArrayList<MapInfo>();
		ArrayList<MapInfo> list2 = new ArrayList<MapInfo>();// 断电断线
		ArrayList<MapInfo> list3 = new ArrayList<MapInfo>();// 设备异常
		ArrayList<MapInfo> list4 = new ArrayList<MapInfo>();// 水质异常
		String sql1 = null;
		String sql2 = null;
		String sql3 = null;
		String sql4 = null;
		int areaID = getAreaid(county);
		
		if (county.equals("all")) {
			 sql1 = "SELECT x.sewageID , x.short_title, y.name,x.coordinateX, x.coordinateY,4 AS isabnormal from sewage x , area y WHERE x.areaID=y.id ";
			// 断电断线
			 sql2 = "SELECT sewageID ,short_title,coordinateX,coordinateY ,3 AS isabnormal FROM sewage WHERE sewageID in(((SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data  where CONVERT(varchar(12),testingtime,120) = CONVERT(varchar(12),getdate(),120))) )";
			// 设备异常
			 sql3 = "SELECT  sewageID , short_title, coordinateX,coordinateY,2 AS isabnormal FROM sewage WHERE sewageID in (SELECT sewageID FROM run_data_abnormal WHERE CONVERT(varchar(12),testingtime,120) = CONVERT(varchar(12),getdate(),120)) ";
			// 水质异常
			 sql4 = "SELECT  sewageID,short_title,coordinateX,coordinateY,1 AS isabnormal FROM sewage  WHERE sewageID in (SELECT sewageID FROM detection_data_abnormal WHERE CONVERT(varchar(12),testingtime,120) = CONVERT(varchar(12),getdate(),120) )";
		} else {
			 sql1 = "SELECT x.sewageID , x.short_title, y.name,x.coordinateX, x.coordinateY,4 AS isabnormal from sewage x , area y WHERE x.areaID=y.id AND y.name='"
					+ county + "'";
			 sql2 = "SELECT sewageID ,short_title,coordinateX,coordinateY ,3 AS isabnormal FROM sewage WHERE sewageID in(((SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data  where CONVERT(varchar(12),testingtime,120) = CONVERT(varchar(12),getdate(),120))) ) AND areaID = "+areaID+"";
			 sql3 = "SELECT  sewageID , short_title, coordinateX,coordinateY,2 AS isabnormal FROM sewage WHERE sewageID in (SELECT sewageID FROM run_data_abnormal WHERE CONVERT(varchar(12),testingtime,120) = CONVERT(varchar(12),getdate(),120)) AND areaID = "+areaID+"";
			 sql4 = "SELECT  sewageID,short_title,coordinateX,coordinateY,1 AS isabnormal FROM sewage  WHERE sewageID in (SELECT sewageID FROM detection_data_abnormal WHERE CONVERT(varchar(12),testingtime,120) = CONVERT(varchar(12),getdate(),120) ) AND areaID = "+areaID+"";;
		}
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet1 = null;
		ResultSet resultSet2 = null;
		ResultSet resultSet3 = null;
		ResultSet resultSet4 = null;
		// ResultSet resultSet1 = null;
		try {
			statement = connection.createStatement();
			resultSet1 = statement.executeQuery(sql1);
			if (resultSet1 != null) {
				while (resultSet1.next()) {
					MapInfo temp = new MapInfo();
					temp.setSewageID(resultSet1.getInt(1));
					temp.setShort_title(resultSet1.getString(2));
					temp.setCounty(resultSet1.getString(3));
					temp.setCoordinateX(resultSet1.getDouble(4));
					temp.setCoordinateY(resultSet1.getDouble(5));
					temp.setIsabnormal(resultSet1.getInt(6));
					list1.add(temp);
				}
			}
			resultSet2 = statement.executeQuery(sql2);
			if (resultSet2 != null) {
				while (resultSet2.next()) {
					MapInfo temp = new MapInfo();
					temp.setSewageID(resultSet2.getInt(1));
					temp.setShort_title(resultSet2.getString(2));
					temp.setCoordinateX(resultSet2.getDouble(3));
					temp.setCoordinateY(resultSet2.getDouble(4));
					temp.setIsabnormal(resultSet2.getInt(5));
					list2.add(temp);
				}
			}
			resultSet3 = statement.executeQuery(sql3);// 设备异常
			if (resultSet3 != null) {
				while (resultSet3.next()) {
					MapInfo temp = new MapInfo();
					temp.setSewageID(resultSet3.getInt(1));
					temp.setShort_title(resultSet3.getString(2));
					temp.setCoordinateX(resultSet3.getDouble(3));
					temp.setCoordinateY(resultSet3.getDouble(4));
					temp.setIsabnormal(resultSet3.getInt(5));
					list3.add(temp);
				}
			}
			resultSet4 = statement.executeQuery(sql4);// 水质异常
			if (resultSet4 != null) {
				while (resultSet4.next()) {
					MapInfo temp = new MapInfo();
					temp.setSewageID(resultSet4.getInt(1));
					temp.setShort_title(resultSet4.getString(2));
					temp.setCoordinateX(resultSet4.getDouble(3));
					temp.setCoordinateY(resultSet4.getDouble(4));
					temp.setIsabnormal(resultSet4.getInt(5));
					list4.add(temp);
				}
			}

			for (int i = 0; i < list1.size(); i++) {
				for (int j = 0; j < list2.size(); j++) { // 断电断线
					if ((list1.get(i).getShort_title()).equals((list2.get(j)
							.getShort_title()))) {
						list1.get(i).setIsabnormal(
								(list2.get(j).getIsabnormal()));
					}

				}

				for (int j = 0; j < list4.size(); j++) { // 水质异常
					if ((list1.get(i).getShort_title()).equals((list4.get(j)
							.getShort_title()))) {
						list1.get(i).setIsabnormal(
								(list4.get(j).getIsabnormal()));
					}

				}
				for (int j = 0; j < list3.size(); j++) { // 设备故障
					if ((list1.get(i).getShort_title()).equals((list3.get(j)
							.getShort_title()))) {
						list1.get(i).setIsabnormal(
								(list3.get(j).getIsabnormal()));
					}
				}
			}

			return list1;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet1);
			DBHelp.closeResultSet(resultSet2);
			DBHelp.closeResultSet(resultSet3);
			DBHelp.closeResultSet(resultSet4);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list1;
	}

}
