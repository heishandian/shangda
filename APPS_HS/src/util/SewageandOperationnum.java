package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SewageandOperationnum {
	public static Map<String,Object> getSewageidByShort_title(String short_title) {
		Map<String,Object> sewageandOperationnum = new HashMap<String,Object>();
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "SELECT sewageID,operationnum FROM sewage WHERE short_title = '"
				+ short_title + "'";
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					sewageandOperationnum.put("sewageID",resultSet.getInt(1)) ;
					sewageandOperationnum.put("operationnum",resultSet.getString(2)) ;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return sewageandOperationnum;
	}
}
