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

import beans.EquipmentError;
import beans.Sewageinfo;


/**
 * @author apple get the names of counties (of the city)
 */
public class SewageServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doGet(req, resp);
		this.doPost(req, resp);//请求转发
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// System.out.println("1235465");
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray = JSONArray.fromObject(getSewageinfoList());
		// JSONArray jsonArray2 = JSONArray.fromObject(getSewageinfoList1());
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray);
		// jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();

	}

	private ArrayList<EquipmentError> getSewageinfoList() {
		//ArrayList<Sewageinfo> list = new ArrayList<Sewageinfo>();
		ArrayList<EquipmentError> list1 = new ArrayList<EquipmentError>();
		String sql1 = "select X.short_title,X.coordinateX,X.coordinateY from  sewage X " ;
				/*"Y.equipment1state,Y.equipment2state,Y.equipment3state from  " +
				"sewage X Left Join run_data Y  ON X.sewageID=Y.sewageID AND " +
				"CONVERT(varchar(12),Y.testingtime,111) = CONVERT(varchar(12),GETDATE(),111) ";*/
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
				EquipmentError sewageinfo = new EquipmentError();
				sewageinfo.setName(resultSet.getString(1));
				sewageinfo.setX(resultSet.getFloat(2));
				sewageinfo.setY(resultSet.getFloat(3));
				/*sewageinfo.setEquimmentstate1(resultSet.getString(4));
				sewageinfo.setEquimmentstate2(resultSet.getString(5));
				sewageinfo.setEquimmentstate3(resultSet.getString(6));*/
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
		System.out.println("-->" + list1);
		return list1;
	}

}
