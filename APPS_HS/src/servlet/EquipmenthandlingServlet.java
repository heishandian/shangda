package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DBHelp;

//import oracle.sql.DATE;

import beans.Sewage;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author apple get information of warnings and values of sensors
 */
public class EquipmenthandlingServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("sewage_name");
		String testingtime = req.getParameter("alertTime");
		String equipmentno = req.getParameter("alertInfo");
		Integer equipment = Integer.parseInt(equipmentno);
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			station = new String(station.getBytes("iso-8859-1"), "utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		printWriter.print(equipentHandling(station, testingtime, equipment));
		printWriter.flush();
		printWriter.close();
	}

	private String equipentHandling(String station, String testingtime,
			Integer equipment) {
		int id = 0;
		String sql = null;
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql1 = "select sewageID from sewage where short_title='"
				+ station + "'";
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql1);
			while (resultSet.next()) {
				id= resultSet.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id==0){
			return "There is no this station!";
		}else if (equipment >= 6 && equipment <= 22) {
			sql = "update run_data_abnormal set isrepaired=1  where sewageid='"
					+ id + "' and testingtime='" + testingtime
					+ "' and equipmentno='" + equipment + "'";
		} else if (1 <= equipment && equipment <= 5) {
			sql = "update detection_data_abnormal set isrepaired=1  where sewageid='"
					+ id
					+ "' and testingtime='"
					+ testingtime
					+ "' and detectionno='" + equipment + "'";
		} else if (equipment == 110) {
			return "Power bolt does not need to deal with!";
		} else {
			return "There is not this equipment!";
		}
		try {
			statement.executeUpdate(sql);
			return "success";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}

	}

}
