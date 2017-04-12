package servlet;

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

import util.DBHelp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import beans.Detection1;


/**
 * @author apple draw some charts
 */
public class ChartServlet extends HttpServlet {
	float detection1=0f;
	float detection2=0f;
	float detection3=0f;
	float detection5=0f;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String station = req.getParameter("stationName");
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
	
	
	
	private float getDayWater(String stationName){
		
		int sewageid = 0;
		String sql="select  cast(statistic_day.water as decimal(18,2)) from statistic_day,sewage   where CONVERT(varchar(10),statistic_day.testingtime,120) = CONVERT(varchar(10),GETDATE(),120) and sewage.sewageID=statistic_day.sewageid AND sewage.short_title='"+stationName+"' ";
		PadingUtil padingUtil = new PadingUtil();
		Connection connection = DBHelp.getConnection();
		float daywater = 0f;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					daywater=resultSet.getFloat(1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return daywater;
		
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
		float daywater = getDayWater(stationName);
		String time = null;
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			String sql_a = "SELECT reduceCOD,reduceNH3N,reduceP FROM sewage WHERE short_title =  '"+ stationName+"' ";
			statement = connection.createStatement();
				resultSet = statement.executeQuery(sql_a);
				if (resultSet != null) {//先加入上面那张表，最新的查询数据
					Detection1 newestDet = new Detection1();
					while (resultSet.next()) {
						newestDet.setDetection1(daywater);
						newestDet.setDetection2((float) (Math.round((daywater*(resultSet.getFloat(1)))*100))/100);
						newestDet.setDetection3((float) (Math.round((daywater*(resultSet.getFloat(2)))*100))/100);
						newestDet.setDetection5((float) (Math.round((daywater*(resultSet.getFloat(3)))*100))/100);
						list.add(newestDet);
					}
					String sql = "select TOP 1  CONVERT(varchar(19),testingtime,120),cast(detection1 as decimal(18,2)),cast(detection2 as decimal(18,2)),cast(detection3 as decimal(18,2)) , " +
							"cast(detection5 as decimal(18,2)),1 as flag from dbo.detection_data   WHERE sewageID =(select sewageID FROM sewage WHERE sewage.short_title='"+stationName+"') " +
							"order by testingtime  desc";
						resultSet = statement.executeQuery(sql);
						if (resultSet != null) //先加入上面那张表，最新的查询数据
							while (resultSet.next()) {
								time=resultSet.getString(1);
							}
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date d1 = df.parse(time);//解析成df指定的时间格式
							long diff = d1.getTime()-1000 * 60 * 60 * 24;//获取前一天的时间毫秒数
							java.sql.Date date_sql = new java.sql.Date(diff);			
					String sql_c = "  select AVG(DATEPART(hh,a.testingtime)),cast(avg(a.detection1) as decimal(18,2)),cast(avg(a.detection2) as decimal(18,2)),cast(avg(a.detection3) as decimal(18,2)),cast(avg(a.detection5) as decimal(18,2)),"
							+ "1 AS flag from  detection_data a,sewage b   where "
							+ "b.short_title =  '"
							+ stationName
							+ "'  AND b.sewageID = a.sewageID AND (CONVERT(varchar(10),a.testingtime,120) = CONVERT(varchar(10),'"+time+"',120)) "//GETDATE()
							+ "group by CONVERT(varchar(13),a.testingtime,120)";
					resultSet = statement.executeQuery(sql_c);
					if (resultSet != null) {

						while (resultSet.next()) {
							Detection1 det = new Detection1();
							det.setTestingtime(resultSet.getString(1));
							det.setDetection1(resultSet.getFloat(2));
							det.setDetection2(resultSet.getFloat(3));
							det.setDetection3(resultSet.getFloat(4));
							det.setDetection5(resultSet.getFloat(5));
							det.setFlag(resultSet.getInt(6));
							detection1=resultSet.getFloat(2);
				            detection2=resultSet.getFloat(3);
				            detection3=resultSet.getFloat(4);
				            detection5=resultSet.getFloat(5);
							list1.add(det);
							a++;
						}
					}
					String sql_d="select A.* FROM (select top "+(24-a)+" AVG(DATEPART(hh,a.testingtime)) testingtime,cast(avg(a.detection1) as decimal(18,2)) detection1," +
							"cast(avg(a.detection2) as decimal(18,2)) detection2,cast(avg(a.detection3) as decimal(18,2)) detection3,cast(avg(a.detection5) as decimal(18,2)) detection5,0 AS flag from  detection_data a,sewage b   where b.short_title = '"+stationName+"'  AND b.sewageID = a.sewageID AND (CONVERT(varchar(10),a.testingtime,120) = CONVERT(varchar(10),'"+date_sql+"',120)) group by CONVERT(varchar(13),a.testingtime,120) order by AVG(DATEPART(hh,a.testingtime)) desc ) AS A  order by A.testingtime ASC";
					resultSet = statement.executeQuery(sql_d);
					if (resultSet != null) {
						while (resultSet.next()) {
							Detection1 det = new Detection1();
							det.setTestingtime(resultSet.getString(1));
							det.setDetection1(resultSet.getFloat(2));
							det.setDetection2(resultSet.getFloat(3));
							det.setDetection3(resultSet.getFloat(4));
							det.setDetection5(resultSet.getFloat(5));
							det.setFlag(resultSet.getInt(6));
							detection1=resultSet.getFloat(2);
				            detection2=resultSet.getFloat(3);
				            detection3=resultSet.getFloat(4);
				            detection5=resultSet.getFloat(5);
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
				            	init.setDetection1((float)Math.round((detection1+Math.random())*100)/100);
				            	init.setDetection2((float)Math.round((detection2+Math.random())*100)/100);
				            	init.setDetection3((float)Math.round((detection3+100*Math.random())*100)/100);
				            	init.setDetection5((float)Math.round((detection5+Math.random())*100)/100);
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
			            	init.setDetection1((float)Math.round((detection1+Math.random())*100)/100);
			            	init.setDetection2((float)Math.round((detection2+Math.random())*100)/100);
			            	init.setDetection3((float)Math.round((detection3+100*Math.random())*100)/100);
			            	init.setDetection5((float)Math.round((detection5+Math.random())*100)/100);
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
