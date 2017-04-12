package com.czq.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Sewage {
	public int getSewageidByShort_title(String short_title) {
		int sewageid = 0;
		Connection connection = DBHelp.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "SELECT sewageID FROM sewage WHERE short_title = '"
				+ short_title + "'";
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet != null) {
				while (resultSet.next()) {
					sewageid = resultSet.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelp.closeResultSet(resultSet);
			DBHelp.closeStatement(statement);
			DBHelp.closeConnection(connection);
		}
		return sewageid;
	}
}
