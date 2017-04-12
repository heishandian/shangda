package com.czq.servlet;

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

import com.czq.util.DBHelp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author apple
 * get the names of counties (of the city)
 */
public class AreaServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("1235465");
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray = JSONArray.fromObject(getAreaList());
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
		
	}
	
	private ArrayList<String> getAreaList()
	{
		ArrayList<String> list=new ArrayList<String>();
		String sql="select name from dbo.area";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while(resultSet.next())
			{
				list.add(resultSet.getString(1));
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		System.out.println("-->"+list);
		return list;
	}
}
