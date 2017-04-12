package servlet;
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
import beans.WaterFlow_day;
import beans.WaterFlow_month;
import beans.WaterFlow_year;

public class WaterFlowChartServlet extends HttpServlet{

	int Tonnage = 0;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("stationName");
		String flag = req.getParameter("Flag");
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			station = new String(station.getBytes("iso-8859-1"),"utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter=resp.getWriter();
		JSONArray jsonArray=new JSONArray();
		
		if("Day".equalsIgnoreCase(flag)){
			String year = req.getParameter("Year");
			String month = req.getParameter("Month");
			if(month.length()==1){
				month="0".concat(month);//concat方法，将两个字符串拼接在一起
			}
			String date = req.getParameter("Date");
			if(date.length()==1){
				date="0".concat(date);
			}
			String Date=year+"-"+month+"-"+date;
			jsonArray = JSONArray.fromObject(getDateWaterFlowInfo(station,Date));
		}
		if("Month".equalsIgnoreCase(flag)){
			String year = req.getParameter("Year");
			String month = req.getParameter("Month");
			if(month.length()==1){
				month="0".concat(month);
			}
			String Date=year+"-"+month;
			jsonArray = JSONArray.fromObject(getMonthWaterFlowInfo(station,Date));
		}
		if("Year".equalsIgnoreCase(flag)){
			String Date = req.getParameter("Year");
			jsonArray = JSONArray.fromObject(getYearWaterFlowInfo(station,Date));
		}
		JSONObject jsonValue=new JSONObject();
		jsonValue.element("result", jsonArray);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}
	//date 
	ArrayList<WaterFlow_day> getDateWaterFlowInfo(String stationName,String Date){
		Tonnage = getTonnage(stationName);
		ArrayList<WaterFlow_day> list = new ArrayList<WaterFlow_day>();
		String sql = "select AVG(DATEPART(hh,testingtime)) hour, " +
				"AVG(detection6) waterflow ,"+Tonnage+" tonnage  from detection_data  WHERE sewageID IN (SELECT sewageID FROM sewage WHERE short_title ='"+stationName+"') " +
				" AND (CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+Date+"',120)) " +
				"group by CONVERT(varchar(13),testingtime,120) order by hour asc ";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null){
				while(resultSet.next()){
					WaterFlow_day temp = new WaterFlow_day();
					temp.setHour(resultSet.getInt(1));
					temp.setWater(resultSet.getFloat(2));
					temp.setTonnage(resultSet.getInt(3));
					list.add(temp);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}
	//month
	ArrayList<WaterFlow_month> getMonthWaterFlowInfo(String stationName,String Date){
		Tonnage = getTonnage(stationName);
		ArrayList<WaterFlow_month> list = new ArrayList<WaterFlow_month>();
		String sql = "SELECT AVG(DATEPART(dd,testingtime)) day, SUM(water) waterflow,"+Tonnage+" tonnage from statistic_day WHERE sewageID IN (SELECT sewageID FROM sewage WHERE short_title ='"+stationName+"') AND (CONVERT(varchar(7),testingtime,120) = CONVERT(varchar(7),'"+Date+"',120)) group by CONVERT(varchar(10),testingtime,120) ORDER BY day asc";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null){
				while(resultSet.next()){
					WaterFlow_month temp = new WaterFlow_month();
					temp.setDate(resultSet.getInt(1));
					temp.setWater(resultSet.getFloat(2));
					temp.setTonnage(resultSet.getInt(3));
					list.add(temp);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}
	//year
	ArrayList<WaterFlow_year> getYearWaterFlowInfo(String stationName,String Date){
		Tonnage = getTonnage(stationName);
		ArrayList<WaterFlow_year> list = new ArrayList<WaterFlow_year>();
		String sql = "SELECT AVG(DATEPART(mm,testingtime)) month," +
				" SUM(water) waterflow, "+Tonnage+" AS tonnage from statistic_day   " +
				"where sewageID IN (SELECT sewageID FROM sewage WHERE short_title ='"+stationName+"') AND(CONVERT(varchar(4),testingtime,120) = CONVERT(varchar(4),'"+Date+"',120)) group by CONVERT(varchar(7),testingtime,120)";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null){
				while(resultSet.next()){
					WaterFlow_year temp = new WaterFlow_year();
					temp.setMonth(resultSet.getInt(1));
					temp.setWater(resultSet.getFloat(2));
					temp.setTonnage(resultSet.getInt(3));
					list.add(temp);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}
	
	private int getTonnage (String stationName){
		
		String sql = "SELECT tonnage FROM sewage WHERE short_title='"+stationName+"'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int tonnage = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null){
				while(resultSet.next()){
					tonnage = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return tonnage;
		
	}
	
}
