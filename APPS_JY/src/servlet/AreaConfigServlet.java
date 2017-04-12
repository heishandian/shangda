package servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

import util.DBHelp;

import beans.Area;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author apple
 * county configure
 */
public class AreaConfigServlet extends HttpServlet{

	protected void doGet(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException, IOException{
		this.doPost(req, resp);
	}
	
	protected void doPost(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException, IOException{
		System.out.println("county configure ~~~");
		String flag = req.getParameter("requestFlag");
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		
		switch(flag.charAt(0)){
			case 'C':
				printWriter.print(createRecord(req));
				break;
			case 'U':
				printWriter.print(updateRecord(req));
				break;
			case 'R':/*more carefully*/
				JSONArray jsonArray = new JSONArray();
				JSONObject jsonValue = new JSONObject();
				jsonArray = JSONArray.fromObject(readArrayList());
				jsonValue.element("result",jsonArray);
				printWriter.print(jsonValue);
				break;
			case 'D':
				break;
		}
//		jsonValue.element("result",jsonArray);
//		
//		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}
	
	private String createRecord(HttpServletRequest req) throws IOException{
		String sql = "insert into dbo.area(";
		String key = null;
		String value = null;
		Map map = new HashMap();
		map.put("superior_area", 1);
		map.put("name",req.getParameter("name").trim());
		map.put("intro",req.getParameter("intro").trim());
		map.put("principal",req.getParameter("principal".trim()));
		map.put("tel",req.getParameter("tel").trim());
		
		if(map.get("name") == null || "".equals(map.get("name").toString()))
		{
			return "Unavailable key add";
		}
		if(isExisted(map.get("name").toString())){
			return "Have already existed";
		}
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			String tempString = new String(req.getParameter("name").getBytes("iso-8859-1"),"utf-8");
			map.put("name", tempString.trim());
			
			tempString = new String(req.getParameter("principal").getBytes("iso-8859-1"),"utf-8");
			map.put("principal", tempString.trim());
			
			tempString = new String(req.getParameter("intro").getBytes("iso-8859-1"),"utf-8");
			map.put("intro", tempString.trim());
		}
		
		Iterator iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry entry = (Map.Entry) iterator.next();
			if (key == null){
				key = entry.getKey().toString();
				value = "'"+entry.getValue().toString()+"'";
			}else{
				key += ","+entry.getKey().toString();
				value += ",'"+entry.getValue().toString()+"'";
			}
			
		}
		sql += key+" ) values ("+value+")";
		System.out.println("add sql-->"+sql);
		
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
		}finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}
	
	private String updateRecord(HttpServletRequest req) throws IOException{
		String responseString = null;
		String sql = "update dbo.area set ";
		String key = null;
		String value = null;
		Map map = new HashMap();
		map.put("name",req.getParameter("name").trim());
		map.put("intro",req.getParameter("intro").trim());
		map.put("principal",req.getParameter("principal".trim()));
		map.put("tel",req.getParameter("tel").trim());
//		map.put("areaId",req.getParameter("areaId"));
		
		if(map.get("name") == null || "".equals(map.get("name").toString()))
		{
			return "Unavailable key add";
		}
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			String tempString = new String(req.getParameter("name").getBytes("iso-8859-1"),"utf-8");
			map.put("name", tempString.trim());
			
			tempString = new String(req.getParameter("principal").getBytes("iso-8859-1"),"utf-8");
			map.put("principal", tempString.trim());

			tempString = new String(req.getParameter("intro").getBytes("iso-8859-1"),"utf-8");
			map.put("intro", tempString.trim());
		}
		
		Iterator iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry entry = (Map.Entry) iterator.next();
//			key = entry.getKey().toString();
//			value = "'"+entry.getValue().toString()+"'";
			if (key == null){
				key = entry.getKey().toString();
				value = "'"+entry.getValue().toString()+"'";
				sql += key+" = "+value;
			}else{
				key = entry.getKey().toString();
				value = "'"+entry.getValue().toString()+"'";
				sql += ","+key+" = "+value;
			}			
		}
		sql += "where areaId = '"+req.getParameter("areaId").toString()+"'";
		System.out.println("update sql-->"+sql);
		
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
		}finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		
	}
	
	private ArrayList<Area> readArrayList(){
		ArrayList<Area> list = new ArrayList<Area>();
		String sql = "select * from dbo.area ";
//		areaID,superior_area,name,map,intro,principal,tel
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null){
				while(resultSet.next()){
					Area tempArea = new Area();
					tempArea.setAreaId(resultSet.getInt(1));
					tempArea.setSuperiorArea(resultSet.getInt(2));
					tempArea.setName(resultSet.getString(3));
					tempArea.setIntro(resultSet.getString(5));
					tempArea.setPrincipal(resultSet.getString(6));
					tempArea.setTel(resultSet.getString(7));
//					System.out.println("123-->"+tempArea.toString());
					
					list.add(tempArea);
				}
			}
			}catch(SQLException e){
				e.getStackTrace();
			}finally{
				DBHelp.closeResultSet(resultSet);
				DBHelp.closeStatement(statement);
				DBHelp.closeConnection(connection);
			}
//			System.out.println("area detail-->"+list.toString());
		return list;
	}
	
	private boolean isExisted(String keyValue){
		String sql = "select count(*) from dbo.area where name = '"+keyValue+"'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet !=null)
			{
				while(resultSet.next()){
					if(0 == resultSet.getInt(1))
						return false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}finally{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return true;
	}
	
	
}
