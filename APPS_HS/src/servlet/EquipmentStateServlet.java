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

import beans.EquipmentState;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EquipmentStateServlet extends HttpServlet {
	int item_q = 0;
	int item_qq = 0;
	int sewageid = 0;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String start_time = null;
		String end_time = null;
		String station = req.getParameter("short_title");
		String pagingnum = req.getParameter("pagingnum");
		String items = req.getParameter("items");
		start_time = req.getParameter("start_time");
		end_time = req.getParameter("end_time");
		Integer pagingnum1 = Integer.parseInt(pagingnum);//将字符转换成int类型的
		Integer items1 = Integer.parseInt(items);
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			station = new String(station.getBytes("iso-8859-1"),"utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = JSONArray.fromObject(getEquipmentStateList(station,pagingnum1,items1,start_time,end_time));//将List封装成Json数组
		JSONArray jsonArray2 = JSONArray.fromObject(getCount(station,pagingnum1,items1,start_time,end_time));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result1", jsonArray1);//封装Json对象
		jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		 item_q = 0;
		 item_qq = 0;
		printWriter.flush();
		printWriter.close();
	}

	private ArrayList<EquipmentState> getEquipmentStateList(String stationName,
			Integer pagingnum, Integer item,String starttime,String endtime) {
		sewageid = getSewageID(stationName);
		String sql = null;
		if (pagingnum < 0) {
			pagingnum = 1;
		} 	
		ArrayList<EquipmentState> list = new ArrayList<EquipmentState>();
		if(!"".equals(stationName) && ("".equals(starttime) || starttime==null) && ("".equals(endtime) || endtime == null) ){
			sql = "SELECT " +
						"CONVERT(VARCHAR(19),testingtime,120),equipment1state,equipment2state,equipment3state,equipment4state "+
				  "FROM"+ 
				  		"(SELECT ROW_NUMBER() OVER (ORDER BY testingtime desc) AS rows,* FROM run_data where sewageid = "+sewageid+") AS temp "+
				   "WHERE " +
				   		"rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
		}
		else if(!"".equals(stationName) && !"".equals(starttime) && !"".equals(endtime)){
			sql = "SELECT " +
					"CONVERT(VARCHAR(19),testingtime,120),equipment1state,equipment2state,equipment3state,equipment4state "+
			  "FROM "+ 
			  		"(SELECT ROW_NUMBER() OVER (ORDER BY testingtime desc) AS rows,* FROM run_data where sewageid = "+sewageid+" " +
			  		"AND testingtime >= '"+starttime+"'AND testingtime <= '"+endtime+"') AS temp "+
			   "WHERE " +
			   		"rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
		}
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				item_q++;
				EquipmentState equip = new EquipmentState();
				equip.setTestingtime(resultSet.getString(1));
				equip.setEquipment1state(resultSet.getShort(2));
				equip.setEquipment2state(resultSet.getShort(3));
				equip.setEquipment3state(resultSet.getShort(4));
				equip.setEquipment4state(resultSet.getShort(5));
				list.add(equip);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return list;
	}
	
	private PadingUtil getCount(String stationName,
			Integer pagingnum, Integer items, String starttime,String endtime){
		String sql = null;
		if(!"".equals(stationName) && ("".equals(starttime) || starttime==null) && ("".equals(endtime) || endtime == null) ){
		 sql = "SELECT COUNT(*) itemscount FROM run_data WHERE sewageID = "+sewageid+"";
		}else if (!"".equals(stationName) && !"".equals(starttime) && !"".equals(endtime)){
		 sql = "SELECT COUNT(*) itemscount FROM run_data WHERE CONVERT(varchar(10),testingtime,120) >= CONVERT(varchar(10),'"+starttime+"',120) AND CONVERT(varchar(10),testingtime,120) <= CONVERT(varchar(10),'"+endtime+"',120) AND sewageID = "+sewageid+"";
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
	private int getSewageID(String short_title) {
		String sql = "SELECT sewageID FROM sewage WHERE short_title='"+short_title+"'";
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
