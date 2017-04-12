package servlet;

//(SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'2016-04-15 08:00:00.000',120))

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DBHelp;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import beans.Alert;
import beans.PadingUtil;

public class AlertQueryServlet_old extends HttpServlet  {
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("stationName");
		String pagingnum = req.getParameter("pagingnum");
		String items = req.getParameter("items");
		String time = req.getParameter("time");
		String flag = req.getParameter("requestFlag");
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			station = new String(station.getBytes("iso-8859-1"), "utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray = new JSONArray();
		if ("RA".equalsIgnoreCase(flag)) {// 获取所有站点的报警信息
			System.out.println("不需要查所有站点的");
			// jsonArray = JSONArray.fromObject(getPointedAlertList(flag,
			// "all"));
		}
		if ("RC".equalsIgnoreCase(flag))// 指定区域的所有报警信息
		{
			String county = req.getParameter("countyName");
			if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
				county = new String(county.getBytes("iso-8859-1"), "utf-8");
			}
			jsonArray = JSONArray.fromObject(getPointedCountyAlertList(county));
		}
		if ("RP".equalsIgnoreCase(flag)) {// 指定站点的报警信息
			jsonArray = JSONArray.fromObject(getPointedAlertList(pagingnum1,
					items1, flag, station, time));
		}
		if ("RT".equalsIgnoreCase(flag)) {
			jsonArray = JSONArray.fromObject(getRealTimeAlertInfo());
		}
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}

	/* top 120 in alertInfo order by x.alerttime 20140708 */
	private ArrayList<Alert> getAlertList(String username) {
		ArrayList<Alert> list = new ArrayList<Alert>();
		String sql = "select top 120 x.*,y.short_title from dbo.alert x,dbo.sewage y,dbo.Administrator z "
				+ "where z.name = '"
				+ username
				+ "' and x.adminId = z.administratorId and x.sewageId = y.sewageId "
				+ "order by x.alerttime desc ";
		// System.out.println("---->"+sql);
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					Alert temp = new Alert();
					/*
					 * temp.setAlertID(resultSet.getInt(1));
					 * temp.setAdminID(resultSet.getInt(2));
					 * temp.setSewageID(resultSet.getInt(3));
					 * temp.setAlertInfo(resultSet.getString(4));
					 * temp.setAlertTime(resultSet.getString(5));
					 * temp.setState(resultSet.getInt(6));
					 * temp.setShortTitle(resultSet.getString(7));
					 */

					list.add(temp);
				}
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}

	/* top 60 in alertInfo county */
	private ArrayList<Alert> getPointedCountyAlertList(String countyName) {
		ArrayList<Alert> list = new ArrayList<Alert>();
		String sql1 = null;
		String sql2 = null;
		String sql3 = null;

		sql1 = "SELECT A.testingtime,B.short_title,A.equipmentno,A.isrepaired,"
				+ "E.name FROM run_data_abnormal A ,sewage B,area C,admin_area D,"
				+ "admin E WHERE A.sewageid=B.sewageID AND B.areaID=C.id AND C.id=D.areaid "
				+ "AND D.adminid=E.id AND CONVERT(varchar(10),A.testingtime,120) = CONVERT(varchar(10),getdate(),120) AND C.name='"
				+ countyName + "'"; // 指定区域报警信息

		sql2 = "SELECT A.testingtime,B.short_title,A.detectionno,A.isrepaired,E.name"
				+ " FROM detection_data_abnormal A ,sewage B,area C,admin_area D,admin "
				+ "E WHERE A.sewageid=B.sewageID AND B.areaID=C.id AND C.id=D.areaid AND"
				+ " D.adminid=E.id AND CONVERT(varchar(10),A.testingtime,120) = CONVERT(varchar(10),getdate(),120) AND C.name='"
				+ countyName + "'";

		sql3 = "SELECT GETDATE() AS time,A.short_title,110 as info,D.name FROM ( SELECT areaID,short_title,name FROM sewage WHERE sewageID in(( SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),getdate(),120)) ))AS A,area B,admin_area C,admin D WHERE A.areaID=B.id AND B.id=C.areaid AND C.adminid=D.id AND B.name='"
				+ countyName + "'";

		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		// ResultSet resultSet1 = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql1);
			// resultSet1 = statement.executeQuery(sql2);
			if (resultSet != null) {
				while (resultSet.next()) {
					Alert temp = new Alert();
					temp.setAlertTime(resultSet.getString(1));
					temp.setSewage_name(resultSet.getString(2));
					temp.setAlertInfo(resultSet.getInt(3));
					temp.setState(resultSet.getInt(4));
					temp.setAdmin(resultSet.getString(5));
					list.add(temp);
				}
			}
			resultSet = statement.executeQuery(sql2);
			if (resultSet != null) {
				while (resultSet.next()) {
					Alert temp = new Alert();
					temp.setAlertTime(resultSet.getString(1));
					temp.setSewage_name(resultSet.getString(2));
					temp.setAlertInfo(resultSet.getInt(3));
					temp.setState(resultSet.getInt(4));
					temp.setAdmin(resultSet.getString(5));
					list.add(temp);
				}
			}
			resultSet = statement.executeQuery(sql3);
			if (resultSet != null) {
				while (resultSet.next()) {
					Alert temp = new Alert();
					temp.setAlertTime(resultSet.getString(1));
					temp.setSewage_name(resultSet.getString(2));
					temp.setAlertInfo(resultSet.getInt(3));
					temp.setAdmin(resultSet.getString(4));
					list.add(temp);
				}

			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}

	/**
	 * 获取指定站点的报警信息
	 * 
	 * @param flag
	 * @param stationName
	 * @return
	 */
	private ArrayList<Alert> getPointedAlertList (int pagingnum, int items,
			String flag, String stationName, String time) {
		int sewageid = getSewageID(stationName);
		String admin = getAdmin(stationName);
		int total_items1 = 0;
		int total_items2 = 0;
		int total_items3 = 0;
		int k = 0; 
		int flag1=0;
		int flagg=0;
		int flagb=0;
		int a=0;
		int b=0;
		ArrayList<Alert> list = new ArrayList<Alert>();
		String sql0 = null;
		String sql1 = null;
		String sql2 = null;
		String sql3 = null;
		if (flag.equals("RP")) {
		/*	
			  sql1 = "SELECT testingtime,'" + stationName +
			  "' AS short_title,equipmentno,isrepaired, (SELECT name from admin WHERE id IN (SELECT adminid FROM admin_area WHERE areaid IN(SELECT areaID FROM sewage WHERE short_title='"
			  + stationName +
			  "') ) ) AS name FROM run_data_abnormal WHERE CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),getdate(),120) AND sewageid= (SELECT sewageID FROM sewage WHERE short_title='"
			  + stationName + "')";
			 */

			sql1 = "SELECT TOP "
					+ items
					+ " testingtime,'"
					+ stationName
					+ "' AS short_title, equipmentno,isrepaired, '"
					+ admin
					+ "' as name  FROM run_data_abnormal WHERE runid NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") runid FROM run_data_abnormal where sewageid="
					+ sewageid
					+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
					+ time
					+ "',120) ORDER BY testingtime desc) AND sewageid="
					+ sewageid
					+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
					+ time + "',120) ORDER BY testingtime desc";

			/*sql2 = "SELECT TOP "
					+ items
					+ " testingtime,'"
					+ stationName
					+ "' AS short_title, detectionno,isrepaired,'"
					+ admin
					+ "' as name  FROM detection_data_abnormal WHERE detectionID NOT IN (select top (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") detectionID FROM detection_data_abnormal where sewageid="
					+ getSewageID(stationName)
					+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
					+ time
					+ "',120) ORDER BY testingtime desc) AND sewageid="
					+ getSewageID("+stationName+")
					+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
					+ time + "',120) ORDER BY testingtime desc";*/

			/*
			 * sql2 = "SELECT testingtime,'" + stationName +
			 * "' AS short_title,detectionno,isrepaired, (SELECT name from admin WHERE id IN (SELECT adminid FROM admin_area WHERE areaid IN(SELECT areaID FROM sewage WHERE short_title='"
			 * + stationName +
			 * "') ) ) AS name FROM detection_data_abnormal WHERE CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),getdate(),120) AND sewageid= (SELECT sewageID FROM sewage WHERE short_title='"
			 * + stationName + "')";
			 */
			/*
			 * sql3 =
			 * "SELECT GETDATE() AS time,A.short_title,110 as info,D.name FROM ( SELECT areaID,short_title,name FROM sewage WHERE sewageID in(( SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),getdate(),120)))) AS A,area B,admin_area C,admin D WHERE A.areaID=B.id AND B.id=C.areaid AND C.adminid=D.id AND A.short_title='"
			 * + stationName + "'";
			 */

			/*sql3 = "SELECT short_title FROM sewage WHERE sewageID IN(SELECT TOP "
					+ items
					+ " sewageID  "
					+ "FROM (SELECT sewageID FROM sewage WHERE sewageID in ( (SELECT DISTINCT sewageID FROM sewage) "
					+ "EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
					+ time
					+ "',120) )) "
					+ "ORDER BY sewageID) AS A WHERE sewageID NOT IN (SELECT TOP (("
					+ pagingnum
					+ "-1)*"
					+ items
					+ ") sewageID FROM (SELECT sewageID FROM sewage WHERE sewageID in ( (SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
					+ time + "',120) )) ORDER BY sewageID)AS B))";*/

		}
		// 哪一天的断电断线情况
		// ( SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT
		// sewageID FROM run_data WHERE CONVERT(varchar(10),testingtime,120) =
		// CONVERT(varchar(10),'"+time+"',120))
		// 断电断线sewageID
		// SELECT sewageID FROM sewage WHERE sewageID in ( (SELECT DISTINCT
		// sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID FROM run_data
		// WHERE CONVERT(varchar(10),testingtime,120) =
		// CONVERT(varchar(10),'"+time+"',120) ))
		if (flag.equals("RA")) {
			sql1 = "SELECT A.testingtime,B.short_title,A.equipmentno,A.isrepaired,"
					+ "E.name FROM run_data_abnormal A ,sewage B,area C,admin_area D,"
					+ "admin E WHERE A.sewageid=B.sewageID AND B.areaID=C.id AND C.id=D.areaid "
					+ "AND D.adminid=E.id AND CONVERT(varchar(10),A.testingtime,120) = CONVERT(varchar(10),getdate(),120)";

			sql2 = "SELECT A.testingtime,B.short_title,A.detectionno,A.isrepaired,E.name"
					+ " FROM detection_data_abnormal A ,sewage B,area C,admin_area D,admin "
					+ "E WHERE A.sewageid=B.sewageID AND B.areaID=C.id AND C.id=D.areaid AND"
					+ " D.adminid=E.id AND CONVERT(varchar(10),A.testingtime,120) = CONVERT(varchar(10),getdate(),120) ";
			sql3 = "SELECT GETDATE() AS time,A.short_title,110 as info,D.name FROM ( SELECT areaID,short_title,name FROM sewage WHERE sewageID in(( SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),getdate(),120)))) AS A,area B,admin_area C,admin D WHERE A.areaID=B.id AND B.id=C.areaid AND C.adminid=D.id ";

		}

		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql1);
				if (resultSet != null) {
					while (resultSet.next()) {
						total_items1++;
						Alert temp = new Alert();
						temp.setAlertTime(resultSet.getString(1));
						temp.setSewage_name(resultSet.getString(2));
						temp.setAlertInfo(resultSet.getInt(3));
						temp.setState(resultSet.getInt(4));
						temp.setAdmin(resultSet.getString(5));
						list.add(temp);
					}

				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
         k=items-total_items1;
			if (0 < total_items1 && total_items1 < items) {
				flagg=pagingnum;
				sql2 = "SELECT TOP ("+k+") testingtime,'"
						+ stationName
						+ "' AS short_title, detectionno,isrepaired,'"
						+ admin
						+ "' as name  FROM detection_data_abnormal WHERE sewageid="
						+ sewageid
						+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
						+ time + "',120) ORDER BY testingtime desc";
				try {
					resultSet = statement.executeQuery(sql2);
					if (resultSet != null) {
						while (resultSet.next()) {
							flag1=1;
							total_items2++;
							Alert temp = new Alert();
							temp.setAlertTime(resultSet.getString(1));
							temp.setSewage_name(resultSet.getString(2));
							temp.setAlertInfo(resultSet.getInt(3));
							temp.setState(resultSet.getInt(4));
							temp.setAdmin(resultSet.getString(5));
							list.add(temp);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (total_items1 == 0) {//设备故障查询为空
				int num=pagingnum;
				sql2 = "SELECT TOP "
						+ items
						+ " testingtime,'"
						+ stationName
						+ "' AS short_title, detectionno,isrepaired,'"
						+ admin
						+ "' as name  FROM detection_data_abnormal WHERE detectionID NOT IN (select top ((("
						+ pagingnum
						+ "-1-"+flagg+")*"
						+ items
						+ ")+("
						+ total_items2
						+ ")) detectionID FROM detection_data_abnormal where sewageid="
						+ getSewageID(stationName)
						+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
						+ time
						+ "',120) ORDER BY testingtime desc) AND sewageid="
						+ getSewageID("+stationName+")
						+ " AND CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
						+ time + "',120) ORDER BY testingtime desc";
				try {
					resultSet = statement.executeQuery(sql2);
					if (resultSet != null) {
						while (resultSet.next()) {
							flag1=2;
							total_items2++;
							Alert temp = new Alert();
							temp.setAlertTime(resultSet.getString(1));
							temp.setSewage_name(resultSet.getString(2));
							temp.setAlertInfo(resultSet.getInt(3));
							temp.setState(resultSet.getInt(4));
							temp.setAdmin(resultSet.getString(5));
							list.add(temp);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(flag1==1){
			a=k-total_items2;}
			if(flag1==2){
			b=items-total_items2;}
			if(a==items){
				a=0;
			}
			if(b==items)
			{
				b=0;
			}
       if(flag1==1){
			if(total_items2<k ){ //第一页没有满
				flagb=pagingnum;
				//(k-total_items2)
			sql3="SELECT TOP "+a+" sewageID,short_title FROM sewage WHERE sewageID IN((SELECT DISTINCT sewageID FROM sewage)" +
					" EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120)) )";
			try {
				resultSet = statement.executeQuery(sql3);
				if (resultSet != null) {
					while (resultSet.next()) {
						total_items3++;
						Alert temp = new Alert();
						temp.setAlertTime(time);
						temp.setSewage_name(resultSet.getString(1));
						temp.setAlertInfo(110);
						temp.setAdmin(getAdmin(resultSet.getString(1)));
						list.add(temp);
					}}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}}else if(flag1==2){
		if(total_items2<items ){ 
			flagb=pagingnum;
			//(k-total_items2)
		sql3="SELECT TOP "+b+" sewageID,short_title FROM sewage WHERE sewageID IN((SELECT DISTINCT sewageID FROM sewage)" +
				" EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120)) )";
		try {
			resultSet = statement.executeQuery(sql3);
			if (resultSet != null) {
				while (resultSet.next()) {
					total_items3++;
					Alert temp = new Alert();
					temp.setAlertTime(time);
					temp.setSewage_name(resultSet.getString(1));
					temp.setAlertInfo(110);
					temp.setAdmin(getAdmin(resultSet.getString(1)));
					list.add(temp);
				}}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}}
		int c=getMax(a,b);
		
		if(total_items1<items)
     {if (total_items2==0){
    	sql3="SELECT TOP "+items+" A.short_title FROM (SELECT sewageID,short_title FROM sewage WHERE sewageID IN((SELECT DISTINCT sewageID FROM sewage) " +
    			"EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120)) )) AS A WHERE sewageID not in (SELECT TOP ((("+ pagingnum+"-1-"+flagb+")*"+ items+ ")+"+c+") sewageID  FROM (SELECT  sewageID,short_title FROM sewage WHERE sewageID IN((SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120)) )) AS B )"; 
    	try {
			resultSet = statement.executeQuery(sql3);
			if (resultSet != null) {
				while (resultSet.next()) {
					total_items3++;
					Alert temp = new Alert();
					temp.setAlertTime(time);
					temp.setSewage_name(resultSet.getString(1));
					temp.setAlertInfo(110);
					temp.setAdmin(getAdmin(resultSet.getString(1)));
					list.add(temp);
				}}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
     }}
     return list;  
	}

       
   private int getMax(int a ,int b){    
	   if(a>=b)
		   return a;
	   else
		   return b;
   }
       
       
       
	private int getSewageID(String name) {
		String sql = "SELECT sewageID FROM sewage WHERE short_title='" + name
				+ "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int sewage = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					sewage = resultSet.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return sewage;
	}

	private String getAbnormalID(String time) {
		String sql = "((SELECT DISTINCT sewageID FROM sewage) EXCEPT (SELECT DISTINCT sewageID  FROM run_data WHERE  CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
				+ time + "',120))) as A ORDER BY A.sewageID ASC";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		String admin = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					admin = resultSet.getString(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return admin;
	}

	private String getAdmin(String name) {
		String sql = "SELECT name from admin WHERE id IN (SELECT adminid FROM admin_area WHERE areaid IN(SELECT areaID FROM sewage WHERE short_title='"
				+ name + "') ) ";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		String admin = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					admin = resultSet.getString(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return admin;
	}

	private PadingUtil getCount(String stationName, Integer pagingnum,
			Integer items, String time) {
		String sql = "SELECT COUNT(*) itemscount FROM detection_data WHERE CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"
				+ time
				+ "',120) AND sewageID = (SELECT sewageID FROM sewage WHERE short_title='"
				+ stationName + "')";
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
					padingUtil.setItems(items);
					padingUtil.setPagingnum(pagingnum);
					padingUtil.setPagecount((int) (Math.ceil((resultSet
							.getInt(1)) / items) + 1));
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

	private ArrayList<Alert> getLastestAlertList(String username) {
		ArrayList<Alert> list = new ArrayList<Alert>();
		String sql = "select top 120 x.*,y.short_title from dbo.alert x,dbo.sewage y,dbo.Administrator z "
				+ "where z.name = '"
				+ username
				+ "' and x.adminId = z.administratorId and x.sewageId = y.sewageId "
				+ "order by x.alerttime desc ";
		// System.out.println("---->"+sql);
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					Alert temp = new Alert();
					/*
					 * temp.setAlertID(resultSet.getInt(1));
					 * temp.setAdminID(resultSet.getInt(2));
					 * temp.setSewageID(resultSet.getInt(3));
					 * temp.setAlertInfo(resultSet.getString(4));
					 * temp.setAlertTime(resultSet.getString(5));
					 * temp.setState(resultSet.getInt(6));
					 * temp.setShortTitle(resultSet.getString(7));
					 */

					list.add(temp);
				}
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}

	/* realtime info alertInfo station */
	private ArrayList<Alert> getRealTimeAlertInfo() {
		ArrayList<Alert> list = new ArrayList<Alert>();
		String sql = "select  distinct y.short_title,x.alertInfo from dbo.alert x,dbo.sewage y "
				+ "where datediff(second,x.alerttime,getdate()) < 6 and x.state = 0 and x.sewageID = y.sewageID";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		System.out.println("RealTime Alert -->" + getNowDate());
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					Alert temp = new Alert();
					/*
					 * temp.setShortTitle(resultSet.getString(1));
					 * temp.setAlertInfo(resultSet.getString(2));
					 */

					list.add(temp);
				}
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}

	private String getNowDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(new java.util.Date());
	}
}
