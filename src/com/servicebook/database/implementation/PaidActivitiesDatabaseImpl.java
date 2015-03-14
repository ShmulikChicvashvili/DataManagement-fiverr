package com.servicebook.database.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.primitives.DBPaidActivity;
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

	/**
	 * The add activity query.
	 * 
	 * @param title
	 *            String
	 * @param username
	 *            String
	 * @param capacity
	 *            short
	 * @param distance
	 *            short
	 * @param type
	 *            Enum
	 */
	private String addActivityQuery;

	/**
	 * The delete activity query.
	 * 
	 * @param id
	 *            int
	 */
	private String deleteActivityQuery;

	/**
	 * The get activities offered by user query.
	 * 
	 * @param username
	 *            String
	 */
	private String getActivitiesOfferedByUserQuery;

	private void initializeQueries() {
		addActivityQuery = String
				.format("INSERT INTO %s (`%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?);",
						activityTable, ActivityTableColumn.TITLE.toString()
								.toLowerCase(), ActivityTableColumn.USERNAME
								.toString().toLowerCase(),
						ActivityTableColumn.CAPACITY.toString().toLowerCase(),
						ActivityTableColumn.DISTANCE.toString().toLowerCase(),
						ActivityTableColumn.TYPE.toString().toLowerCase());

		deleteActivityQuery = String.format("DELETE FROM %s WHERE `%s`=?;",
				activityTable, ActivityTableColumn.ID.toString().toLowerCase());

		getActivitiesOfferedByUserQuery = String.format(
				"SELECT * FROM %s WHERE `%s`=?", activityTable,
				ActivityTableColumn.USERNAME.toString().toLowerCase());
	}

	public PaidActivitiesDatabaseImpl(String activityTable,
			String registrationTable, String schema, BasicDataSource datasource) {
		super(schema, datasource);
		this.activityTable = this.schema + "." + activityTable;
		this.registrationTable = this.schema + "." + registrationTable;

		initializeQueries();

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement()) {

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

	/**
	 * Sets the parameters for the prepared statement handling adding an
	 * activity to the database (as initialized in initializeQueries).
	 *
	 * @param stmt
	 *            the prepared statement that the parameters will be filled to
	 * @param activity
	 *            the activity to be added
	 * @throws SQLException
	 *             In case one or more of the parameters couldn't be set.
	 */
	private void setAddActivityPreparedStatementParams(PreparedStatement stmt,
			DBPaidActivity activity) throws SQLException {
		stmt.setString(1, activity.getTitle());
		stmt.setString(2, activity.getUsername());
		stmt.setShort(3, activity.getCapacity());
		stmt.setShort(4, activity.getDistance());
	}

	@Override
	public void addPaidService(DBPaidService service) {
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn
						.prepareStatement(addActivityQuery)) {
			setAddActivityPreparedStatementParams(stmt, service);
			stmt.setString(5, ActivityType.SERVICE.toString().toUpperCase());

			stmt.execute();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addPaidTask(DBPaidTask task) {
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn
						.prepareStatement(addActivityQuery)) {
			setAddActivityPreparedStatementParams(stmt, task);
			stmt.setString(5, ActivityType.TASK.toString().toUpperCase());

			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deletePaidActivity(int id, Connection conn) {
		try (PreparedStatement stmt = conn
				.prepareStatement(deleteActivityQuery)) {
			stmt.setInt(1, id);

			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		List<DBPaidService> services = new ArrayList<DBPaidService>();
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn
						.prepareStatement(getActivitiesOfferedByUserQuery)) {
			stmt.setString(1, username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<DBPaidTask> getTasksOfferedByUser(String username, int start,
			int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerToActivity(String username, long id, Connection conn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterFromActivity(String username, long id, Connection conn) {
		// TODO Auto-generated method stub

	}
}
