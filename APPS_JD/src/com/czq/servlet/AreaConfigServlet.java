package com.czq.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

import com.czq.util.DBHelp;
import com.czq.entitiy.Area;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author apple county configure
 */
public class AreaConfigServlet extends HttpServlet {

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
		case 'C':// 新建区域
			printWriter.print(createRecord(req));
			break;
		case 'U':// 更新
			printWriter.print(updateRecord(req));
			break;
		case 'R':// 查詢
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonValue = new JSONObject();
			jsonArray = JSONArray.fromObject(readArrayList());
			jsonValue.element("result", jsonArray);
			printWriter.print(jsonValue);
			break;
		case 'D':
			break;
		}
		printWriter.flush();
		printWriter.close();
	}

	private String createRecord(HttpServletRequest req) throws IOException {

		String name = req.getParameter("name");
		String coordinateX = req.getParameter("coordinateX");
		String coordinateY = req.getParameter("coordinateY");
		String introduce = req.getParameter("introduce");
		String Administrator = req.getParameter("administrator");
		int areaID = 0;
		int adminID = 0;
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			name = new String(name.getBytes("iso-8859-1"), "utf-8");// 姓名
			introduce = new String(introduce.getBytes("iso-8859-1"), "utf-8");// 介紹
		}
		String sql = "INSERT INTO area(name,coordinateX,coordinateY,introduce) VALUES(?,?,?,?)";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		if (isExistedArea(name) != 0) {
			return "Area has existed";
		} else {
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, name);
				ps.setString(2, coordinateX);
				ps.setString(3, coordinateY);
				ps.setString(4, introduce);
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				return "failed ";
			}
			if (((areaID = isExistedArea(name)) != 0)
					&& ((adminID = getAdminID(Administrator)) != 0)) {
				String sql1 = "INSERT INTO admin_area(adminid,areaid) VALUES('"
						+ adminID + "','" + areaID + "')";
				try {
					statement.executeUpdate(sql1);
				} catch (SQLException e) {
					e.printStackTrace();
					return "failed";
				}

			} else {
				return "failed";
			}

		}
		return "insert successfunlly";

	}

	private String updateRecord(HttpServletRequest req) throws IOException {
		String name = req.getParameter("name");
		String coordinateX = req.getParameter("coordinateX");
		String coordinateY = req.getParameter("coordinateY");
		String introduce = req.getParameter("introduce");
		String Administrator = req.getParameter("administrator");
		int areaID = 0;
		int adminID = 0;
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			name = new String(name.getBytes("iso-8859-1"), "utf-8");// 姓名
			introduce = new String(introduce.getBytes("iso-8859-1"), "utf-8");// 介紹
		}
		String sql = "INSERT INTO area(name,coordinateX,coordinateY,introduce) VALUES(?,?,?,?)";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		if (isExistedArea(name) != 0) {
			return "Area has existed";
		} else {
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, name);
				ps.setString(2, coordinateX);
				ps.setString(3, coordinateY);
				ps.setString(4, introduce);
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				return "failed ";
			}
			if (((areaID = isExistedArea(name)) != 0)
					&& ((adminID = getAdminID(Administrator)) != 0)) {
				String sql1 = "INSERT INTO admin_area(adminid,areaid) VALUES('"
						+ adminID + "','" + areaID + "')";
				try {
					statement.executeUpdate(sql1);
				} catch (SQLException e) {
					e.printStackTrace();
					return "failed";
				}

			} else {
				return "failed";
			}

		}

		return "insert successfunlly";

	}

	private ArrayList<Area> readArrayList() {
		ArrayList<Area> list = new ArrayList<Area>();
		String sql = "SELECT area.name,admin.name,admin.telephone FROM area,admin_area,admin WHERE area.id=admin_area.areaid AND admin_area.adminid=admin.id";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					Area area = new Area();
					area.setArea_name(resultSet.getString(1));
					area.setAdmin_name(resultSet.getString(2));
					area.setTelephone(resultSet.getString(3));
					list.add(area);
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

	private int isExistedArea(String name) {
		String sql = "select id from area where name = '" + name + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		Integer flag = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					flag = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return flag;
	}

	private int getAdminID(String Administractor) {
		String sql = "select id from admin where name = '" + Administractor
				+ "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		Integer flag = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					flag = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return flag;
	}

	private boolean isExisted(String keyValue) {
		String sql = "select count(*) from dbo.area where name = '" + keyValue
				+ "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					if (0 == resultSet.getInt(1))
						return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return true;
	}

}
