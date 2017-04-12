package com.czq.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

import com.czq.entitiy.Admin;
import com.czq.util.DBHelp;

/**
 * @author apple
 *
 */
public class AdminConfigServlet extends HttpServlet{

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
		case 'C'://新建
			printWriter.print(createRecord(req));
			break;
		case 'U'://修改
			printWriter.print(updateRecord(req));
			break;
		case 'R'://查询
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonValue = new JSONObject();
			jsonArray = JSONArray.fromObject(readArrayList());
			jsonValue.element("result", jsonArray);
			printWriter.print(jsonValue);
			break;
		default :
			break;
		}
		
		printWriter.flush();
		printWriter.close();
	}

	private String createRecord(HttpServletRequest req) throws IOException{
		String name = req.getParameter("name").trim();//姓名
		String telephone = req.getParameter("telephone").trim();//电话
		String adress = req.getParameter("adress").trim();//地址
		String email = req.getParameter("email").trim();//邮箱
		String sql ="INSERT INTO admin(name,telephone,adress,email) VALUES(?,?,?,?)";
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			name = new String(name.getBytes("iso-8859-1"),"utf-8");
			adress = new String(adress.getBytes("iso-8859-1"),"utf-8");
		}
		Connection connection = DBHelp.getConnection();
		PreparedStatement statement = null;
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, telephone);
			ps.setString(3, adress);
			ps.setString(4, email);
			statement.executeUpdate(sql);
			return "Add successfully";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Failed";
		}finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}
	
	private String updateRecord(HttpServletRequest req) throws IOException{
		String name = req.getParameter("name").trim();//姓名
		String telephone = req.getParameter("telephone").trim();//电话
		String adress = req.getParameter("adress").trim();//地址
		String email = req.getParameter("email").trim();//邮箱
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			name = new String(name.getBytes("iso-8859-1"),"utf-8");
			adress = new String(adress.getBytes("iso-8859-1"),"utf-8");
		}
		String sql ="UPDATE admin SET name='"+name+"',telephone='"+telephone+"',address='"+adress+"',email='"+email+"'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			return "Update successfully";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Failed";
		}finally{
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}
	
	private ArrayList<Admin> readArrayList() {
		ArrayList<Admin> list = new ArrayList<Admin>();
		String sql = " SELECT name,telephone,adress,email FROM admin";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					Admin admin = new Admin();
					admin.setName(resultSet.getString(1));
					admin.setTelephone(resultSet.getString(2));
					admin.setAdress(resultSet.getString(3));
					admin.setEmail(resultSet.getString(4));
					list.add(admin);
				}
			}
		} catch (SQLException e) {
			e.getStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}
	
}
