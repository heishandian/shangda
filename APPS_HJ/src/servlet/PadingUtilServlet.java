package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.DBHelp;
import beans.Detection;
import beans.PadingUtil;


public class PadingUtilServlet extends HttpServlet {
	int sewageid = 0 ;
	int item_q = 0;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("short_title");//注意以前是stationName 现在改成了short_title
		String pagingnum = req.getParameter("pagingnum");
		String items = req.getParameter("items");
		String start_time = req.getParameter("start_time");
		String end_time = req.getParameter("end_time");
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			station = new String(station.getBytes("iso-8859-1"), "utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = JSONArray.fromObject(getResultStatisticList(
				station, pagingnum1, items1,start_time,end_time));
		JSONArray jsonArray2 = JSONArray.fromObject(getCount(
				station, pagingnum1, items1,start_time,end_time));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result1", jsonArray1);
		jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		item_q = 0;
		printWriter.flush();
		printWriter.close();

	}

	private ArrayList<Detection> getResultStatisticList(String shorttile,
			Integer pagingnum, Integer item, String starttime,String endtime) {
		String sql=null;
		sewageid = getSewageID(shorttile);//获取站点的站点id
		if (pagingnum < 0) {
			pagingnum = 1;
		}
		ArrayList<Detection> list = new ArrayList<Detection>();//cast(detection1 as decimal(18,2))
		//获取指定站点的所有数据
		if(!"".equals(shorttile) && "".equals(starttime) && "".equals(endtime)){
			sql = "SELECT TOP  "
					+ item
					+ " CONVERT(varchar(19),a.testingtime,120),cast(a.detection1 as decimal(18,2)),cast(a.detection2 as decimal(18,2)),cast(a.detection3 as decimal(18,2)),cast(a.detection4 as decimal(18,2)),cast(a.detection5 as decimal(18,2))  "+
				    "From detection_data a "
					+ "where a.detectionID  not in ( SELECT TOP (("
					+ item
					+ ")*("
					+ pagingnum
					+ "-1)) a.detectionID "
					+ "FROM detection_data aa  where"
					+ " aa.sewageID="+sewageid+"   ORDER BY aa.testingtime desc ) and a.sewageID="+sewageid+"  ORDER BY a.testingtime desc";
		} else if(!"".equals(shorttile) && !"".equals(starttime) && !"".equals(endtime)){
		 sql = "SELECT TOP  "
				+ item
				+ " CONVERT(varchar(19),a.testingtime,120),cast(a.detection1 as decimal(18,2)),cast(a.detection2 as decimal(18,2)),cast(a.detection3 as decimal(18,2)),cast(a.detection4 as decimal(18,2)),cast(a.detection5 as decimal(18,2))  "+
			    "From detection_data a "
				+ "where a.detectionID  not in ( SELECT TOP (("
				+ item
				+ ")*("
				+ pagingnum
				+ "-1)) aa.detectionID "
				+ "FROM detection_data aa "
				+ " where aa.sewageID="+sewageid+"  AND CONVERT(varchar(10),aa.testingtime,120) >= '"+starttime+"' and CONVERT(varchar(10),aa.testingtime,120) <= '"+endtime+"' ORDER BY aa.testingtime desc ) and a.sewageID="+sewageid+"  " +
					" AND CONVERT(varchar(10),a.testingtime,120) >= '"+starttime+"' and CONVERT(varchar(10),a.testingtime,120) <= '"+endtime+"' ORDER BY a.testingtime desc";
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
					Detection det = new Detection();
					det.setTestingtime(resultSet.getString(1));
					det.setDetection1(resultSet.getFloat(2));
					det.setDetection2(resultSet.getFloat(3));
					det.setDetection3(resultSet.getFloat(4));
					det.setDetection4(resultSet.getInt(5));//液位
					det.setDetection5(resultSet.getFloat(6));
					list.add(det);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return list;
	}
	private int getSewageID(String short_title) {
		String sql = "SELECT sewageID FROM sewage WHERE short_title='" + short_title
				+ "'";
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
	private PadingUtil getCount(String stationName,
			Integer pagingnum, Integer items, String starttime,String endtime){
		String sql = null;
		//查询指定站点的全部数据
		if(!"".equals(stationName) && "".equals(starttime) && "".equals(endtime)){
		sql = "SELECT COUNT(*) itemscount FROM detection_data a where  a.sewageID = "+sewageid+"";}
		else if (!"".equals(stationName) && !"".equals(starttime) && !"".equals(endtime)){//查询指定站点，指定时间段
		sql = "SELECT COUNT(*) itemscount FROM detection_data a WHERE  CONVERT(varchar(10),a.testingtime,120) >= '"+starttime+"' and CONVERT(varchar(10),a.testingtime,120) <= '"+endtime+"'  AND a.sewageID = "+sewageid+"";
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
					padingUtil.setItemscount(a);//总条数
					padingUtil.setItems(item_q);//当前条数
					padingUtil.setPagingnum(pagingnum);//当前页页码
					//System.out.println(10f/3);
					if((a % items)==0){
					padingUtil.setPagecount((int) (Math.ceil(a/items)));
					}else{
					padingUtil.setPagecount((int) (Math.ceil(a/items)+1));}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return padingUtil;
		
	}

}
