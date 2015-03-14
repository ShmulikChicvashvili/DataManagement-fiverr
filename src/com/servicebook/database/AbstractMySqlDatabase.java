package com.servicebook.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

public abstract class AbstractMySqlDatabase {
	protected BasicDataSource datasource;

	/**
	 * Instantiates a new abstract my sql database.
	 *
	 * @param datasource
	 *            the datasource
	 */
	public AbstractMySqlDatabase(String schema, BasicDataSource datasource) {
		super();
		this.datasource = datasource;
	}

	protected Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}
}
