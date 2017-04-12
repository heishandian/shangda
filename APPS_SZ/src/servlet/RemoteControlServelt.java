package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DBHelp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import beans.Sewage1;


/**
 * @author apple configure-Sewage CUR(D no)
 */
public class RemoteControlServelt extends HttpServlet {
	private int sewageID;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = resp.getWriter();
		printWriter.print(RemoteControl(req));
		printWriter.flush();
		printWriter.close();
	}

	public static void sendSewageid(int sewageid,int control) throws InterruptedException {
		try {
			Socket socket = new Socket("58.215.202.186", 8990);
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			byte[] req = new byte[3];
			int a = sewageid / 127;
			int b = sewageid % 127;
			req[0] = (byte) 35;
			req[1] = (byte) a;
			req[2] = (byte) b;
			req[3] = (byte) control;
			// 接收服务器的相应
			byte[] reply = new byte[2];
			for (int j = 0; j < 2; j++) {
				os.write(req);
				os.flush();
				// 读两次
				is.read(reply);
				Thread.sleep(100L);
				is.read(reply);
				if (reply[0] == 35 || reply[1] == 35) {
					// 重新再发一次，否则
					System.out.println(reply[0]+" "+reply[1]);
					break;
				} else {
					Thread.sleep(10000L);
				}
			}
			socket.shutdownOutput();
			is.close();
			os.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String RemoteControl(HttpServletRequest req) throws IOException {
		int controlparameter = 0;
		Map<Integer,String> map = new HashMap<Integer,String>();
		map.put(1, "equip1");
		map.put(2, "equip2");
		map.put(3, "equip3");
		map.put(4, "equip4");
		map.put(5, "equip5");
		map.put(6, "equip6");
		map.put(7, "equip7");
		String short_title = req.getParameter("short_title").trim();// 所属地区

		if (!"utf-8".equalsIgnoreCase(req.getCharacterEncoding())) {
			short_title = new String(short_title.getBytes("iso-8859-1"), "utf-8");
		}
		
		Integer sewageid = querySewageId(short_title);
		String control = req.getParameter("control").trim();// 控制系统ID
		System.out.println(control);
		StringBuilder sql = new StringBuilder("INSERT INTO remoteControl(sewageid,");
		char [] controlArr = control.toCharArray();
		for(int i=controlArr.length-1;i>=0;i--){
			controlparameter+=changetodecimal(controlArr.length-i,String.valueOf(controlArr[i]));
			sql.append(map.get(i+1));
			if(i!=0)
			sql.append(",");
		}
		sql.append(") values(");
		sql.append(sewageid);
		sql.append(",");
		for(int i=0;i<controlArr.length;i++){
			sql.append(controlArr[i]);
			if(i<controlArr.length-1)
			sql.append(",");
		}
		sql.append(")");
		System.out.println(sql);
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(sql.toString());
			/*try {
			sendSewageid(sewageid,controlparameter);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			return "success";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		} finally {
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
	}


private int changetodecimal(int a,String b){
	int result = 1;
	int c = Integer.parseInt(b);
	for(int i=1;i<a;i++)
	{
	result*=2;	
	}
	return c*result;
}

	private int querySewageId(String stationname) {

		String sql = "SELECT sewageID FROM sewage WHERE short_title='" + stationname + "'";
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int id = 0;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {

				while (resultSet.next()) {
					id = resultSet.getInt(1);
					// return id;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return id;
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return id;
	}

}
