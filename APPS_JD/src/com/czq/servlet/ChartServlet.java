package com.czq.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.czq.entitiy.Detection1;
import com.czq.util.DBHelp;

/**
 * @author apple draw some charts
 */
public class ChartServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("stationName");
		// sometimes the CharacterSet is not "UTF-8"
		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			station = new String(station.getBytes("iso-8859-1"), "utf-8");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray = JSONArray.fromObject(getDetectionList(station));
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray);
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}

	private ArrayList<Detection1> getDetectionList(String stationName) {
		ArrayList<Detection1> list = new ArrayList<Detection1>();
		ArrayList<Detection1> list1 = new ArrayList<Detection1>();
		ArrayList<Detection1> list2 = new ArrayList<Detection1>();
		Integer aaa = null;
		int flagg=0;
		int flag1=0;
		int a=0;
		int sewageID = 0;
		String time = null;
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			String sql_a = "select TOP 1  CONVERT(varchar(19),testingtime,120),cast(detection1 as decimal(18,2)),cast(detection2 as decimal(18,2)),cast(detection3 as decimal(18,2)) , " +
					"cast(detection5 as decimal(18,2)),1 as flag from dbo.detection_data   WHERE sewageID =(select sewageID FROM sewage WHERE sewage.short_title='"+stationName+"') " +
					"order by testingtime  desc";
			statement = connection.createStatement();
				resultSet = statement.executeQuery(sql_a);
				if (resultSet != null) {//先加入上面那张表，最新的查询数据
					Detection1 newestDet = new Detection1();
					while (resultSet.next()) {
						newestDet.setTestingtime(resultSet.getString(1));
						time=resultSet.getString(1);
						newestDet.setDetection1(resultSet.getFloat(2));
						newestDet.setDetection2(resultSet.getFloat(3));
						newestDet.setDetection3(resultSet.getFloat(4));
						newestDet.setDetection5(resultSet.getFloat(5));
						newestDet.setFlag(resultSet.getInt(6));
						list.add(newestDet);
					}
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date d1 = df.parse(time);//解析成df指定的时间格式
					long diff = d1.getTime()-1000 * 60 * 60 * 24;//获取前一天的时间毫秒数
					java.sql.Date date_sql = new java.sql.Date(diff);
					String sql_c = " select AVG(DATEPART(hh,a.testingtime)),cast(avg(a.detection1) as decimal(18,2)) detection1,cast(avg(a.detection2) as decimal(18,2)) detection2,cast(avg(a.detection3) as decimal(18,2)) detection3,cast(avg(a.detection5) as decimal(18,2)) detection5,"
							+ "1 AS flag from  detection_data a,sewage b   where "
							+ "b.short_title =  '"
							+ stationName
							+ "'  AND b.sewageID = a.sewageID AND (CONVERT(varchar(10),a.testingtime,120) = CONVERT(varchar(10),'"+time+"',120)) "//GETDATE()
							+ "group by CONVERT(varchar(13),a.testingtime,120)";
					resultSet = statement.executeQuery(sql_c);//查询最新一天的数据，同时记录总条数
					if (resultSet != null) {

						while (resultSet.next()) {
							Detection1 det = new Detection1();
							det.setTestingtime(resultSet.getString(1));
							det.setDetection1(resultSet.getFloat(2));
							det.setDetection2(resultSet.getFloat(3));
							det.setDetection3(resultSet.getFloat(4));
							det.setDetection5(resultSet.getFloat(5));
							det.setFlag(resultSet.getInt(6));
							list1.add(det);
							a++;
						}
					}
					//cast(detection1 as decimal(18,2))
					String sql_d="select A.* FROM (select top "+(24-a)+" AVG(DATEPART(hh,a.testingtime)) testingtime,cast(avg(a.detection1) as decimal(18,2)) detection1,cast(avg(a.detection2) as decimal(18,2)) detection2,cast(avg(a.detection3) as decimal(18,2)) detection3,cast(avg(a.detection5) as decimal(18,2)) detection5,0 AS flag from  detection_data a,sewage b   where b.short_title = '"+stationName+"'  AND b.sewageID = a.sewageID AND (CONVERT(varchar(10),a.testingtime,120) = CONVERT(varchar(10),'"+date_sql+"',120)) group by CONVERT(varchar(13),a.testingtime,120) order by AVG(DATEPART(hh,a.testingtime)) desc ) AS A  order by A.testingtime ASC";
					resultSet = statement.executeQuery(sql_d);//查询之前一天的数据，加上当天的总共补成24个点
					if (resultSet != null) {
						while (resultSet.next()) {
							Detection1 det = new Detection1();
							det.setTestingtime(resultSet.getString(1));
							det.setDetection1(resultSet.getFloat(2));
							det.setDetection2(resultSet.getFloat(3));
							det.setDetection3(resultSet.getFloat(4));
							det.setDetection5(resultSet.getFloat(5));
							det.setFlag(resultSet.getInt(6));
							list2.add(det);
						}

					}
				
			         String aa =list1.get(list1.size()-1).getTestingtime();  //获取大的小时
			            if(aa.equals(null)){
			            	System.out.println("今天没有数据");
			            }else{
			            aaa=Integer.parseInt(aa);
			            }
				
				
					for(int j=aaa+1;j<=23;j++)
					{
						for(int i = 0; i < list2.size(); i++)  
				        {  
				            if((list2.get(i).getFlag().equals(0))&&(Integer.parseInt(list2.get(i).getTestingtime())==j))
				            {
				            	 list.add(list2.get(i));
				            	 flag1=1;
				            	  break;
				            }
				            flag1=2;
				        } 
				           
				            if(flag1==2)
				            {
				            	Detection1 init = new Detection1();
				            	init.setTestingtime(String.valueOf(j));
				            	init.setDetection1(0f);
				            	init.setDetection2(0f);
				            	init.setDetection3(0f);
				            	init.setDetection5(0f);
				            	init.setFlag(0);
				            	list.add(init);
							}
				        
					
					}
					for(int j=0;j<=aaa;j++)
					{
						for(int i = 0; i < list1.size(); i++)  
				        {  
				            if(((list1.get(i).getFlag().equals(1)))&&(Integer.parseInt(list1.get(i).getTestingtime())==j)){
				            	 list.add(list1.get(i));
				            	 flagg=1;
				            break;
				            
				            }
				            flagg=2;
				        } 
						if(flagg==2){
			            	Detection1 init = new Detection1();
			            	init.setTestingtime(String.valueOf(j));
			            	init.setFlag(1);
			            	init.setDetection1(0f);
			            	init.setDetection2(0f);
			            	init.setDetection3(0f);
			            	init.setDetection5(0f);
			            	list.add(init);
						}
			            
					}
				} else {
					return null;
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

}
