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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.DBHelp;
import beans.Role;
import beans.SysUser;

/**
 * @author apple
 * 
 */
public class AdminConfigServlet extends HttpServlet {

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
		case 'C':// 新增
			printWriter.print(createRecord(req));
			break;
		case 'U':// 更新
			printWriter.print(updateRecord(req));
			break;
		case 'R':// 查询
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonValue = new JSONObject();
			jsonArray = JSONArray.fromObject(readArrayList());
			jsonValue.element("result", jsonArray);
			printWriter.print(jsonValue);
			break;
		case 'J'://查询角色
			JSONArray jsonArray1 = new JSONArray();
			JSONObject jsonValue1 = new JSONObject();
			jsonArray = JSONArray.fromObject(getAllRoles());
			jsonValue1.element("result", jsonArray);
			printWriter.print(jsonValue1);
			break;
		case 'F'://复位密码
			printWriter.print(ResetPassWord(req));
			break;
		default:
			break;
		}

		printWriter.flush();
		printWriter.close();
	}

	private String ResetPassWord(HttpServletRequest req) {
		String loginname = req.getParameter("loginname").trim();// 姓名
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			try {
				loginname = new String(loginname.getBytes("iso-8859-1"),
						"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 姓名
		}
		String sql = "UPDATE sysuser SET loginpwd=? WHERE loginname =?";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		if (isLoginNameExisted(loginname) == 0) {
			return "wrong loginname";
		} else {
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, loginname);
				ps.setString(2, loginname);
				ps.execute();
			} catch (SQLException e) {

				e.printStackTrace();
				return "reset failed";
			} finally {
				DBHelp.closeResultSet(resultSet);
				DBHelp.closeStatement(statement);
				DBHelp.closeConnection(connection);
			}
			return "reset success";
		}
	}

	private List<Role> getAllRoles() {
		String sql = "SELECT id,name FROM sysrole";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		List<Role> list = new ArrayList<Role>();

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				Role role = new Role();
				role.setId(resultSet.getInt(1));
				role.setRole(resultSet.getString(2));
				list.add(role);
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

	private String createRecord(HttpServletRequest req) throws IOException {
		Integer userid = 0;
		Integer roleid = 0;
		Map map = new HashMap();
		String loginname = req.getParameter("loginname").trim();// 登陆名
		String username = req.getParameter("username").trim();// 姓名
		String role = req.getParameter("role").trim();// 角色
		String department = req.getParameter("department").trim();// 部门
		String telephone = req.getParameter("telephone").trim();// 电话
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			username = new String(username.getBytes("iso-8859-1"), "utf-8");// 姓名
			role = new String(role.getBytes("iso-8859-1"), "utf-8");// 角色
			department = new String(department.getBytes("iso-8859-1"), "utf-8");// 部门
		}
		String sql1 = "INSERT INTO sysuserrole(userid,roleid) VALUES(?,?)";
		String sql2 = "INSERT INTO sysuser(username,loginpwd,loginname,department,telephone) VALUES(?,?,?,?,?)";
		userid = isUserExisted(username);
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		roleid = isRoleExisted(role);
		if (userid != 0 || roleid == 0) {
			return "User already exists or role not exist ";
		} else {
			try {

				PreparedStatement ps1 = connection.prepareStatement(sql2);
				ps1.setString(1, username);
				ps1.setString(2, loginname);
				ps1.setString(3, loginname);
				ps1.setString(4, department);
				ps1.setString(5, telephone);
				ps1.execute();
				PreparedStatement ps2 = connection.prepareStatement(sql1);
				ps2.setInt(1, isUserExisted(username));
				ps2.setInt(2, roleid);
				ps2.execute();
			} catch (Exception e) {
				e.printStackTrace();
				return "create failed";
			} finally {
				DBHelp.closeResultSet(resultSet);
				DBHelp.closeStatement(statement);
				DBHelp.closeConnection(connection);
			}
			return "create success";
		}

	}

	private String updateRecord(HttpServletRequest req) throws IOException {
		String key = null;
		String value = null;
		Integer userid = 0;
		Integer roleid = 0;
		Map map = new HashMap();
		String loginname = req.getParameter("loginname").trim();// 登陆名
		String username = req.getParameter("username").trim();
		String role = req.getParameter("role").trim();
		String department = req.getParameter("department").trim();
		String telephone = req.getParameter("telephone").trim();
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			username = new String(username.getBytes("iso-8859-1"), "utf-8");// 姓名
			role = new String(role.getBytes("iso-8859-1"), "utf-8");// 角色
			department = new String(department.getBytes("iso-8859-1"), "utf-8");// 部门
		}
		String sql1 = "SELECT id FROM sysuser WHERE username='" + username
				+ "'";
		String sql2 = "SELECT id FROM sysrole WHERE name='" + role + "'";
		String sql3 = "UPDATE sysuserrole SET roleid=? WHERE userid=?";
		String sql4 = "UPDATE sysuser SET username=?,loginpwd=?,loginname=?,department=?,telephone=? WHERE id=?";

		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql1);// 利用用户姓名查询用户id
			while (resultSet.next()) {
				userid = resultSet.getInt(1);
			}
			if (userid == 0) {
				return "User not exist";
			}
			resultSet = statement.executeQuery(sql2);// 查询角色roleid
			while (resultSet.next()) {
				roleid = resultSet.getInt(1);
			}
			if (roleid == 0) {
				return "role not exist";
			}
			PreparedStatement ps2 = connection.prepareStatement(sql3);
			ps2.setInt(1, userid);
			ps2.setInt(2, roleid);
			ps2.execute();
			PreparedStatement ps1 = connection.prepareStatement(sql4);
			ps1.setString(1, username);
			ps1.setString(2, loginname);
			ps1.setString(3, loginname);
			ps1.setString(4, department);
			ps1.setString(5, telephone);
			ps1.setInt(6, userid);
			ps1.execute();
		} catch (Exception e) {
			e.printStackTrace();
			return "update failed";
		} finally {

			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return "update success";

	}

	private ArrayList<SysUser> readArrayList() {
		ArrayList<SysUser> list = new ArrayList<SysUser>();
		String sql = "SELECT loginname,username,Z.name AS role,department,telephone FROM sysuser X,sysuserrole Y,sysrole Z WHERE X.id=Y.userid AND Y.roleid=Z.id";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					SysUser sysUser = new SysUser();
					sysUser.setLoginname(resultSet.getString(1));
					sysUser.setUsername(resultSet.getString(2));
					sysUser.setRole(resultSet.getString(3));
					sysUser.setDepartment(resultSet.getString(4));
					sysUser.setTelephone(resultSet.getString(5));
					list.add(sysUser);
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

	private int isUserExisted(String username) {
		String sql = "SELECT id FROM sysuser WHERE username='" + username + "'";
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

	private int isRoleExisted(String role) {
		String sql = "SELECT id FROM sysrole WHERE name='" + role + "'";
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

	private int isLoginNameExisted(String loginname) {
		String sql = "SELECT count(*)  FROM sysuser WHERE loginname='" + loginname
				+ "'";
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
