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
import beans.EquipRepair;

/**
 * @author apple
 * 
 */
public class MaintenanceServlet extends HttpServlet {
	int item_q=0;
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
		case 'C':// 新增设备维修记录（保养单）
			printWriter.print(creatEquipRepairRecord(req));
			break;
		case 'R':// 查询设备维修记录（保养单）
			JSONArray jsonArray1 = new JSONArray();
			JSONArray jsonArray2 = new JSONArray();
			JSONObject jsonValue = new JSONObject();
			try {
				jsonArray1 = JSONArray.fromObject(getEquipRepairRecord(req));
				jsonArray2 = JSONArray.fromObject(getCount(req));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsonValue.element("result1", jsonArray1);
			jsonValue.element("result2", jsonArray2);
			item_q=0;
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
		String repairreason = req.getParameter("repairreason").trim();// 保养原因
		String repaircontent = req.getParameter("repaircontent").trim();// 保养内容
		String consumematerial = req.getParameter("consumematerial").trim();// 消耗材料
		String repairman = req.getParameter("repairman").trim();// 维修人员
		String completetime = req.getParameter("completetime").trim();//维护时间
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			short_title = new String(short_title.getBytes("iso-8859-1"),
					"utf-8");
			repairreason = new String(repairreason.getBytes("iso-8859-1"),
					"utf-8");
			repaircontent = new String(repaircontent.getBytes("iso-8859-1"),
					"utf-8");
			consumematerial = new String(
					consumematerial.getBytes("iso-8859-1"), "utf-8");
			repairman = new String(repairman.getBytes("iso-8859-1"), "utf-8");
			completetime = new String(completetime.getBytes("iso-8859-1"),
					"utf-8");
		}
		if ((sewageid = gersewageid(short_title)) != 0) {
			String sql = "INSERT INTO equiprepairrecord(sewageid,repairreason,repaircontent,consumematerial,repairman,completetime) VALUES(?,?,?,?,?,?)";
			Connection connection = DBHelp.getConnection();
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, sewageid);
				ps.setString(2, repairreason);
				ps.setString(3, repaircontent);
				ps.setString(4, consumematerial);
				ps.setString(5, repairman);
				ps.setString(6, completetime);
				ps.execute();
			} catch (Exception e) {
				e.printStackTrace();
				return "create failed";
			} finally {
				DBHelp.closeResultSet(resultSet);
				DBHelp.closeStatement(statement);
				DBHelp.closeConnection(connection);
			}

		}else{
			return "sewage does not exist";
		}
		return "create success";

	}

	// 查询设备维修记录，也就是保养单
	private ArrayList<EquipRepair> getEquipRepairRecord(HttpServletRequest req) {
		Integer sewageid = 0;
		ArrayList<EquipRepair> list = new ArrayList<EquipRepair>();
		String county = req.getParameter("county");
		String short_title = req.getParameter("short_title");
		String pagingnum = req.getParameter("pagingnum");
		String items = req.getParameter("items");
		//String time = req.getParameter("time");
		String sql = null;
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		if (pagingnum1 < 0) {
			pagingnum1 = 1;
		}
		Integer items1 = Integer.parseInt(items);
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			try {
				short_title = new String(short_title.getBytes("iso-8859-1"), "utf-8");
				county = new String(county.getBytes("iso-8859-1"), "utf-8"); 
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("".equals(county) && "".equals(short_title)) //查询所有区域的保养单信息
		{ sewageid = gersewageid(short_title);
		sql = "SELECT TOP "+ items1+ "  y.short_title as short_title,y.operationnum,X.repairreason,X.repaircontent,X.consumematerial," +
				"X.repairman,CONVERT(varchar(10),X.completetime,120) FROM equiprepairrecord X,sewage y WHERE X.id not in (SELECT TOP (("+items1+")*("+pagingnum1+"-1)) id " +
						" FROM equiprepairrecord  ORDER BY completetime DESC)   AND  x.sewageid=y.sewageID ORDER BY completetime DESC";
		}
		if (!"".equals(county) && !"".equals(short_title)) //区域非空，站点非空   注意不需要按照时间查询了
			{ sewageid = gersewageid(short_title);
			  /*sql = "SELECT TOP "+ items1+ "  '"+short_title+"' as short_title,X.repairreason,X.repaircontent,X.consumematerial," +
				"X.repairman,CONVERT(varchar(10),X.completetime,120) FROM equiprepairrecord X WHERE X.id not in (SELECT TOP (("+items1+")*("+pagingnum1+"-1)) id " +
						" FROM equiprepairrecord WHERE CONVERT(varchar(10),completetime,120) = CONVERT(varchar(10),'"+time+"',120)  " +
								"AND sewageid='"+sewageid+"'  ORDER BY completetime DESC) AND CONVERT(varchar(10),completetime,120) = CONVERT(varchar(10),'"+time+"',120) " +
										" AND sewageid='"+sewageid+"'  ORDER BY completetime DESC";*/
			sql = "SELECT TOP "+ items1+ "  '"+short_title+"' as short_title,(SELECT operationnum FROM sewage WHERE short_title='"+short_title+"') as operationnum,X.repairreason,X.repaircontent,X.consumematerial," +
					"X.repairman,CONVERT(varchar(10),X.completetime,120) FROM equiprepairrecord X WHERE X.id not in (SELECT TOP (("+items1+")*("+pagingnum1+"-1)) id " +
							" FROM equiprepairrecord  " +
									"where sewageid='"+sewageid+"'  ORDER BY completetime DESC)  " +
											" AND sewageid='"+sewageid+"'  ORDER BY completetime DESC";
			}
		if(!"".equals(county) &&  "".equals(short_title)){//区域非空，站点空按照区域查询
			/*sql = "SELECT TOP "+ items1+ "  y.short_title as short_title,X.repairreason,X.repaircontent,X.consumematerial," +
					"X.repairman,CONVERT(varchar(10),X.completetime,120) FROM equiprepairrecord X,sewage y WHERE X.id not in (SELECT TOP (("+items1+")*("+pagingnum1+"-1)) id " +
							" FROM equiprepairrecord WHERE CONVERT(varchar(10),completetime,120) = CONVERT(varchar(10),'"+time+"',120)  " +
									"AND sewageid IN (SELECT sewageid FROM sewage,area WHERE sewage.areaID=area.id and area.name = '"+county+"')  ORDER BY completetime DESC) AND CONVERT(varchar(10),completetime,120) = CONVERT(varchar(10),'"+time+"',120) " +
											" AND X.sewageid IN (SELECT sewage.sewageid FROM sewage,area WHERE sewage.areaID=area.id and area.name = '"+county+"') and x.sewageid=y.sewageID ORDER BY completetime DESC";
			*/
			sql = "SELECT TOP "+ items1+ "  y.short_title as short_title,y.operationnum,X.repairreason,X.repaircontent,X.consumematerial," +
					"X.repairman,CONVERT(varchar(10),X.completetime,120) FROM equiprepairrecord X,sewage y WHERE X.id not in (SELECT TOP (("+items1+")*("+pagingnum1+"-1)) id " +
							" FROM equiprepairrecord  " +
									"where sewageid IN (SELECT sewageid FROM sewage,area WHERE sewage.areaID=area.id and area.name = '"+county+"')  ORDER BY completetime DESC)   " +
											" AND X.sewageid IN (SELECT sewage.sewageid FROM sewage,area WHERE sewage.areaID=area.id and area.name = '"+county+"') and x.sewageid=y.sewageID ORDER BY completetime DESC";
		}
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					item_q++;
					EquipRepair equipRepair = new EquipRepair();
					equipRepair.setShort_title(resultSet.getString(1));
					equipRepair.setOperationnum(resultSet.getString(2));
					equipRepair.setRepairreason(resultSet.getString(3));
					equipRepair.setRepaircontent(resultSet.getString(4));
					equipRepair.setConsumematerial(resultSet.getString(5));
					equipRepair.setRepairman(resultSet.getString(6));
					equipRepair.setCompletetime(resultSet.getString(7));
					list.add(equipRepair);
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
	
	
private PadingUtil getCount(HttpServletRequest req){
	Integer sewageid = 0;
	ArrayList<EquipRepair> list = new ArrayList<EquipRepair>();
	String county = req.getParameter("county");
	String short_title = req.getParameter("short_title");
	String pagingnum = req.getParameter("pagingnum");
	String items = req.getParameter("items");
	//String time = req.getParameter("time");
	PadingUtil padingUtil = new PadingUtil();
	String sql = null ;
	Integer pagingnum1 = Integer.parseInt(pagingnum);
	Integer items1 = Integer.parseInt(items);
	if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
		try {
			short_title = new String(short_title.getBytes("iso-8859-1"), "utf-8");
			county = new String(county.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	if ("".equals(county) && "".equals(short_title)){
		sql = "SELECT COUNT(*) count FROM equiprepairrecord ";
	}
	if (!"".equals(county) && !"".equals(short_title)) {//区域非空，站点非空,按站点查询
		sewageid = gersewageid(short_title);
		 //sql = "SELECT COUNT(*) count FROM equiprepairrecord WHERE CONVERT(varchar(10),completetime,120) = CONVERT(varchar(10),'"+time+"',120) AND sewageid='"+sewageid+"'  ";
		sql = "SELECT COUNT(*) count FROM equiprepairrecord WHERE sewageid='"+sewageid+"'  ";
	}
	if (!"".equals(county) && "".equals(short_title)) {
	// sql = "SELECT COUNT(*) count FROM equiprepairrecord WHERE CONVERT(varchar(10),completetime,120) = CONVERT(varchar(10),'"+time+"',120) and sewageid IN (SELECT sewageid FROM sewage,area WHERE sewage.areaID=area.id and area.name = '"+county+"') ";
	 sql = "SELECT COUNT(*) count FROM equiprepairrecord WHERE  sewageid IN (SELECT sewageid FROM sewage,area WHERE sewage.areaID=area.id and area.name = '"+county+"') ";
	}
	Connection connection = DBHelp.getConnection();
	Statement statement = null;
	ResultSet resultSet = null;

	try {
		statement = connection.createStatement();
		resultSet = statement.executeQuery(sql);
		if (resultSet != null) {

			while (resultSet.next()) {
				padingUtil.setItemscount(resultSet.getInt(1));
				padingUtil.setItems(item_q);
				padingUtil.setPagingnum(pagingnum1);
				padingUtil.setPagecount( (int) (Math.ceil(((resultSet.getFloat(1))/(float)items1))));
				System.out.println(Math.ceil(3f/2f));
			
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


}
