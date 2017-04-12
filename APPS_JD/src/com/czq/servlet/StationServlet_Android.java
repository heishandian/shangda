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

import com.czq.entitiy.Station;
import com.czq.util.DBHelp;

/**
 * @author apple get station list in the county pointed
 */
public class StationServlet_Android extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String area = req.getParameter("county");
		System.out.println("before encoding-->" + area);
		System.out.println(req.getCharacterEncoding());
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			area = new String(area.getBytes("iso-8859-1"), "utf-8");
		}
		System.out.println("after encoding-->" + area);
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray = JSONArray.fromObject(getStationList(area));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}

	private ArrayList<Station> getStationList(String area) {
		ArrayList<Station> list = new ArrayList<Station>();

		String sql = "select x.sewageID,x.short_title from dbo.sewage x,dbo.area y where y.name='"
				+ area + "' and y.id=x.areaID ";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				Station station = new Station();
				station.setId(resultSet.getInt(1));
				station.setName(resultSet.getString(2));
				list.add(station);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}

}
