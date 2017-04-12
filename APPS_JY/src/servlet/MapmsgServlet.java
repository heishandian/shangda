package servlet;

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

import util.DBHelp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import beans.Map1;


public class MapmsgServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("stationName");
		String operationum = req.getParameter("operationnum");
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			station = new String(station.getBytes("iso-8859-1"), "utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = JSONArray.fromObject(getDetectionList(station,operationum));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray1);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}

	private List<Map1> getDetectionList(String station,String operationnum) {
		String sewageID = null;
		float reduceCOD=0;
		float reduceNH3N=0;
		float reduceP = 0;
		float day_water=0;
		ArrayList<Map1> list1 = new ArrayList<Map1>();
	    Map1 det = new Map1();
	    String sql0 = null;
	    String sql1 = null;
	    String sql2 = null;
	    String sql3 = null;
	    if(operationnum==null ){//按照运营编号查询
	    	 sql0="SELECT sewageID,short_title,operationnum,reduceCOD,reduceNH3N,reduceP FROM sewage WHERE short_title='"+station+"'";
	    }else {
	    	 sql0="SELECT sewageID,short_title,operationnum,reduceCOD,reduceNH3N,reduceP FROM sewage WHERE operationnum='"+operationnum+"'";
	    }
	     sql1 = "SELECT TOP 1 equipment1state,equipment2state,equipment3state,equipment4state FROM run_data ";		
		 sql2 = "select TOP 1   cast(detection1 as decimal(18,2)), cast(detection2 as decimal(18,2)), cast(detection3 as decimal(18,2)) ,  cast(detection4 as decimal(18,2)), cast(detection5 as decimal(18,2)) , cast(detection6 as decimal(18,2)) from dbo.detection_data ";
		 sql3="select TOP 1 cast(water as decimal(18,2)) from statistic_day where CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),GETDATE(),120) ";
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
				reduceCOD=resultSet.getFloat(4);
				reduceNH3N=resultSet.getFloat(5);
				reduceP=resultSet.getFloat(6);
			}
			sql1+=" WHERE sewageID="+sewageID+" order by testingtime desc";
			resultSet = statement.executeQuery(sql1);
			while (resultSet.next()) {
				det.setEquipment1state(resultSet.getString(1));
				det.setEquipment2state(resultSet.getString(2));	
				det.setEquipment3state(resultSet.getString(3));
				det.setEquipment4state(resultSet.getString(4));
				}
			sql2+="WHERE sewageID="+sewageID+" order by testingtime desc";
			resultSet = statement.executeQuery(sql2);
			while (resultSet.next()) {
				det.setDetection1(resultSet.getFloat(1));
				det.setDetection2(resultSet.getFloat(2));
				det.setDetection3(resultSet.getFloat(3));
				det.setDetection4(resultSet.getString(4));
				det.setDetection5(resultSet.getFloat(5));	
				det.setInstant_flow(resultSet.getFloat(6));	
				}
			sql3+=" AND sewageID="+sewageID+"";
			resultSet = statement.executeQuery(sql3);
			while (resultSet.next()) {
				day_water=resultSet.getFloat(1);
				det.setDay_water(resultSet.getFloat(1));//(float)(Math.round((day_water*reduceP)*100))/100
				det.setReduceCOD((float)(Math.round((day_water*reduceCOD)*100))/100);
				det.setReduceNH3N((float)(Math.round((day_water*reduceNH3N)*100))/100);
				det.setReduceP((float)(Math.round((day_water*reduceP)*100))/100);
		         }			
		} catch (SQLException e) {
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