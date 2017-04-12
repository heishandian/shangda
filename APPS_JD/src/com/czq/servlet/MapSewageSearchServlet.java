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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.czq.entitiy.MapSewageSearch;
import com.czq.util.DBHelp;

/**
 * @author apple get the names of counties (of the city)
 */
public class MapSewageSearchServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doGet(req, resp);
		this.doPost(req, resp);
	}

	@Override
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
		JSONArray jsonArray1 = JSONArray.fromObject(getMapSewageInfoList(station));
		//JSONArray jsonArray2 = JSONArray.fromObject(getDetectionList1(station));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray1);
		//jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();

	}

	private ArrayList<MapSewageSearch> getMapSewageInfoList(String station) {
		//ArrayList<Sewageinfo> list = new ArrayList<Sewageinfo>();
		ArrayList<MapSewageSearch> list1 = new ArrayList<MapSewageSearch>();
		String sql1 = "select	X.short_title,X.coordinateX,X.coordinateY from  " +
				"sewage X WHERE X.short_title='"+station+"'";
		// String sql1="select short_title,coordinateX from dbo.sewage";
		// String sql2="select coordinateY from dbo.sewage";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql1);
			// resultSet2 = statement.executeQuery(sql2);
			while (resultSet.next()) {
				MapSewageSearch sewageinfo = new MapSewageSearch();
				sewageinfo.setName(resultSet.getString(1));
				sewageinfo.setX(resultSet.getFloat(2));
				sewageinfo.setY(resultSet.getFloat(3));
			
				list1.add(sewageinfo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			// DBHelp.closeResultSet(resultSet1);
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list1;
	}

}
