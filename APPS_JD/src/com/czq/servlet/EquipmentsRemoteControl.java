package com.czq.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.czq.util.DBHelp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author apple
 * implement of the client request for remote control
 * such as release warnings of sensors or stop/start the sets
 */
public class EquipmentsRemoteControl extends HttpServlet{

	private String stationName;
	
	public String getStationName() {
		return stationName;
	}


	private void setStationName(String stationName) {
		this.stationName = stationName;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("stationName");
		//sometimes the CharacterSet is not "UTF-8"
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			station = new String(station.getBytes("iso-8859-1"),"utf-8");
		}
		setStationName(station);
		String controlString = req.getParameter("controlString");
//		System.out.println("controlString-->"+controlString);
		boolean flag = false;
		PrintWriter printWriter=resp.getWriter();
		
		//人工格栅 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("confirmGrating")){
			
			flag = confirmGrating();
		}
		//曝气机 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("releaseAirWarning")){
		
			flag = releaseAirWarning();
		}
		//污水泵 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("releaseElevatorWarning")){
		
			flag = releaseElevatorWarning();
		}
		//回流泵 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("releaseMudWarning")){
		
			flag = releaseMudWarning();
		}
		//温度传感器 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("releaseTemWarning")){
			
			flag = releaseTemWarning();
		}
		//PH传感器 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("releasePHWarning")){
		
			flag = releasePHWarning();
		}
		//ORP传感器 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("releaseORPWarning")){
		
			flag = releaseORPWarning();
		}
		//LS传感器 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("releaseLSWarning")){
		
			flag = releaseLSWarning();
		}
		//DO传感器 响应“解除警报”操作
		if(controlString.equalsIgnoreCase("releaseDOWarning")){
		
			flag = releaseDOWarning();
		}
		//打开／关闭 曝气机、污水泵、回流泵
		if(controlString.equalsIgnoreCase("submitRemoteControl")){
		
			String stateString = req.getParameter("stateString");
			String username = req.getParameter("username");
			
			flag = updateControlLog(stateString,username);
		}
		
		if(!flag){
			printWriter.print("0");
		}
		else{
			printWriter.print("update successfully!");
		}
		
		printWriter.flush();
		printWriter.close();
	
	}
	
	private String getNowDate()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new java.util.Date());
	}
	
	//without user identification
	boolean confirmGrating(){
		
		String station = getStationName();
		Connection connection = DBHelp.getConnection();
		
		String tempDateString = this.getNowDate();
		String sql = "update sewage set confirmGratingTime='"+tempDateString+
				"' where short_title='"+station+"'";
		Statement statement = null;
		//ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			//resultSet = statement.getResultSet();
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}

	boolean releaseAirWarning(){
		
		String station = getStationName();
		Connection connection = DBHelp.getConnection();
		String sql = "update sewage set equipment1state='"+String.valueOf(2) +
				"' where short_title='"+station+"'";
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
			//resultSet = statement.getResultSet();
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}
	
    boolean releaseElevatorWarning(){
    	
    	String station = getStationName();
		Connection connection = DBHelp.getConnection();
		String sql = "update sewage set equipment2state='"+String.valueOf(2) +
				"' where short_title='"+station+"'";
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
			//resultSet = statement.getResultSet();
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}

    boolean releaseMudWarning(){
    	
    	String station = getStationName();
		Connection connection = DBHelp.getConnection();
		String sql = "update sewage set equipment3state='"+String.valueOf(2) +
				"' where short_title='"+station+"'";
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
			//resultSet = statement.getResultSet();
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}
    
    boolean releaseTemWarning(){
    	
    	String station = getStationName();
    	String str;
		char tmp[]=new char[5];
		Connection connection = DBHelp.getConnection();
		String sql = "select device_alert from sewage where short_title='"+station+"'";
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					str = resultSet.getString(1);
					tmp[0]='1';
					tmp[1]=str.charAt(1);
					tmp[2]=str.charAt(2);
					tmp[3]=str.charAt(3);
					tmp[4]=str.charAt(4);
				}
			} else
				System.out.println("未查询到结果");
			sql = "update sewage set device_alert='"+String.valueOf(tmp) +
			"' where short_title='"+station+"'";
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeConnection(connection);
		}
	}
    
    boolean releasePHWarning(){
		
    	String station = getStationName();
    	String str;
		char tmp[]=new char[5];
		Connection connection = DBHelp.getConnection();
		String sql = "select device_alert from sewage where short_title='"+station+"'";
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					str = resultSet.getString(1);
					tmp[0]=str.charAt(0);
					tmp[1]='1';
					tmp[2]=str.charAt(2);
					tmp[3]=str.charAt(3);
					tmp[4]=str.charAt(4);
				}
			} else
				System.out.println("未查询到结果");
			sql = "update sewage set device_alert='"+String.valueOf(tmp) +
			"' where short_title='"+station+"'";
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeConnection(connection);
		}
	}
    
    boolean releaseORPWarning(){
		
    	String station = getStationName();
    	String str;
		char tmp[]=new char[5];
		Connection connection = DBHelp.getConnection();
		String sql = "select device_alert from sewage where short_title='"+station+"'";
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					str = resultSet.getString(1);
					tmp[0]=str.charAt(0);
					tmp[1]=str.charAt(1);
					tmp[2]='1';
					tmp[3]=str.charAt(3);
					tmp[4]=str.charAt(4);
				}
			} else
				System.out.println("未查询到结果");
			sql = "update sewage set device_alert='"+String.valueOf(tmp) +
			"' where short_title='"+station+"'";
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeConnection(connection);
		}
	}
    boolean releaseLSWarning(){
		
    	String station = getStationName();
    	String str;
		char tmp[]=new char[5];
		Connection connection = DBHelp.getConnection();
		String sql = "select device_alert from sewage where short_title='"+station+"'";
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					str = resultSet.getString(1);
					tmp[0]=str.charAt(0);
					tmp[1]=str.charAt(1);
					tmp[2]=str.charAt(2);
					tmp[3]='1';
					tmp[4]=str.charAt(4);
				}
			} else
				System.out.println("未查询到结果");
			sql = "update sewage set device_alert='"+String.valueOf(tmp) +
			"' where short_title='"+station+"'";
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeConnection(connection);
		}
	}
    boolean releaseDOWarning(){
		
    	String station = getStationName();
    	String str;
		char tmp[]=new char[5];
		Connection connection = DBHelp.getConnection();
		String sql = "select device_alert from sewage where short_title='"+station+"'";
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					str = resultSet.getString(1);
					tmp[0]=str.charAt(0);
					tmp[1]=str.charAt(1);
					tmp[2]=str.charAt(2);
					tmp[3]=str.charAt(3);
					tmp[4]='1';
				}
			} else
				System.out.println("未查询到结果");
			sql = "update sewage set device_alert='"+String.valueOf(tmp) +
			"' where short_title='"+station+"'";
			statement.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeConnection(connection);
		}
	}
    
    //improvement: carry out role authentication before the function
    boolean updateControlLog(String states,String username){
    	
    	String station = getStationName();
		String sqla = null;
		String sqlb = null;
		String sqlc = null;
		String sqld = null;
		int logId = -1;
		int roleId = -1;
		int controlId = -1;
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try{
			sqla = "select x.userid,x.roleid from dbo.userrole x,dbo.csusers y where " +
					"y.logid = '"+username+"' and y.userid = x.userid ";
			System.out.println("sqla-->"+sqla);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqla);
			
			if(resultSet != null){
				while(resultSet.next()){
					logId = resultSet.getInt(1);
					roleId = resultSet.getInt(2);
				}
			}else{
				return false;
			}
			
			if(roleId == 1 ||roleId == 2){
				return false;
			}else{
				sqlb = "select controlID from dbo.sewage where short_title = '"+station+"'";
				System.out.println("sqlb-->"+sqlb);
				resultSet = statement.executeQuery(sqlb);
				if(resultSet != null){
					while(resultSet.next()){
						controlId = resultSet.getInt(1);
					}
				}else{
					return false;
				}
				
				sqlc = "update dbo.sewage set equipment1state = "+states.charAt(0)+", " +
						"equipment2state = "+states.charAt(1)+", equipment3state = "+states.charAt(2);
				sqld = "insert into dbo.pump_control_log values('"+logId+"','"+controlId+"','" +
					new java.sql.Timestamp(new Date().getTime())+"','"+states.charAt(0)+"','"+states.charAt(1)+"','" +
					states.charAt(2)+"','"+states.charAt(3)+"','2')";
				System.out.println("sqlc-->"+sqlc);
				System.out.println("sqld-->"+sqld);
				statement.executeUpdate(sqlc);
				statement.executeUpdate(sqld);
				return true;
			}
		}catch(SQLException ex){
			ex.printStackTrace();
			return false;
		}finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeConnection(connection);
		
		}
	}
}
