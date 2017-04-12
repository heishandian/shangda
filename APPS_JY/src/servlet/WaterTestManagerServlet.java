package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.DBHelp;
import beans.AlertMessage;
import beans.WaterTestManager;

/**
 * @author apple
 * 
 */
public class WaterTestManagerServlet extends HttpServlet {
	int temp = 0;
	Integer pagingnum1=0;;
	Integer items1=0;
	String  operationnum = null;
	String  short_title = null;
	String  county = null;
	String start_time = null;
	String end_time = null;
	int areaid = 0;
	int sewageid =0;
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requestflag = null;
		String pagingnum = null;
		String items = null;
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		requestflag = req.getParameter("requestFlag");// 请求标志
		resp.setContentType("text/json");
		JSONArray jsonArray1 = new JSONArray();
		JSONArray jsonArray2 = new JSONArray();
		switch (requestflag.charAt(0)) {
		case 'R':
			pagingnum = req.getParameter("pagingnum");// 页码
			items = req.getParameter("items");// 一页的条数
			pagingnum1 = Integer.parseInt(pagingnum);
			items1 = Integer.parseInt(items);
			jsonArray1 = JSONArray.fromObject(GetWaterTestManager(req));
			jsonArray2 = JSONArray.fromObject(GetWaterTestManagerCount(req));
			JSONObject jsonValue = new JSONObject();
			jsonValue.element("result1", jsonArray1);
			jsonValue.element("result2", jsonArray2);
			printWriter.print(jsonValue);
			temp = 0;
			break;
		case 'C':// 新增 printWriter.print(createRecord(req)); break; case
			try {
				printWriter.print(creatWaterTestManager(req));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		printWriter.flush();
		printWriter.close();

	}

	private ArrayList<WaterTestManager> GetWaterTestManager(HttpServletRequest req) throws IOException {
		String sql = null;
		if (pagingnum1 < 0) {
			pagingnum1 = 1;
		}
		operationnum = req.getParameter("operationnum");// 运营编号
		ArrayList<WaterTestManager> list = new ArrayList<WaterTestManager>();
		if ("".equals(operationnum)) {//运营编号为空，按照区域、站点、时间段查询
				short_title = req.getParameter("short_title").trim();// 污水站点
				county = req.getParameter("county");// 区域
				if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
					try {
						short_title = new String(
								short_title.getBytes("iso-8859-1"), "utf-8");
						county = new String(county.getBytes("iso-8859-1"), "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				start_time = req.getParameter("start_time");// 传入查询开始时间
				end_time = req.getParameter("end_time");// 传入查询的结束时间
		if ("".equals(county) && "".equals(short_title))//区域为空，站点为空
	   {
			if("".equals(start_time) && "".equals(end_time)){
				sql="SELECT TOP "+items1+" y.short_title,z.name ,y.operationnum,y.controlID,x.incod,x.innh3n,x.inp,x.outcod ,x.outnh3n,x.outp,CONVERT(varchar(19),x.testingtime,120) FROM waterTestManager x,sewage y,area z WHERE  x.id not in (SELECT TOP (("
					+ pagingnum1 + "-1)*" + items1
					+ ") id FROM waterTestManager ORDER BY testingtime DESC) AND  x.sewageid=y.sewageID AND y.areaID=z.id ORDER BY x.testingtime DESC";
			}else if(!"".equals(start_time) && !"".equals(end_time)){
				sql=" SELECT TOP "+items1+"  y.short_title,z.name ,y.operationnum,y.controlID,x.incod,x.innh3n,x.inp,x.outcod ,x.outnh3n,x.outp,CONVERT(varchar(19),x.testingtime,120) FROM waterTestManager x,sewage y,area z WHERE  x.id not in (SELECT TOP (("
					+ pagingnum1 + "-1)*" + items1
					+ ") id FROM waterTestManager WHERE testingtime>='"+start_time+"' AND testingtime<='"+end_time+"' ORDER BY testingtime DESC) AND x.testingtime>='"+start_time+"' AND x.testingtime<='"+end_time+"' AND x.sewageid=y.sewageID AND y.areaID=z.id ORDER BY x.testingtime DESC";
			}else{
				System.out.println("查询所有区域水质化验历史数据条件有误");
			}
	   }
		else if(!"".equals(county) && "".equals(short_title))
				{//查询指定区域
				areaid = getAreaID(county);
				if("".equals(start_time) && "".equals(end_time)){
					sql=" SELECT TOP "+items1+" y.short_title,z.name ,y.operationnum,y.controlID,x.incod,x.innh3n,x.inp,x.outcod ,x.outnh3n,x.outp,CONVERT(varchar(19),x.testingtime,120) FROM waterTestManager x,sewage y,area z WHERE  x.id not in (SELECT TOP (("
						+ pagingnum1 + "-1)*" + items1
						+ ") id FROM waterTestManager WHERE sewageid in (SELECT sewageID FROM sewage WHERE areaID="+areaid+") ORDER BY testingtime DESC) AND  x.sewageid=y.sewageID AND y.areaID=z.id  AND y.areaID="+areaid+" ORDER BY x.testingtime DESC";
				}else if(!"".equals(start_time) && !"".equals(end_time)){
					sql=" SELECT TOP "+items1+"  y.short_title,z.name ,y.operationnum,y.controlID,x.incod,x.innh3n,x.inp,x.outcod ,x.outnh3n,x.outp,CONVERT(varchar(19),x.testingtime,120) FROM waterTestManager x,sewage y,area z WHERE  x.id not in (SELECT TOP (("
						+ pagingnum1 + "-1)*" + items1
						+ ") id FROM waterTestManager WHERE testingtime>='"+start_time+"' AND testingtime<='"+end_time+"' AND  sewageid in (SELECT sewageID FROM sewage WHERE areaID="+areaid+") ORDER BY testingtime DESC) AND   x.testingtime>='"+start_time+"' AND x.testingtime<='"+end_time+"' AND x.sewageid=y.sewageID AND y.areaID=z.id  AND y.areaID="+areaid+"  ORDER BY x.testingtime DESC";
				}else{
						System.out.println("查询所有区域水质化验历史数据条件有无");
					}			 
				}
		else if(!"".equals(county) && !"".equals(short_title))//区域和站点非空
		{//查询指定站点
			sewageid=gersewageid(short_title);
			start_time = req.getParameter("start_time");// 传入查询开始时间
			end_time = req.getParameter("end_time");// 传入查询的结束时间
			if("".equals(start_time) && "".equals(end_time)){
				sql="SELECT TOP "+items1+" y.short_title,z.name ,y.operationnum,y.controlID,x.incod,x.innh3n,x.inp,x.outcod ,x.outnh3n,x.outp,CONVERT(varchar(19),x.testingtime,120) FROM waterTestManager x,sewage y,area z WHERE  x.id not in (SELECT TOP (("
						+ pagingnum1 + "-1)*" + items1
						+ ") id FROM waterTestManager WHERE sewageid ="+sewageid+" ORDER BY testingtime DESC) AND  x.sewageid=y.sewageID AND y.areaID=z.id AND x.sewageid ="+sewageid+" ORDER BY x.testingtime DESC";
					
			}else if(!"".equals(start_time) && !"".equals(end_time)){
				sql=" SELECT TOP "+items1+" '"+short_title+"' as short_title,'"+county+"' as county ,y.operationnum,y.controlID,x.incod,x.innh3n,x.inp,x.outcod ,x.outnh3n,x.outp,CONVERT(varchar(19),x.testingtime,120)  FROM waterTestManager x,sewage y WHERE  x.id not in (SELECT TOP (("
					+ pagingnum1 + "-1)*" + items1
					+ ") id FROM waterTestManager WHERE testingtime>='"+start_time+"' AND testingtime<='"+end_time+"' AND  sewageid ="+sewageid+" ORDER BY testingtime DESC) AND   x.testingtime>='"+start_time+"' AND x.testingtime<='"+end_time+"' AND x.sewageid=y.sewageID AND y.sewageID = "+sewageid+"  ORDER BY x.testingtime DESC";
			
			}else{
				System.out.println("查询所有区域水质化验历史数据条件有无");
			}	
		
		}else{
			System.out.println("查询条件有误");
		}
		}else if(!"".equals(operationnum)){
			sewageid=gersewageidByoperationnum(operationnum);
			start_time = req.getParameter("start_time");// 传入查询开始时间
			end_time = req.getParameter("end_time");// 传入查询的结束时间
			if("".equals(start_time) && "".equals(end_time)){

				sql="SELECT TOP "+items1+" y.short_title,z.name ,y.operationnum,y.controlID,x.incod,x.innh3n,x.inp,x.outcod ,x.outnh3n,x.outp,CONVERT(varchar(19),x.testingtime,120) FROM waterTestManager x,sewage y,area z WHERE  x.id not in (SELECT TOP (("
						+ pagingnum1 + "-1)*" + items1
						+ ") id FROM waterTestManager WHERE sewageid ="+sewageid+" ORDER BY testingtime DESC) AND  x.sewageid=y.sewageID AND y.areaID=z.id AND x.sewageid ="+sewageid+" ORDER BY x.testingtime DESC";
					
			}else if(!"".equals(start_time) && !"".equals(end_time)){

				sql="SELECT TOP "+items1+" y.short_title,z.name ,y.operationnum,y.controlID,x.incod,x.innh3n,x.inp,x.outcod ,x.outnh3n,x.outp,CONVERT(varchar(19),x.testingtime,120) FROM waterTestManager x,sewage y,area z WHERE  x.id not in (SELECT TOP (("
						+ pagingnum1 + "-1)*" + items1
						+ ") id FROM waterTestManager WHERE sewageid ="+sewageid+" AND testingtime>='"+start_time+"' AND testingtime<='"+end_time+"' ORDER BY testingtime DESC) AND  x.sewageid=y.sewageID AND y.areaID=z.id AND x.sewageid ="+sewageid+" AND testingtime>='"+start_time+"' AND testingtime<='"+end_time+"' ORDER BY x.testingtime DESC";
				System.out.println(sql);
			}else{
				System.out.println("查询所有区域水质化验历史数据条件有无");
			}		
		}else{
			System.out.println("查询条件有误");
		}	
		System.out.println(sql);
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					temp++;
					WaterTestManager waterTesetManager = new WaterTestManager();
					waterTesetManager.setShort_title(resultSet.getString(1));// 污水站名称
					waterTesetManager.setCounty(resultSet.getString(2));// 站点运营编码
					waterTesetManager.setOperationnum(resultSet.getString(3));//
					waterTesetManager.setControlID(resultSet.getString(4));
					waterTesetManager.setIncod(resultSet.getString(5));
					waterTesetManager.setInnh3n(resultSet.getString(6));// 站点运营编码
					waterTesetManager.setInp(resultSet.getString(7));//
					waterTesetManager.setOutcod(resultSet.getString(8));
					waterTesetManager.setOutnh3n(resultSet.getString(9));
					waterTesetManager.setOutp(resultSet.getString(10));
					waterTesetManager.setTestingtime(resultSet.getString(11));
					list.add(waterTesetManager);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
	private PadingUtil GetWaterTestManagerCount(HttpServletRequest req) {// 返回断电断线信息总条数
		String sql = null;
		operationnum = req.getParameter("operationnum");// 运营编号
		if ("".equals(operationnum)) {//运营编号为空，按照区域、站点、时间段查询
		if ("".equals(county) && "".equals(short_title))//区域为空，站点为空
	   {
			if("".equals(start_time) && "".equals(end_time)){
				sql="SELECT count(*) FROM waterTestManager ";
			}else if(!"".equals(start_time) && !"".equals(end_time)){
				sql="SELECT count(*) FROM   waterTestManager WHERE testingtime>='"+start_time+"' AND testingtime<='"+end_time+"'";
			}else{
				System.out.println("查询所有区域水质化验历史数据条件有误");
			}
	   }
		else if(!"".equals(county) && "".equals(short_title))
				{//查询指定区域
				areaid = getAreaID(county);
				if("".equals(start_time) && "".equals(end_time)){
					sql="SELECT count(*) FROM waterTestManager WHERE sewageid in (SELECT sewageID FROM sewage WHERE areaID="+areaid+") ";
				}else if(!"".equals(start_time) && !"".equals(end_time)){
					sql="SELECT count(*) FROM waterTestManager WHERE testingtime>='"+start_time+"' AND testingtime<='"+end_time+"' AND  sewageid in (SELECT sewageID FROM sewage WHERE areaID="+areaid+")";
				}else{
						System.out.println("查询所有区域水质化验历史数据条件有无");
					}
			System.out.println(sql);
				}
		else if(!"".equals(county) && !"".equals(short_title))//区域和站点非空
		{//查询指定站点
			sewageid=gersewageid(short_title);
			if("".equals(start_time) && "".equals(end_time)){
				sql="SELECT count(*) FROM waterTestManager WHERE sewageid ="+sewageid+"";
			}else if(!"".equals(start_time) && !"".equals(end_time)){
				sql="SELECT count(*) FROM waterTestManager WHERE testingtime>='"+start_time+"' AND testingtime<='"+end_time+"' AND  sewageid ="+sewageid+" ";
			}else{
				System.out.println("查询所有区域水质化验历史数据条件有无");
			}	
		
		}else{
			System.out.println("查询条件有误");
		}
		}else{
			sewageid=gersewageidByoperationnum(operationnum);
			if("".equals(start_time) && "".equals(end_time)){
				sql="SELECT count(*) FROM waterTestManager WHERE sewageid ="+sewageid+" ";
			}else if(!"".equals(start_time) && !"".equals(end_time)){
				sql="SELECT count(*) FROM waterTestManager WHERE testingtime>='"+start_time+"' AND testingtime<='"+end_time+"' AND  sewageid ="+sewageid+"";
			}else{
				System.out.println("查询所有区域水质化验历史数据条件有无");
			}		
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
					padingUtil.setItemscount(a);// 总条数
					padingUtil.setItems(temp);// 当前条数
					padingUtil.setPagingnum(pagingnum1);// 当前页页码
					if ((a % items1) == 0) {
						padingUtil.setPagecount((int) (Math.ceil(a / items1)));
					} else {
						padingUtil
								.setPagecount((int) (Math.ceil(a / items1) + 1));
					}
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
	
	private String creatWaterTestManager(HttpServletRequest req)
			throws IOException {
		int sewageid = 0;
		String short_title = null;
		String testingtime = req.getParameter("testingtime").trim();
		String outcod = req.getParameter("outcod").trim();
		String outnh3n = req.getParameter("outnh3n").trim();
		String outp = req.getParameter("outp").trim();
		String incod = req.getParameter("incod").trim();
		String innh3n = req.getParameter("innh3n").trim();
		String inp = req.getParameter("inp").trim();
		float outcodf = Float.parseFloat(outcod);
		float outnh3nf = Float.parseFloat(outnh3n);
		float outpf = Float.parseFloat(outp);
		float incodf = Float.parseFloat(incod);
		float innh3nf = Float.parseFloat(innh3n);
		float inpf = Float.parseFloat(inp);
		short_title = req.getParameter("short_title").trim();// 污水站点
		try {
			if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
				short_title = new String(short_title.getBytes("iso-8859-1"),
						"utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((sewageid = gersewageid(short_title)) != 0) {
			String sql = "INSERT INTO waterTestManager(sewageid,testingtime,outcod,outnh3n,outp,incod,innh3n,inp) VALUES(?,?,?,?,?,?,?,?)";
			Connection connection = DBHelp.getConnection();
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, sewageid);
				ps.setString(2, testingtime);
				ps.setFloat(3, outcodf);
				ps.setFloat(4, outnh3nf);
				ps.setFloat(5, outpf);
				ps.setFloat(6, incodf);
				ps.setFloat(7, innh3nf);
				ps.setFloat(8, inpf);
				ps.execute();
			} catch (Exception e) {
				e.printStackTrace();
				return "create failed";
			} finally {
				DBHelp.closeResultSet(resultSet);
				DBHelp.closeStatement(statement);
				DBHelp.closeConnection(connection);
			}

		} else {
			return "sewage does not exist";
		}
		return "create success";

	}

	private int gersewageid(String short_title) {
		String sql = "SELECT sewageID FROM sewage WHERE short_title='"
				+ short_title + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					return resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return 0;
	}
	private int gersewageidByoperationnum(String operationnum  ) {
		String sql = "SELECT sewageID FROM sewage WHERE operationnum ='"
				+ operationnum   + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					return resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return 0;
	}
	private int getAreaID(String area) {
		String sql = "SELECT id FROM area WHERE name='" + area + "'";
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
