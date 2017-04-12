package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author apple
 * connect database by the JDBC
 */
public class DBHelp {
//	private static final String url="jdbc:sqlserver://172.18.139.146:1433;databaseName=CS";
	private static final String url="jdbc:sqlserver://localhost:1433;databaseName=CS_XS";
	private static final String driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String userName="sa";
	private static final String userPassWord="WOmima123";
    //private static final String userPassWord="@@jiangnan##**&&123";
	//private static final String userPassWord="WOmima123";
	/*172.18.139.146
	 * private static final String url="jdbc:mysql://localhost:3306/test";
	private static final String driver="com.mysql.jdbc.Driver";
	private static final String userName="root";
	private static final String userPassWord="root12";*/
	
	public static Connection getConnection(){
		try {
			Class.forName(driver);
			Connection connection=DriverManager.getConnection(url, userName, userPassWord);
			if (null!=connection) {
				return connection;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void closeResultSet(ResultSet resultSet) {
		if (null!=resultSet) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void closeStatement(Statement statement) {
		if (null!=statement) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void closeConnection(Connection connection) {
		if (null!=connection) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		DBHelp.getConnection();
	}
}
