package com.czq.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.czq.entitiy.Sewage;
import com.czq.util.DBHelp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MapTestServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/json");
		resp.setCharacterEncoding("utf-8");
		
		PrintWriter printWriter = resp.getWriter();
		JSONArray jsonArray = JSONArray.fromObject(getAllSewageInfo());
		JSONObject jsonValue = new JSONObject();
		jsonValue.element("result", jsonArray);
		
		printWriter.print(jsonValue);
		printWriter.flush();
		printWriter.close();
		
	}
	
	private ArrayList<Sewage> getAllSewageInfo(){
		ArrayList<Sewage> list = new ArrayList<Sewage>();
		String sql = "select * from dbo.sewage";
		
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet != null){
				while(resultSet.next()){
                    Sewage temp = new Sewage();
					
					temp.setSewageId(resultSet.getInt(1));
					temp.setAreaId(resultSet.getInt(2));
					temp.setControlId(resultSet.getInt(3));
					temp.setAdministratorId(resultSet.getInt(4));
					temp.setShortTitle(resultSet.getString(5));
					temp.setName(resultSet.getString(6));
					temp.setAddress(resultSet.getString(7));
					temp.setCoordinateX(resultSet.getFloat(8));
					temp.setCoordinateY(resultSet.getFloat(9));
					
					temp.setEquipment1state(resultSet.getString(10));
					temp.setEquipment2state(resultSet.getString(11));
					temp.setEquipment3state(resultSet.getString(12));
					temp.setEquipment4state(resultSet.getString(13));
					temp.setEquipment5state(resultSet.getString(14));
					
					temp.setDetection1(resultSet.getFloat(15));
					temp.setDetection2(resultSet.getFloat(18));
					temp.setDetection3(resultSet.getFloat(22));
					temp.setDetection4(resultSet.getFloat(24));
					temp.setDetection5(resultSet.getFloat(27));
					temp.setDetection6(resultSet.getFloat(30));
					
					temp.setDetection1dl(resultSet.getFloat(17));
					temp.setDetection1ul(resultSet.getFloat(16));
					temp.setDetection2dl(resultSet.getFloat(20));
					temp.setDetection2ul(resultSet.getFloat(19));
					temp.setDetection3dl(resultSet.getFloat(23));
					temp.setDetection3ul(resultSet.getFloat(21));
					temp.setDetection4dl(resultSet.getFloat(26));
					temp.setDetection4ul(resultSet.getFloat(25));
					temp.setDetection5dl(resultSet.getFloat(29));
					temp.setDetection5ul(resultSet.getFloat(28));
					temp.setDetection6dl(resultSet.getFloat(32));
					temp.setDetection6ul(resultSet.getFloat(31));
					
					temp.setGratingDays(resultSet.getInt(40));
					
					temp.setWaterflow(resultSet.getFloat(43));
					temp.setReduceCOD(resultSet.getFloat(44));
					temp.setReduceNH3N(resultSet.getFloat(45));
					temp.setReduceP(resultSet.getFloat(46));
//					temp.setAdministratorName(resultSet.getString(47));
//					temp.setCountyName(resultSet.getString(48));
					
//					System.out.println("123-->" + temp.toString());

					list.add(temp);
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return list;
		
	}
	
}
