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
import beans.SewageDoc;

/**
 * @author apple
 * 
 */
public class SewagedocServlet extends HttpServlet {
	int temp = 0;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String short_title = null;
		String county = null;
		String operationnum = null;
		String pagingnum = null;
		String items = null;
		short_title = req.getParameter("short_title").trim();// 污水站点
		county = req.getParameter("county");// 区域
		operationnum = req.getParameter("operationnum");// 运营编号
		pagingnum = req.getParameter("pagingnum");// 页码
		items = req.getParameter("items");// 一页的条数
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			try {
				short_title = new String(short_title.getBytes("iso-8859-1"),
						"utf-8");
				county = new String(county.getBytes("iso-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			resp.setContentType("text/json");
			resp.setCharacterEncoding("UTF-8");
			PrintWriter printWriter = resp.getWriter();
			JSONArray jsonArray1 = new JSONArray();
			JSONArray jsonArray2 = new JSONArray();
			JSONObject jsonValue = null;
			try {
				jsonArray1 = JSONArray.fromObject(getSewageDoc(pagingnum1,
						items1, county, short_title, operationnum));
				jsonArray2 = JSONArray.fromObject(getDevicedocCount(county,
						short_title, pagingnum1, items1, operationnum));
				jsonValue = new JSONObject();
				jsonValue.element("result1", jsonArray1);
				jsonValue.element("result2", jsonArray2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			printWriter.print(jsonValue);
			temp = 0;
			printWriter.flush();
			printWriter.close();
		}
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

	private ArrayList<SewageDoc> getSewageDoc(int pagingnum, int items,
			String county, String short_title, String operationnum) {
		Integer sewageid = 0;
		String sql = null;
		int areaid = 0;
		ArrayList<SewageDoc> list = new ArrayList<SewageDoc>();
		// 全部为空 查询所有区域的站点档案信息
		if ("".equals(county) && "".equals(short_title)
				&& "".equals(operationnum)) {
			sql = "SELECT  top "
					+ items
					+ " short_title,operationnum,controlID,controlMethod,tonnage,emissionStandard FROM sewage  WHERE  sewageID not in (SELECT TOP (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") sewageID FROM sewage ORDER BY short_title ASC) ORDER BY short_title ASC ";
		} else if (!"".equals(county) && "".equals(short_title)
				&& "".equals(operationnum)) {// 区域不为空，查询指定区域的
			areaid = getAreaID(county);
			sql = "SELECT  top "
					+ items
					+ " short_title,operationnum,controlID,controlMethod,tonnage,emissionStandard FROM sewage WHERE areaID = "
					+ areaid + " and  sewageID not in (SELECT TOP (("
					+ pagingnum + "-1)*" + items
					+ ") sewageID FROM sewage WHERE areaID = " + areaid
					+ "  ORDER BY short_title ASC) ORDER BY short_title ASC  ";
		} else if (!"".equals(county) && !"".equals(short_title)
				&& "".equals(operationnum)) {// 区域不为空、站定不为空，查询指定站点
			sewageid = gersewageid(short_title);
			sql = "SELECT  top "
					+ items
					+ " short_title,operationnum,controlID,controlMethod,tonnage,emissionStandard FROM sewage WHERE sewageID = "
					+ sewageid + " and  sewageID not in (SELECT TOP (("
					+ pagingnum + "-1)*" + items
					+ ") sewageID FROM sewage WHERE sewageID = " + sewageid
					+ "  ORDER BY short_title ASC) ORDER BY short_title ASC  ";
		} else if (!"".equals(operationnum)) {// 只要运营标号非空，那么按照运营标号查询
			sql = "SELECT  top "
					+ items
					+ " short_title,operationnum,controlID,controlMethod,tonnage,emissionStandard FROM sewage WHERE operationnum = '"
					+ operationnum + "' and  sewageID not in (SELECT TOP (("
					+ pagingnum + "-1)*" + items
					+ ") sewageID FROM sewage WHERE operationnum = '"+operationnum+"' ORDER BY short_title ASC) ORDER BY short_title ASC  ";
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
					SewageDoc sewageDoc = new SewageDoc();
					sewageDoc.setShort_title(resultSet.getString(1));
					sewageDoc.setOperationnum(resultSet.getString(2));
					sewageDoc.setControlID(resultSet.getString(3));
					sewageDoc.setControlMethod(resultSet.getString(4));// 处理工艺
					sewageDoc.setTonnage(resultSet.getString(5));
					sewageDoc.setEmissionStandard(resultSet.getString(6));// 排放标准
					list.add(sewageDoc);
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

	private PadingUtil getDevicedocCount(String county, String short_title,
			int pagingnum, int items, String operationnum) {
		int sewageid = 0;
		int areaid = 0;
		String sql = null;
		if ("".equals(county) && "".equals(short_title)
				&& "".equals(operationnum)) {
			sql = "SELECT  count(*) FROM sewage ";
		} else if (!"".equals(county) && "".equals(short_title)
				&& "".equals(operationnum)) {// 区域不为空，查询指定区域的
			areaid = getAreaID(county);
			sql = "SELECT   count(*) FROM sewage WHERE areaID = " + areaid
					+ " ";
		} else if (!"".equals(county) && !"".equals(short_title)
				&& "".equals(operationnum)) {// 区域不为空、站定不为空，查询指定站点
			sewageid = gersewageid(short_title);
			sql = "SELECT  count(*)  FROM sewage WHERE sewageID = " + sewageid
					+ " ";
		} else if (!"".equals(operationnum)) {// 只要运营标号非空，那么按照运营标号查询
			sql = "SELECT count(*)  FROM sewage WHERE operationnum = '"+operationnum+"' ";
		}
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					int a = resultSet.getInt(1);
					padingUtil.setItemscount(a);// 总条数
					padingUtil.setItems(temp);// 当前条数
					padingUtil.setPagingnum(pagingnum);// 当前页页码
					if ((a % items) == 0) {
						padingUtil.setPagecount((int) (Math.ceil(a / items)));
					} else {
						padingUtil
								.setPagecount((int) (Math.ceil(a / items) + 1));
					}

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

	private int getAreaID(String area) {
		String sql = "SELECT id FROM area WHERE name='" + area + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int areaid = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					areaid = resultSet.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return areaid;
	}

}
