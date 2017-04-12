package com.czq.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.czq.entitiy.Map1;
import com.czq.entitiy.Map2;
import com.czq.entitiy.Map3;
import com.czq.util.DBHelp;

/**
 * @author apple get information about detection data of station pointed
 */
public class MapmsgServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("stationName");
		// sometimes the CharacterSet is not "UTF-8"
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			station = new String(station.getBytes("iso-8859-1"), "utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = JSONArray.fromObject(getDetectionList(station));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray1);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}

	private List<Map1> getDetectionList(String station) {
		String sewageID = null;
		ArrayList<Map1> list1 = new ArrayList<Map1>();
	    Map1 det = new Map1();
	    String sql0="SELECT sewageID,short_title,operationnum FROM sewage WHERE short_title='"+station+"' ";
	    String sql1 = "SELECT TOP 1 " +
				"equipment6state,equipment7state,equipment8state,equipment9state,equipment10state," +
				"equipment11state,equipment12state,equipment13state,equipment14state,equipment15state," +
				"equipment16state,equipment17state,equipment18state,equipment19state,equipment20state," +
				"equipment21state,equipment22state FROM run_data  ";		
		String sql2 = "select TOP 1   cast(detection1 as decimal(18,2)),cast(detection2 as decimal(18,2)),cast(detection3 as decimal(18,2)),cast(detection5 as decimal(18,2)),cast(detection6 as decimal(18,2)) from dbo.detection_data ";
		String sql3="select TOP 1 water from statistic_day where CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),GETDATE(),120)";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql0);
			while (resultSet.next()) {
				sewageID=resultSet.getString(1);
				det.setShort_title(resultSet.getString(2));
				det.setOperationnum(resultSet.getString(3));
				}
			sql1+="WHERE sewageID="+sewageID+" order by testingtime desc";
			resultSet = statement.executeQuery(sql1);
			while (resultSet.next()) {
				det.setEquipment6state(resultSet.getString(1));
				det.setEquipment7state(resultSet.getString(2));	
				det.setEquipment8state(resultSet.getString(3));
				det.setEquipment9state(resultSet.getString(4));
				det.setEquipment10state(resultSet.getString(5));
				det.setEquipment11state(resultSet.getString(6));
				det.setEquipment12state(resultSet.getString(7));
				det.setEquipment13state(resultSet.getString(8));
				det.setEquipment14state(resultSet.getString(9));
				det.setEquipment15state(resultSet.getString(10));
				det.setEquipment16state(resultSet.getString(11));
				det.setEquipment17state(resultSet.getString(12));
				det.setEquipment18state(resultSet.getString(13));
				det.setEquipment19state(resultSet.getString(14));
				det.setEquipment20state(resultSet.getString(15));
				det.setEquipment21state(resultSet.getString(16));
				det.setEquipment22state(resultSet.getString(17));
				}
			sql2+="WHERE sewageID="+sewageID+" order by testingtime desc";
			resultSet = statement.executeQuery(sql2);
			while (resultSet.next()) {
				det.setDetection1(resultSet.getFloat(1));
				det.setDetection2(resultSet.getFloat(2));
				det.setDetection3(resultSet.getFloat(3));
				det.setDetection5(resultSet.getFloat(4));	
				det.setInstant_flow(resultSet.getFloat(5));	
				}
			sql3+=" AND sewageID="+sewageID+"";
			resultSet = statement.executeQuery(sql3);
			while (resultSet.next()) {
				det.setDay_water(resultSet.getFloat(1));
		         }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			list1.add(det);
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list1;
	}

}