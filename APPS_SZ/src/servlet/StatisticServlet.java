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

import beans.statistic;

/**
 * @author apple get information about detection data of station pointed
 */
public class StatisticServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		this.doPost(req, resp);
	}

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
		JSONArray jsonArray = JSONArray.fromObject(getDetectionList(station));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}

	private ArrayList<statistic> getDetectionList(String station) {
  ArrayList<statistic> list = new ArrayList<statistic>();

		String sql1 = "select (ISNULL(SUM(X.detection6),0)) AS day_water," +
				"AVG( Y.reduceCOD)*(ISNULL(SUM(X.detection6),0)) AS COD," +
				"AVG( Y.reduceNH3N)*(ISNULL(SUM(X.detection6),0)) AS NH3N," +
				"AVG( Y.reduceP)*(ISNULL(SUM(X.detection6),0)) AS P " +
				"from detection_data X ,sewage Y " +
				"WHERE Y.short_title='"+station+"' AND CONVERT(varchar(12),testingtime,111) = CONVERT(varchar(12),GETDATE(),111)" +
				" and X.sewageID = y.sewageID ";
	
		ResultSet resultSet1 = null;
		Statement statement = null;
		ResultSet resultSet = null;
		Connection connection = DBHelp.getConnection();
		try {
			statement = connection.createStatement();
			resultSet1 = statement.executeQuery(sql1);
			
			while (resultSet1.next()) {
				statistic det = new statistic();
				det.setDay_water(resultSet1.getString(1));
				det.setCOD(resultSet1.getString(2));
				det.setNH3N(resultSet1.getString(3));
				det.setP(resultSet1.getString(4));
				list.add(det);

			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return list;
}
}