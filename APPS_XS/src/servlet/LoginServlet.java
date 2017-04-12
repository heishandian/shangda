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

import util.DBHelp;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * @author apple
 * login check (without session) 
 */
public class LoginServlet extends HttpServlet{

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String username=req.getParameter("username");
		String password=req.getParameter("password");
//		System.out.println("name:"+username+",pwd:"+password);
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter=resp.getWriter();
		if(loginCheck(username,password).equalsIgnoreCase("failed"))
		{
			printWriter.print("failed");
		}
		else
		{
			printWriter.print(loginCheck(username,password));
		}
		//System.out.println("name:"+username+",pwd:"+password);
		printWriter.flush();
		printWriter.close();
	}
	
	private String loginCheck(String username,String password)
	{
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			/*resultSet = statement.executeQuery("select right(sys.fn_varbintohexstr(hashbytes('md5','" +
					password+"')),32)");
			if(resultSet.next())
			{
				password = resultSet.getString(1);
			}*/
			resultSet = statement.executeQuery("select loginpwd,username from dbo.sysuser where loginname='" 
					+username.trim()+"'");
			if(resultSet != null)
			{
				while(resultSet.next()){
					String dbPwd = resultSet.getString(1);
					//System.out.println("-->"+dbPwd+"input-->"+password);
					if(dbPwd.equals(password.toString()))
					{
						//System.out.println("OK");
						return resultSet.getString(2);
					}
					return "failed";
				}
			}
			return "failed";
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		} 
		finally{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}

}
