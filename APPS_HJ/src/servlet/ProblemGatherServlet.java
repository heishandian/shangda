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
import beans.ProblemGather;
import beans.SewageDoc;

/**
 * @author apple
 * 
 */
public class ProblemGatherServlet extends HttpServlet {
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
		String requestflag = null;
		String title = null;
		String description = null;
		String finder = null;
		String findtime = null;
		requestflag = req.getParameter("requestFlag");// 请求标志
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		switch (requestflag.charAt(0)) {
		case 'R':
			JSONArray jsonArray1 = new JSONArray();
			JSONArray jsonArray2 = new JSONArray();
			JSONObject jsonValue = null;
			operationnum = req.getParameter("operationnum");// 运营编号
			if ("".equals(operationnum)) {
				short_title = req.getParameter("short_title").trim();// 污水站点
				county = req.getParameter("county");// 区域
				if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
					try {
						short_title = new String(
								short_title.getBytes("iso-8859-1"), "utf-8");
						county = new String(county.getBytes("iso-8859-1"), "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			pagingnum = req.getParameter("pagingnum");// 页码
			items = req.getParameter("items");// 一页的条数
			Integer pagingnum1 = Integer.parseInt(pagingnum);
			Integer items1 = Integer.parseInt(items);
			
			try {
				jsonArray1 = JSONArray.fromObject(getProblemGather(pagingnum1,
						items1, county, short_title, operationnum));
				jsonArray2 = JSONArray.fromObject(getProblemGatherCount(county,
						short_title, pagingnum1, items1, operationnum));
				jsonValue = new JSONObject();
				jsonValue.element("result1", jsonArray1);
				jsonValue.element("result2", jsonArray2);
			} catch (Exception e) {
				e.printStackTrace();
			}

			printWriter.print(jsonValue);
			temp = 0;
			break;

		case 'C':// 新增 printWriter.print(createRecord(req)); break; case
			printWriter.print(creatProblemGather(req));
			break;
		default:
			break;
		}

		printWriter.flush();
		printWriter.close();

	}

	private String creatProblemGather(HttpServletRequest req)
			throws IOException {
		int sewageid = 0;
		String short_title = null;
		String county = null;
		String title = null;
		String description = null;
		String finder = null;
		String findtime = null;
		short_title = req.getParameter("short_title").trim();// 污水站点
		title = req.getParameter("title").trim();// 污水站点
		description = req.getParameter("description").trim();// 污水站点
		finder = req.getParameter("finder").trim();// 污水站点
		findtime = req.getParameter("findtime").trim();// 污水站点
		try {
			if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
				short_title = new String(short_title.getBytes("iso-8859-1"),
						"utf-8");
				title = new String(title.getBytes("iso-8859-1"), "utf-8");
				description = new String(description.getBytes("iso-8859-1"),
						"utf-8");
				finder = new String(finder.getBytes("iso-8859-1"), "utf-8");
				findtime = new String(findtime.getBytes("iso-8859-1"), "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((sewageid = gersewageid(short_title)) != 0) {
			String sql = "INSERT INTO problemGather(title,description,finder,findtime,sewageid) VALUES(?,?,?,?,?)";
			Connection connection = DBHelp.getConnection();
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, title);
				ps.setString(2, description);
				ps.setString(3, finder);
				ps.setString(4, findtime);
				ps.setInt(5, sewageid);
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

	private ArrayList<ProblemGather> getProblemGather(int pagingnum, int items,
			String county, String short_title, String operationnum) {
		Integer sewageid = 0;
		String sql = null;
		int areaid = 0;
		ArrayList<ProblemGather> list = new ArrayList<ProblemGather>();
		// 全部为空 查询所有区域的站点档案信息
		if ("".equals(county) && "".equals(short_title)
				&& "".equals(operationnum)) {
			sql = "SELECT  top "
					+ items
					+ " b.short_title,a.title,a.finder,a.findtime,a.description FROM problemGather a,sewage b  WHERE  a.id not in (SELECT TOP (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") id FROM problemGather ORDER BY findtime DESC) AND a.sewageid=b.sewageID ORDER BY findtime DESC";
		} else if (!"".equals(county) && "".equals(short_title)
				&& "".equals(operationnum)) {// 区域不为空，查询指定区域的
			sql = "SELECT  top "
					+ items
					+ " b.short_title,a.title,a.finder,a.findtime,a.description FROM problemGather a,sewage b  WHERE  a.id not in (SELECT TOP (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") id FROM problemGather WHERE "
					+ "sewageid IN (SELECT sewage.sewageID as sewageid FROM sewage , area WHERE sewage.areaID = area.id AND area.name = '"
					+ county
					+ "' ) "
					+ " ORDER BY findtime DESC) AND a.sewageid IN (SELECT sewage.sewageID as sewageid FROM sewage , area WHERE sewage.areaID = area.id AND area.name = '"
					+ county
					+ "' ) and a.sewageid=b.sewageID ORDER BY findtime DESC";
		} else if (!"".equals(county) && !"".equals(short_title)
				&& "".equals(operationnum)) {// 区域不为空、站定不为空，查询指定站点
			sewageid = gersewageid(short_title);
			sql = "SELECT  top "
					+ items
					+ " '"
					+ short_title
					+ "',title,finder,findtime,description FROM problemGather  WHERE  id not in (SELECT TOP (("
					+ pagingnum + "-1)*" + items
					+ ") id FROM problemGather WHERE sewageid = " + sewageid
					+ " ORDER BY findtime DESC) AND sewageid = " + sewageid
					+ " ORDER BY findtime DESC";
		} else if (!"".equals(operationnum)) {// 只要运营标号非空，那么按照运营标号查询
			sql = "SELECT  top "
					+ items
					+ " (SELECT short_title FROM sewage WHERE operationnum = '"
					+ operationnum
					+ "') as short_title,title,finder,findtime,description FROM problemGather  WHERE  id not in (SELECT TOP (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") id FROM problemGather WHERE sewageid = (SELECT sewageID FROM sewage WHERE operationnum = '"
					+ operationnum
					+ "') ORDER BY findtime DESC) AND sewageid = (SELECT sewageID FROM sewage WHERE operationnum = '"
					+ operationnum + "')  ORDER BY findtime DESC";

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
					ProblemGather Problemgather = new ProblemGather();
					Problemgather.setShort_title(resultSet.getString(1));
					Problemgather.setTitle(resultSet.getString(2));
					Problemgather.setFinder(resultSet.getString(3));
					Problemgather.setFindtime(resultSet.getString(4));// 处理工艺
					Problemgather.setDescription(resultSet.getString(5));
					list.add(Problemgather);
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

	private PadingUtil getProblemGatherCount(String county, String short_title,
			int pagingnum, int items, String operationnum) {
		int sewageid = 0;
		int areaid = 0;
		String sql = null;
		if ("".equals(county) && "".equals(short_title)
				&& "".equals(operationnum)) {
			sql = "SELECT   count(*)  FROM problemGather  ";
		} else if (!"".equals(county) && "".equals(short_title)
				&& "".equals(operationnum)) {// 区域不为空，查询指定区域的
			sql = "SELECT count(*) FROM problemGather  WHERE  sewageid IN (SELECT sewage.sewageID as sewageid FROM sewage , area WHERE sewage.areaID = area.id AND area.name = '"
					+ county + "' ) ";
		} else if (!"".equals(county) && !"".equals(short_title)
				&& "".equals(operationnum)) {// 区域不为空、站定不为空，查询指定站点
			sewageid = gersewageid(short_title);
			sql = "SELECT count(*) FROM problemGather  WHERE  sewageid="
					+ sewageid + "";

		} else if (!"".equals(operationnum)) {// 只要运营标号非空，那么按照运营标号查询
			sql = "SELECT count(*) FROM problemGather  WHERE sewageid = (SELECT sewageID FROM sewage WHERE operationnum = '"
					+ operationnum + "')  ";
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
