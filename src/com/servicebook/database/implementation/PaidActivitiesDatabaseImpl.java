package com.servicebook.database.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.paidActivities.DatabaseAlreadyExistsException;
import com.servicebook.database.exceptions.paidActivities.DatabaseCreationException;
import com.servicebook.database.exceptions.paidActivities.InvalidParameterException;
import com.servicebook.database.primitives.DBPaidActivity;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

public class PaidActivitiesDatabaseImpl extends AbstractMySqlDatabase implements
		PaidActivitiesDatabase {
	private enum ActivityTableColumn {
		ID, TITLE, USERNAME, CAPACITY, DISTANCE, TYPE;

		public String columnName() {
			return toString().toLowerCase();
		}
	}

	private enum RegistrationTableColumn {
		ID, USERNAME;

		public String columnName() {
			return toString().toLowerCase();
		}
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
	 * @param type
	 *            Enum
	 * @param offset
	 *            int
	 * @param amount
	 *            int
	 */
	private String getActivitiesOfferedByUserQuery;

	private void initializeQueries() {
		addActivityQuery = String
				.format("INSERT INTO %s (`%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?);",
						activityTable, ActivityTableColumn.TITLE.columnName(),
						ActivityTableColumn.USERNAME.columnName(),
						ActivityTableColumn.CAPACITY.columnName(),
						ActivityTableColumn.DISTANCE.columnName(),
						ActivityTableColumn.TYPE.columnName());

		deleteActivityQuery = String.format("DELETE FROM %s WHERE `%s`=?;",
				activityTable, ActivityTableColumn.ID.columnName());

		getActivitiesOfferedByUserQuery = String
				.format("SELECT %s.*, count(*) FROM %s JOIN %s ON () GROUP BY %s WHERE `%s`=? AND `%S`=? ORDER BY `%s` LIMIT ?,?",
						activityTable,
						ActivityTableColumn.USERNAME.columnName(),
						ActivityTableColumn.TYPE.columnName(),
						ActivityTableColumn.TITLE.columnName());
	}

	public PaidActivitiesDatabaseImpl(String activityTable,
			String registrationTable, String schema, BasicDataSource datasource)
			throws DatabaseCreationException {
		super(schema, datasource);
		this.activityTable = this.schema + ".`" + activityTable + "`";
		this.registrationTable = this.schema + ".`" + registrationTable + "`";

		initializeQueries();

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement()) {

			stmt.execute(getActivityTableCreationQuery());
			stmt.execute(getRegistrationTableCreationQuery());

			conn.commit();
		} catch (SQLException e) {
			DatabaseCreationException exp = new DatabaseCreationException(e);
			throw exp;
		}
	}

	private String getRegistrationTableCreationQuery() {
		String $ = String.format("CREATE TABLE %s ("
				+ "`%s` INT UNSIGNED NOT NULL," + "`%s` VARCHAR(255) NOT NULL,"
				+ "PRIMARY KEY (`%s`, `%s`));", this.registrationTable,
				RegistrationTableColumn.ID.columnName(),
				RegistrationTableColumn.USERNAME.columnName(),
				RegistrationTableColumn.ID.columnName(),
				RegistrationTableColumn.USERNAME.columnName());

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
				this.activityTable, ActivityTableColumn.ID.columnName(),
				ActivityTableColumn.TITLE.columnName(),
				ActivityTableColumn.USERNAME.columnName(),
				ActivityTableColumn.CAPACITY.columnName(),
				ActivityTableColumn.DISTANCE.columnName(),
				ActivityTableColumn.TYPE.columnName(),
				ActivityTableColumn.ID.columnName(),
				ActivityTableColumn.USERNAME.columnName());

		return $;
	}

	/**
	 * Sets the parameters for the prepared statement handling adding an
	 * activity to the database (as initialized in initializeQueries).
	 * 
	 * @param activity
	 *            the activity to be added. id is ignored and set by the
	 *            database (incrementally), and capacity and distance must be
	 *            positive.
	 * @throws InvalidParameterException
	 *             In case the activity is null, or capacity or distance are non
	 *             positive.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unexpected SQL error occurs
	 */
	private void AddPaidActivity(DBPaidActivity activity, ActivityType type)
			throws InvalidParameterException, DatabaseUnkownFailureException {
		if (activity == null || activity.getCapacity() <= 0
				|| activity.getDistance() <= 0) {
			throw new InvalidParameterException();
		}
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn
						.prepareStatement(addActivityQuery)) {
			stmt.setString(1, activity.getTitle());
			stmt.setString(2, activity.getUsername());
			stmt.setShort(3, activity.getCapacity());
			stmt.setShort(4, activity.getDistance());
			stmt.setString(5, type.toString().toUpperCase());

			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

	}

	@Override
	public void addPaidService(DBPaidService service)
			throws InvalidParameterException, DatabaseUnkownFailureException {
		AddPaidActivity(service, ActivityType.SERVICE);
	}

	@Override
	public void addPaidTask(DBPaidTask task)
			throws DatabaseUnkownFailureException, InvalidParameterException {
		AddPaidActivity(task, ActivityType.TASK);
	}

	@Override
	public void deletePaidActivity(int id, Connection conn)
			throws DatabaseUnkownFailureException {
		try (PreparedStatement stmt = conn
				.prepareStatement(deleteActivityQuery)) {
			stmt.setInt(1, id);

			conn.commit();
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}
	}

	private boolean isStartAmountInRanges(int start, int amount) {
		return start > 0 && amount > 0;
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
			int start, int amount) throws InvalidParameterException {
		if (username == null || !isStartAmountInRanges(start, amount)) {
			throw new InvalidParameterException();
		}

		List<DBPaidService> services = new ArrayList<DBPaidService>();
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn
						.prepareStatement(getActivitiesOfferedByUserQuery)) {
			stmt.setString(1, username);
			stmt.setString(2, ActivityType.SERVICE.toString().toUpperCase());
			stmt.setInt(3, start);
			stmt.setInt(4, amount);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(ActivityTableColumn.ID.columnName());
				String title = rs.getString(ActivityTableColumn.TITLE
						.columnName());
				assert (username.equals(rs
						.getString(ActivityTableColumn.USERNAME.columnName())));
				// String username = rs.getString(ActivityTableColumn.USERNAME
				// .columnName());
				short capacity = rs.getShort(ActivityTableColumn.CAPACITY
						.columnName());
				short distance = rs.getShort(ActivityTableColumn.DISTANCE
						.columnName());

			}
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
