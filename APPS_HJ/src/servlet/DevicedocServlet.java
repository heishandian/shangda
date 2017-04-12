package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import util.DBHelp;
import beans.Devicedoc;

/**
 * @author apple
 * 
 */
public class DevicedocServlet extends HttpServlet {
	int temp = 0;
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String short_title = req.getParameter("short_title").trim();// 污水站点
		String county = req.getParameter("county");//区域
		String pagingnum = req.getParameter("pagingnum");//页码
		String items = req.getParameter("items");//一页的条数
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			try {
				short_title = new String(short_title.getBytes("iso-8859-1"),
						"utf-8");
				county = new String(county.getBytes("iso-8859-1"),
						"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		String flag = req.getParameter("requestFlag");
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = new JSONArray();
		JSONArray jsonArray2 = new JSONArray();
		switch (flag.charAt(0)) {
		case 'C':// 新增设备档案
			printWriter.print(creatEquipRepairRecord(req));
			break;
		case 'R':// 查询设备档案
		
			jsonArray1 = JSONArray.fromObject(getEquipRepairRecord( pagingnum1,  items1,  county,  short_title));
			jsonArray2 = JSONArray.fromObject(getDevicedocCount(county,short_title,pagingnum1,items1));
			JSONObject jsonValue = new JSONObject();
			jsonValue.element("result1", jsonArray1);
			jsonValue.element("result2", jsonArray2);
			printWriter.print(jsonValue);
			 temp = 0;
			 printWriter.flush();
			printWriter.close();
			break;
		default:
			break;
		}
		}}
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

	private ArrayList<Devicedoc> getEquipRepairRecord(int pagingnum, int items, String county, String short_title) {
		Integer sewageid = 0;
		String sql = null;
		ArrayList<Devicedoc> list = new ArrayList<Devicedoc>();
		sewageid = gersewageid( short_title) ;
		//查询所有区域
		if ("".equals(county) && "".equals(short_title)) {
			 sql = " SELECT TOP "+ items+" X.devicename,X.devicetype,X.setuptime FROM devicedoc X  WHERE X.id not in (SELECT TOP (("+ pagingnum+ "-1)*"+items+") id FROM devicedoc  ORDER BY setuptime DESC)  ORDER BY X.setuptime DESC";
		}
		//查询指定区域
		if (!"".equals(county) && "".equals(short_title)){
			 sql = " SELECT TOP "+ items+" X.devicename,X.devicetype,X.setuptime FROM devicedoc X  WHERE X.id not in (SELECT TOP (("+ pagingnum+ "-1)*"+items+") id FROM devicedoc " +
			 		"WHERE sewageid in (select sewage.sewageID FROM area,sewage WHERE sewage.areaID=area.id and area.name='"+county+"' ) ORDER BY setuptime DESC) AND X.sewageid in (select sewage.sewageID FROM area,sewage WHERE sewage.areaID=area.id and area.name='"+county+"' ) ORDER BY X.setuptime DESC";
		}
		//查询指定站点
		if (!"".equals(county) && !"".equals(short_title)) {//区域和站点非空，按照站点查询
			 sql = " SELECT TOP "+ items+" X.devicename,X.devicetype,X.setuptime FROM devicedoc X  WHERE X.id not in (SELECT TOP (("+ pagingnum+ "-1)*"+items+") id FROM devicedoc WHERE sewageid='"+sewageid+"' ORDER BY setuptime DESC) AND X.sewageid='"+sewageid+"' ORDER BY X.setuptime DESC";
		}
		
			 Connection connection = DBHelp.getConnection();
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if (resultSet != null) {
					while (resultSet.next()) {
						temp++;
						Devicedoc devicedoc = new Devicedoc();
						devicedoc.setDevicename(resultSet.getString(1));
						devicedoc.setDevicetype(resultSet.getString(2));
						devicedoc.setSetuptime(resultSet.getString(3));
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
		
		return list;
	}
	private PadingUtil getDevicedocCount(String county,String short_title,int pagingnum,int items){
		int sewageid = 0;
		sewageid = gersewageid( short_title) ;
		String sql = null;
		if ("".equals(county) && "".equals(short_title)) {
			 sql = " SELECT count(*) FROM devicedoc ";
		}
		if (!"".equals(county) && "".equals(short_title)) {
			 sql = "SELECT count(*) FROM devicedoc WHERE sewageid in (select sewage.sewageID FROM area,sewage WHERE sewage.areaID=area.id and area.name='"+county+"' )";}
		if (!"".equals(county) && !"".equals(short_title)) {
		 sql = "SELECT count(*) FROM devicedoc WHERE sewageid='"+sewageid+"' ";}
		
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					padingUtil.setItemscount(resultSet.getInt(1));
					padingUtil.setItems(temp);
					padingUtil.setPagingnum(pagingnum);
					padingUtil.setPagecount( (int) (Math.ceil(((double)(resultSet.getInt(1))/items))));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return padingUtil;

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
