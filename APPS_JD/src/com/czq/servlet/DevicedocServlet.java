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
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.czq.entitiy.Devicedoc;
import com.czq.entitiy.EquipRepair;
import com.czq.entitiy.Role;
import com.czq.entitiy.SysUser;
import com.czq.util.DBHelp;

/**
 * @author apple
 * 
 */
public class DevicedocServlet extends HttpServlet {
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
		case 'C':// 新增设备档案
			printWriter.print(creatEquipRepairRecord(req));
			break;
		case 'R':// 查询设备档案
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonValue = new JSONObject();
			jsonArray = JSONArray.fromObject(getEquipRepairRecord(req));
			jsonValue.element("result", jsonArray);
			printWriter.print(jsonValue);
			break;
		default:
			break;
		}
		printWriter.flush();
		printWriter.close();
	}

	private String creatEquipRepairRecord(HttpServletRequest req)
			throws IOException {
		Integer sewageid = 0;
		String short_title = req.getParameter("short_title").trim();// 污水站点
		String devicename = req.getParameter("devicename").trim();// 保养原因
		String devicetype = req.getParameter("devicetype").trim();// 保养内容
		String setuptime = req.getParameter("setuptime").trim();// 消耗材料
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			short_title = new String(short_title.getBytes("iso-8859-1"),
					"utf-8");
			devicename = new String(devicename.getBytes("iso-8859-1"), "utf-8");
			devicetype = new String(devicetype.getBytes("iso-8859-1"), "utf-8");
			setuptime = new String(setuptime.getBytes("iso-8859-1"), "utf-8");
		}
		if ((sewageid = gersewageid(short_title)) != 0) {
			String sql = "INSERT INTO devicedoc(sewageid,devicename,devicetype,setuptime) VALUES(?,?,?,?)";
			Connection connection = DBHelp.getConnection();
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, sewageid);
				ps.setString(2, devicename);
				ps.setString(3, devicetype);
				ps.setString(4, setuptime);
				ps.execute();
			} catch (Exception e) {
				e.printStackTrace();
				return "create failed";
			} finally {
				DBHelp.closeResultSet(resultSet);
				DBHelp.closeStatement(statement);
				DBHelp.closeConnection(connection);
			}

		} else {
			return "sewage does not exist";
		}
		return "create success";

	}

	// 查询设备维修记录，也就是保养单
	private ArrayList<Devicedoc> getEquipRepairRecord(HttpServletRequest req) {
		Integer sewageid = 0;
		ArrayList<Devicedoc> list = new ArrayList<Devicedoc>();
		String short_title = req.getParameter("short_title").trim();// 污水站点
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			try {
				short_title = new String(short_title.getBytes("iso-8859-1"),
						"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if ((sewageid = gersewageid(short_title)) != 0) {

			String sql = "SELECT Y.short_title,X.devicename,X.devicetype,X.setuptime FROM devicedoc X,sewage Y WHERE  X.sewageid=Y.sewageID AND X.sewageid='"+sewageid+"' ORDER BY X.setuptime DESC";
			Connection connection = DBHelp.getConnection();
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if (resultSet != null) {
					while (resultSet.next()) {
						Devicedoc devicedoc = new Devicedoc();
						devicedoc.setShort_title(resultSet.getString(1));
						devicedoc.setDevicename(resultSet.getString(2));
						devicedoc.setDevicetype(resultSet.getString(3));
						devicedoc.setSetuptime(resultSet.getString(4));
						list.add(devicedoc);
					}
				}
			} catch (SQLException e) {
				e.getStackTrace();
			} finally {
				DBHelp.closeResultSet(resultSet);
				DBHelp.closeStatement(statement);
				DBHelp.closeConnection(connection);
			}
		}else{
			System.out.println("sewage does not exist");
		}
		return list;
	}

	private int gersewageid(String short_title) {
		String sql = "SELECT sewageID FROM sewage WHERE short_title='"
				+ short_title + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					return resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return 0;
	}

}
