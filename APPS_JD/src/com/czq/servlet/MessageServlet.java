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

import com.czq.entitiy.Message;
import com.czq.entitiy.PadingUtil;
import com.czq.util.DBHelp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author apple
 * get the names of counties (of the city)
 */
public class MessageServlet extends HttpServlet{
	int item_q=0;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String telephone = req.getParameter("telephone");
		String pagingnum = req.getParameter("pagingnum");
		String items = req.getParameter("items");
		String time = req.getParameter("time");
		Integer pagingnum1 = Integer.parseInt(pagingnum);
		Integer items1 = Integer.parseInt(items);
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray1 = JSONArray.fromObject(getMessageList(pagingnum1, items1,time,telephone));
		JSONArray jsonArray2 = JSONArray.fromObject(getCount(pagingnum1, items1,time,telephone));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result1", jsonArray1);
		jsonValue.element("result2", jsonArray2);
		printWriter.print(jsonValue);
		item_q=0;
		printWriter.flush();
		printWriter.close();
		
	}
	
	private ArrayList<Message> getMessageList(int pagingnum, int items,String time,String telephone)
	{
		ArrayList<Message> list=new ArrayList<Message>();
		String sql="SELECT TOP "+items+" tel,sendtime,abnormaltype,messagedetail FROM message WHERE messageID NOT IN (SELECT TOP (("+items+")*("+ pagingnum+"-1)) messageID FROM message WHERE  CONVERT(varchar(10),sendtime,120) = CONVERT(varchar(10),'"+time+"',120) AND  tel='"+telephone+"' ORDER BY sendtime DESC) AND CONVERT(varchar(10),sendtime,120) = CONVERT(varchar(10),'"+time+"',120) AND tel='"+telephone+"' ORDER BY sendtime DESC";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while(resultSet.next())
			{Message message = new Message();
			item_q++;
			message.setTel(resultSet.getString(1));
			message.setSendtime(resultSet.getString(2));
			message.setAbnormaltype(resultSet.getInt(3));
			message.setMessagedetail(resultSet.getString(4));
			list.add(message);	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	
	private PadingUtil getCount(Integer pagingnum, Integer items, String time,String telephone ){
		String sql = "SELECT COUNT(*) itemscount FROM message WHERE CONVERT(varchar(10),sendtime,120) = CONVERT(varchar(10),'"+time+"',120)  AND tel='"+telephone+"' ";
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
