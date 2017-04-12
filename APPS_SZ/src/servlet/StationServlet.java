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
 * get station list in the county pointed
 */
public class StationServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String area = req.getParameter("county");
		System.out.println("before encoding-->"+area);
		System.out.println(req.getCharacterEncoding());
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			area = new String(area.getBytes("iso-8859-1"),"utf-8");
		}
		System.out.println("after encoding-->"+area);
//		System.out.println("1235465");
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter=resp.getWriter();
		JSONArray jsonArray = JSONArray.fromObject(getStationList(area));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}
	
	private ArrayList<String> getStationList(String area)
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("请选择站点");
		String sql = "select x.short_title from dbo.sewage x,dbo.area y where y.name='" +
				area+"' and y.id=x.areaID order by x.short_title ";
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
//		System.out.println("-->"+list);
		return list;
	}
	

}
