package com.czq.servlet;

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

import com.czq.entitiy.EquipmentState;
import com.czq.entitiy.PadingUtil;
import com.czq.util.DBHelp;
import com.czq.util.Sewage;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class EquipmentStateServlet extends HttpServlet{
	int item_q = 0;
	int sewageid = 0;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String station = req.getParameter("stationName");
		String pagingnum = req.getParameter("pagingnum");
		String items = req.getParameter("items");
		String time = req.getParameter("time");
		/*String start_time = req.getParameter("start_time");
		String end_time = req.getParameter("end_time");*/
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			station = new String(station.getBytes("iso-8859-1"),"utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = JSONArray.fromObject(getEquipmentStateList(station,pagingnum1,items1,time));
		JSONArray jsonArray2 = JSONArray.fromObject(getCount(station,pagingnum1,items1,time));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result1", jsonArray1);
		jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		item_q = 0;
		printWriter.flush();
		printWriter.close();
		
	}
	
	private ArrayList<EquipmentState> getEquipmentStateList(String short_title,Integer pagingnum, Integer item,String time){
		ArrayList<EquipmentState> list = new ArrayList<EquipmentState>();
		sewageid = new Sewage().getSewageidByShort_title(short_title);
		String sql = null;
		/*if(!"".equals(short_title) && "".equals(start_time) && "".equals(end_time) ){
			sql = "SELECT CONVERT(varchar(19),testingtime,120) as time,equipment6state,equipment7state,equipment8state,"+
					"equipment9state,equipment10state,equipment11state,equipment12state,equipment13state," +
					"equipment14state,equipment15state,equipment16state,equipment17state,equipment18state," +
					"equipment19state,equipment20state,equipment21state,equipment22state " +
					"FROM (SELECT ROW_NUMBER() over(order by testingtime desc) as rows,* FROM run_data "+
					"WHERE sewageID = "+sewageid+") as temp "+
					"WHERE rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
		} else if(!"".equals(short_title) && !"".equals(start_time) && !"".equals(end_time)){
			sql = "SELECT CONVERT(varchar(19),testingtime,120) as time,equipment6state,equipment7state,equipment8state,"+
					"equipment9state,equipment10state,equipment11state,equipment12state,equipment13state," +
					"equipment14state,equipment15state,equipment16state,equipment17state,equipment18state," +
					"equipment19state,equipment20state,equipment21state,equipment22state " +
					"FROM (SELECT ROW_NUMBER() over(order by testingtime desc) as rows,* FROM run_data "+
					"WHERE CONVERT(varchar(10),testingtime,120) >= CONVERT(varchar(10),'"+start_time+"',120) "+
					"AND CONVERT(varchar(10),testingtime,120) <= CONVERT(varchar(10),'"+end_time+"',120) AND sewageID = "+sewageid+") as temp "+
					"WHERE rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
		}*/
		if(!"".equals(short_title) && "".equals(time)  ){
			sql = "SELECT CONVERT(varchar(19),testingtime,120) as time,equipment6state,equipment7state,equipment8state,"+
					"equipment9state,equipment10state,equipment11state,equipment12state,equipment13state," +
					"equipment14state,equipment15state,equipment16state,equipment17state,equipment18state," +
					"equipment19state,equipment20state,equipment21state,equipment22state " +
					"FROM (SELECT ROW_NUMBER() over(order by testingtime desc) as rows,* FROM run_data "+
					"WHERE sewageID = "+sewageid+") as temp "+
					"WHERE rows>("+item+"*("+pagingnum+"-1)) and rows<=("+item+"*"+pagingnum+")";
		} else if(!"".equals(short_title) && !"".equals(time) ){
			sql = "SELECT CONVERT(varchar(19),testingtime,120) as time,equipment6state,equipment7state,equipment8state,"+
					"equipment9state,equipment10state,equipment11state,equipment12state,equipment13state," +
					"equipment14state,equipment15state,equipment16state,equipment17state,equipment18state," +
					"equipment19state,equipment20state,equipment21state,equipment22state " +
					"FROM (SELECT ROW_NUMBER() over(order by testingtime desc) as rows,* FROM run_data "+
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
			while(resultSet.next())
			{   item_q++;
				EquipmentState equip = new EquipmentState();
				equip.setTestingtime(resultSet.getString(1));
				
				equip.setEquipment6state(resultSet.getShort(2));
				equip.setEquipment7state(resultSet.getShort(3));
				equip.setEquipment8state(resultSet.getShort(4));
				equip.setEquipment9state(resultSet.getShort(5));
				equip.setEquipment10state(resultSet.getShort(6));
				
				equip.setEquipment11state(resultSet.getShort(7));
				equip.setEquipment12state(resultSet.getShort(8));
				equip.setEquipment13state(resultSet.getShort(9));
				equip.setEquipment14state(resultSet.getShort(10));
				equip.setEquipment15state(resultSet.getShort(11));
				
				equip.setEquipment16state(resultSet.getShort(12));
				equip.setEquipment17state(resultSet.getShort(13));
				equip.setEquipment18state(resultSet.getShort(14));
				equip.setEquipment19state(resultSet.getShort(15));
				equip.setEquipment20state(resultSet.getShort(16));
				equip.setEquipment21state(resultSet.getShort(17));
				equip.setEquipment22state(resultSet.getShort(18));
				list.add(equip);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return list;
	}
	
	private PadingUtil getCount(String short_title,Integer pagingnum, Integer items, String time){
		String sql = null;
		/*if(!"".equals(short_title) && "".equals(start_time) && "".equals(end_time) ){
			sql = "SELECT COUNT(*) itemscount FROM detection_data WHERE sewageID = "+sewageid+"";
		} else if(!"".equals(short_title) && !"".equals(start_time) && !"".equals(end_time)){
			sql = "SELECT COUNT(*) itemscount FROM run_data WHERE CONVERT(varchar(10),testingtime,120) >= CONVERT(varchar(10),'"+start_time+"',120) "+
		"AND CONVERT(varchar(10),testingtime,120) <= CONVERT(varchar(10),'"+end_time+"',120) AND sewageID = "+sewageid+"";
		}*/
		if(!"".equals(short_title) && "".equals(time)  ){
			sql = "SELECT COUNT(*) itemscount FROM detection_data WHERE sewageID = "+sewageid+"";
		} else if(!"".equals(short_title) && !"".equals(time) ){
			sql = "SELECT COUNT(*) itemscount FROM run_data WHERE CONVERT(varchar(10),testingtime,120) = CONVERT(varchar(10),'"+time+"',120) "+
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

}
