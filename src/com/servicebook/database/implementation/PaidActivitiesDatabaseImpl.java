package com.servicebook.database.implementation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

public class PaidActivitiesDatabaseImpl extends AbstractMySqlDatabase implements
		PaidActivitiesDatabase {
	private enum ActivityTableColumn {
		ID, TITLE, USERNAME, CAPACITY, DISTANCE, TYPE
	}

	private enum RegistrationTableColumn {
		ID, USERNAME
	}

	private enum ActivityType {
		SERVICE, TASK
	}

	private String activityTable;
	private String registrationTable;

	public PaidActivitiesDatabaseImpl(String activityTable,
			String registrationTable, String schema, BasicDataSource datasource) {
		super(schema, datasource);
		this.activityTable = this.schema + "." + activityTable;
		this.registrationTable = this.schema + "." + registrationTable;

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement()) {
			conn.setAutoCommit(false);

			stmt.execute(getActivityTableCreationQuery());
			stmt.execute(getRegistrationTableCreationQuery());

			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getRegistrationTableCreationQuery() {
		String $ = String.format("CREATE TABLE %s ("
				+ "`%s` INT UNSIGNED NOT NULL," + "`%s` VARCHAR(255) NOT NULL,"
				+ "PRIMARY KEY (`%s`, `%s`));", this.registrationTable,
				RegistrationTableColumn.ID.toString().toLowerCase(),
				RegistrationTableColumn.USERNAME.toString().toLowerCase(),
				RegistrationTableColumn.ID.toString().toLowerCase(),
				RegistrationTableColumn.USERNAME.toString().toLowerCase());

		return $;
	}

	private String getActivityTableCreationQuery() {
		String $ = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "`%s` INT UNSIGNED NOT NULL AUTO_INCREMENT,"
				+ "`%s` VARCHAR(255) NOT NULL," + "`%s` VARCHAR(255) NOT NULL,"
				+ "`%s` SMALLINT UNSIGNED NOT NULL,"
				+ "`%s` SMALLINT UNSIGNED NOT NULL,"
				+ "`%s` ENUM('SERVICE','TASK') NOT NULL,"
				+ "PRIMARY KEY (`%s`)," + "INDEX `USERNAME` (`%s` ASC))",
				this.activityTable, ActivityTableColumn.ID.toString()
						.toLowerCase(), ActivityTableColumn.TITLE.toString()
						.toLowerCase(), ActivityTableColumn.USERNAME.toString()
						.toLowerCase(), ActivityTableColumn.CAPACITY.toString()
						.toLowerCase(), ActivityTableColumn.DISTANCE.toString()
						.toLowerCase(), ActivityTableColumn.TYPE.toString()
						.toLowerCase(), ActivityTableColumn.ID.toString()
						.toLowerCase(), ActivityTableColumn.USERNAME.toString()
						.toLowerCase());

		return $;
	}

	@Override
	public void addPaidService(DBPaidService service) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPaidTask(DBPaidTask task) {
		// TODO Auto-generated method stsub

	}

	@Override
	public void deletePaidActivity(long id, Connection conn) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DBPaidService> getServicesOfferedToUser(String username,
			int start, int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBPaidTask> getTasksOfferedToUser(String username, int start,
			int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBPaidService> getServicesOfferedByUser(String username,
			int start, int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBPaidTask> getTasksOfferedByUser(String username, int start,
			int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerToActivity(String username, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterFromActivity(String username, long id) {
		// TODO Auto-generated method stub

	}
}
