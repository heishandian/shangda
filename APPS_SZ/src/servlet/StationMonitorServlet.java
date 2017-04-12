package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DBHelp;

//import oracle.sql.DATE;

import beans.Sewage;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author apple
 * get information of warnings and values of sensors 
 */
public class StationMonitorServlet extends HttpServlet{

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req,resp);
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String station = req.getParameter("stationName");
		//station = "������";
		//sometimes the CharacterSet is not "UTF-8"
		if(!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())){
			station = new String(station.getBytes("iso-8859-1"),"utf-8");
		}
		System.out.println("station-->:"+station);
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		
		ArrayList<Sewage> list = getStationMonitorList(station);
		Sewage tmp = list.get(0);
		String state = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date beginDate = null;
		java.util.Date endDate = null;
		
		if(tmp.getConfirmGratingTime()!=null){
			try {
				beginDate = format.parse(this.getNowDate());
				endDate =	format.parse(tmp.getConfirmGratingTime());
				tmp.setConfirmGratingTime(format.format(endDate));
				long days=(beginDate.getTime()-endDate.getTime())/(24*60*60*1000);
				if(days>tmp.getGratingDays())
				{
					state = "�����������趨��դʱ��"+String.valueOf(days-tmp.getGratingDays())+"��";
//					System.out.println("-->"+state);
				}else{//different from selectdata.jsp in CWS
					state = "��";
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("java.util.Date and format :"+endDate+"\n");
			//System.out.print("java.sql.Date without format :"+java.sql.Date.valueOf(tmp.getConfirmGratingTime()));
		}else{
			state="δ��ʼ����դʱ��";
			//tmp.setConfirmGratingTime("1970-01-01 00:00:00");
		}
		JSONArray jsonArray = JSONArray.fromObject(list);
		JSONObject jsonValue = new JSONObject();
		//JSONObject jsontemp = new JSONObject();
		//jsontemp.element("gratingstate",JSONObject.fromObject(state));
		//jsonArray.add(jsontemp);
		//jsontemp.put("gratingstate", state);
		//jsonArray.add(jsonArray.size(),jsontemp);
		
		jsonValue.element("result", jsonArray);
		
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
	}
	
	private String getNowDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(new java.util.Date());
	}

	private ArrayList<Sewage> getStationMonitorList(String station){
		ArrayList<Sewage> list = new ArrayList<Sewage>();
		String sql = "select * from dbo.sewage where short_title = '" +station+"'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while(resultSet.next())
			{
				Sewage sewage = new Sewage();
				sewage.setEquipment1state(resultSet.getString(10));
				sewage.setEquipment2state(resultSet.getString(11));
				sewage.setEquipment3state(resultSet.getString(12));
				sewage.setEquipment4state(resultSet.getString(13));
				sewage.setEquipment5state(resultSet.getString(14));
				sewage.setDetection1(resultSet.getFloat(15));
				sewage.setDetection1ul(resultSet.getFloat(16));
				sewage.setDetection1dl(resultSet.getFloat(17));
				sewage.setDetection2(resultSet.getFloat(18));
				sewage.setDetection2ul(resultSet.getFloat(19));
				sewage.setDetection2dl(resultSet.getFloat(20));
				sewage.setDetection3(resultSet.getFloat(22));
				sewage.setDetection3ul(resultSet.getFloat(21));//warning!!!
				sewage.setDetection3dl(resultSet.getFloat(23));
				sewage.setDetection4(resultSet.getFloat(24));
				sewage.setDetection4ul(resultSet.getFloat(25));
				sewage.setDetection4dl(resultSet.getFloat(26));
				sewage.setDetection5(resultSet.getFloat(27));
				sewage.setDetection5ul(resultSet.getFloat(28));
				sewage.setDetection5dl(resultSet.getFloat(29));
				sewage.setDetection6(resultSet.getFloat(30));
				sewage.setDetection6ul(resultSet.getFloat(31));
				sewage.setDetection6dl(resultSet.getFloat(32));
				sewage.setControl_strategy(resultSet.getString(38));
				sewage.setDevice_alert(resultSet.getString(39));
				sewage.setGratingDays(resultSet.getInt(40));
				sewage.setConfirmGratingTime(resultSet.getString(41));
				sewage.setWaterflow(resultSet.getFloat(43));
				sewage.setReduceCOD(resultSet.getFloat(44));
				sewage.setReduceNH3N(resultSet.getFloat(45));
				sewage.setReduceP(resultSet.getFloat(46));
				
				list.add(sewage);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
			
		}
		return list;
	}
}
