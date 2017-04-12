package com.czq.servlet;

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

import com.czq.entitiy.Detection;
import com.czq.entitiy.PadingUtil;
import com.czq.util.DBHelp;
import com.czq.util.Sewage;

public class PadingUtilServlet extends HttpServlet {
	int item_q = 0;
	int sewageid = 0;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String station = req.getParameter("stationName");
		String pagingnum = req.getParameter("pagingnum");
		String items = req.getParameter("items");
		String time = req.getParameter("time");
		/*String start_time = req.getParameter("start_time");
		String end_time = req.getParameter("end_time");
*/
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			station = new String(station.getBytes("iso-8859-1"), "utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = JSONArray.fromObject(getResultStatisticList(
				station, pagingnum1, items1, time));
		JSONArray jsonArray2 = JSONArray.fromObject(getCount(station,
				pagingnum1, items1, time));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result1", jsonArray1);
		jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		item_q = 0;
		printWriter.flush();
		printWriter.close();

	}
	//查询指定天的水质监测数据
	private ArrayList<Detection> getResultStatisticList(String short_title,Integer pagingnum, Integer item, String time) {
		// 1.获取站点ID
		sewageid = new Sewage().getSewageidByShort_title(short_title);//getSewageidByShort_title(short_title);
		if (pagingnum <= 0) {
			pagingnum = 1;
		}
		String sql = null;
		ArrayList<Detection> list = new ArrayList<Detection>();
		//2. 查询水质监测记录的SQL语句
		/*if(!"".equals(short_title) && "".equals(start_time) && "".equals(end_time) ){
			sql = "SELECT CONVERT(varchar(19),testingtime,120) as time,detection1,detection2,detection3,detection5 "+
					"FROM (SELECT ROW_NUMBER() over(order by testingtime desc) as rows,* FROM detection_data "+
					"WHERE  sewageID = "+sewageid+") as temp "+
					"WHERE rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
		} else if(!"".equals(short_title) && !"".equals(start_time) && !"".equals(end_time)){

			sql = "SELECT CONVERT(varchar(19),testingtime,120) as time,detection1,detection2,detection3,detection5 "+
			"FROM (SELECT ROW_NUMBER() over(order by testingtime desc) as rows,* FROM detection_data "+
			"WHERE CONVERT(varchar(10),testingtime,120) >= CONVERT(varchar(10),'"+start_time+"',120) "+
			"CONVERT(varchar(10),testingtime,120) <= CONVERT(varchar(10),'"+end_time+"',120)AND sewageID = "+sewageid+") as temp "+
			"WHERE rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
			
		}*/
		if(!"".equals(short_title) && "".equals(time) ){
			sql = "SELECT CONVERT(varchar(19),testingtime,120) as time,detection1,detection2,detection3,detection5 "+
				   "FROM (SELECT ROW_NUMBER() over(order by testingtime desc) as rows,* FROM detection_data "+
				   "WHERE  sewageID = "+sewageid+") as temp "+
			       "WHERE rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
		} else if(!"".equals(short_title) && !"".equals(time)){

			sql = "SELECT CONVERT(varchar(19),testingtime,120) as time,detection1,detection2,detection3,detection5 "+
				  "FROM (SELECT ROW_NUMBER() over(order by testingtime desc) as rows,* FROM detection_data "+
				  "WHERE CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120) "+
				  "AND sewageID = "+sewageid+") as temp "+
				  "WHERE rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
		
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
					det.setDetection5(resultSet.getFloat(5));
					list.add(det);
				}

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

	private PadingUtil getCount(String short_title, Integer pagingnum, Integer items,String time) {
		String sql = null;
		/*if(!"".equals(short_title) && "".equals(start_time) && "".equals(end_time) ){
			sql = "SELECT COUNT(*) itemscount FROM detection_data WHERE sewageID = "+sewageid+"";
		} else if(!"".equals(short_title) && !"".equals(start_time) && !"".equals(end_time)){
			sql = "SELECT COUNT(*) itemscount FROM detection_data WHERE CONVERT(varchar(10),testingtime,120) >= CONVERT(varchar(10),"+start_time+",120) "+
					 "AND CONVERT(varchar(10),testingtime,120) <= CONVERT(varchar(10),"+end_time+",120) AND sewageID = "+sewageid+"";	
		}*/
		if(!"".equals(short_title) && "".equals(time)){
			sql = "SELECT COUNT(*) itemscount FROM detection_data WHERE sewageID = "+sewageid+"";
		} else if(!"".equals(short_title) && !"".equals(time) ){
			sql = "SELECT COUNT(*) itemscount FROM detection_data WHERE CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120) "+
					 "AND sewageID = "+sewageid+"";	
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
					padingUtil.setItemscount(resultSet.getInt(1));
					padingUtil.setItems(item_q);
					padingUtil.setPagingnum(pagingnum);
					padingUtil.setPagecount((int) (Math.ceil(((double) (resultSet.getInt(1)) / items))));
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
