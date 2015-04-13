package com.servicebook.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

public abstract class AbstractMySqlDatabase {
	protected BasicDataSource datasource;
	protected String schema;

	/**
	 * Instantiates a new abstract my sql database.
	 *
	 * @param datasource
	 *            the datasource
	 */
	public AbstractMySqlDatabase(String schema, BasicDataSource datasource) {
		super();
		this.datasource = datasource;
		this.schema = schema;
	}

	protected Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	/**
	 * Checks if is conn closed. surrounds the check so that an exception will
	 * not be thrown.
	 *
	 * @param conn
	 *            the conn to be checked
	 * @return true if conn is closed or an exception occured, false otherwise
	 */
	protected boolean isConnClosed(Connection conn) {
		try {
			if (conn.isClosed()) {
				return true;
			}
		} catch (SQLException e) {
			return true;
		}
		return false;
	}

	protected boolean isValidStr(String str) {
		return str != null && !str.isEmpty();
	}
}
