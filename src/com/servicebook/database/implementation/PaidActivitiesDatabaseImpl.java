package com.servicebook.database.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.mysql.jdbc.MysqlErrorNumbers;
import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.paidActivities.ElementAlreadyExistException;
import com.servicebook.database.exceptions.paidActivities.ElementNotExistException;
import com.servicebook.database.exceptions.paidActivities.TableCreationException;
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
		SERVICE {
			@Override
			public String toDB() {
				return "SERVICE";
			}
		},
		TASK {
			@Override
			public String toDB() {
				return "TASK";
			}
		};

		public abstract String toDB();
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
	 * the get activity status query.
	 * 
	 * @param id
	 *            int
	 */
	private String getActivityStatusQuery;

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
	private String queryNumRegisteredField = "numRegistered";

	private String registerToActivityQuery;
	private String unregisterFromActivityQuery;

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

		getActivityStatusQuery = String.format(
				"SELECT %s.%s FROM %s WHERE `%s`=?", activityTable,
				ActivityTableColumn.TYPE.columnName(), activityTable,
				ActivityTableColumn.ID.columnName());

		getActivitiesOfferedByUserQuery = String
				.format("SELECT %s.*, count(%s.%s) AS "
						+ queryNumRegisteredField
						+ " FROM "
						+ activityTable
						+ " LEFT OUTER JOIN "
						+ registrationTable
						+ " ON ("
						+ activityTable
						+ "."
						+ ActivityTableColumn.ID.columnName()
						+ "="
						+ registrationTable
						+ "."
						+ RegistrationTableColumn.ID.columnName()
						+ ") WHERE %s.`%s`=? AND `%S`=? GROUP BY %s  ORDER BY `%s` LIMIT ?,?",
						activityTable, registrationTable,
						RegistrationTableColumn.USERNAME.columnName(),
						activityTable,
						ActivityTableColumn.USERNAME.columnName(),
						ActivityTableColumn.TYPE.columnName(),
						ActivityTableColumn.ID.columnName(),
						ActivityTableColumn.TITLE.columnName());
	}

	private boolean isValidId(int id) {
		return id >= 0;
	}

	public PaidActivitiesDatabaseImpl(String activityTable,
			String registrationTable, String schema, BasicDataSource datasource)
			throws TableCreationException {
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
			throw new TableCreationException(e);
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
	 * adds the activity to the database.
	 *
	 * @param activity
	 *            the activity to be added. id is ignored and set by the
	 *            database (incrementally), and capacity and distance must be
	 *            positive.
	 * @param type
	 *            the type
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
		assert (activity.getDistance() == 1);
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn
						.prepareStatement(addActivityQuery)) {
			stmt.setString(1, activity.getTitle());
			stmt.setString(2, activity.getUsername());
			stmt.setShort(3, activity.getCapacity());
			stmt.setShort(4, activity.getDistance());
			stmt.setString(5, type.toDB());

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
	public ActivityStatus getActivityStatus(int id)
			throws DatabaseUnkownFailureException, InvalidParameterException {
		if (!isValidId(id)) {
			throw new InvalidParameterException();
		}
		ActivityStatus $ = ActivityStatus.NOT_EXIST;

		try (Connection conn = getConnection()) {
			$ = getActivityStatus(id, conn);
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	@Override
	public ActivityStatus getActivityStatus(int id, Connection conn)
			throws DatabaseUnkownFailureException, InvalidParameterException {
		if (!isValidId(id) || isConnClosed(conn)) {
			throw new InvalidParameterException();
		}

		ActivityStatus $ = ActivityStatus.NOT_EXIST;
		try (PreparedStatement stmt = conn
				.prepareStatement(getActivityStatusQuery)) {
			stmt.setInt(0, id);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String type = rs.getString(ActivityTableColumn.TYPE
						.columnName());
				if (type.equals(ActivityType.SERVICE.toDB())) {
					$ = ActivityStatus.SERVICE;
				} else if (type.equals(ActivityType.TASK.toDB())) {
					$ = ActivityStatus.TASK;
				} else {
					assert (false);
				}
			}

		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	@Override
	public void deletePaidActivity(int id, Connection conn)
			throws DatabaseUnkownFailureException, InvalidParameterException {
		if (!isValidId(id) || isConnClosed(conn)) {
			throw new InvalidParameterException();
		}

		try (PreparedStatement stmt = conn
				.prepareStatement(deleteActivityQuery)) {
			stmt.setInt(1, id);

			conn.commit();
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}
	}

	private boolean isStartAmountInRanges(int start, int amount) {
		return start >= 0 && amount > 0;
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

	/**
	 * Gets the activities offered by user. The activities are ordered by their
	 * title.
	 *
	 * @param username
	 *            the user's username
	 * @param start
	 *            the index for the first activity.
	 * @param amount
	 *            the maximum amount of activities to retrieve.
	 * @param type
	 *            the type of the activity {@link ActivityType}
	 * @return the activities offered by the user
	 * @throws InvalidParameterException
	 *             In case a null was passed, or the ranges are bad.
	 * @throws DatabaseUnkownFailureException
	 *             In case of an unknown SQL exception
	 */
	private List<DBPaidActivity> getActivitiesOfferedByUser(String username,
			int start, int amount, ActivityType type)
			throws InvalidParameterException, DatabaseUnkownFailureException {
		if (username == null || !isStartAmountInRanges(start, amount)) {
			throw new InvalidParameterException();
		}

		List<DBPaidActivity> $ = new ArrayList<DBPaidActivity>();
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn
						.prepareStatement(getActivitiesOfferedByUserQuery)) {
			stmt.setString(1, username);
			stmt.setString(2, type.toDB());
			stmt.setInt(3, start);
			stmt.setInt(4, amount);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(ActivityTableColumn.ID.columnName());
				String title = rs.getString(ActivityTableColumn.TITLE
						.columnName());
				assert (username.equals(rs
						.getString(ActivityTableColumn.USERNAME.columnName())));
				short capacity = rs.getShort(ActivityTableColumn.CAPACITY
						.columnName());
				short distance = rs.getShort(ActivityTableColumn.DISTANCE
						.columnName());
				short numRegistered = rs.getShort(queryNumRegisteredField);

				DBPaidActivity activity = null;

				switch (type) {
				case SERVICE:
					activity = new DBPaidService(id, title, username, capacity,
							distance, numRegistered);
					break;
				case TASK:
					activity = new DBPaidTask(id, title, username, capacity,
							distance, numRegistered);
					break;
				default:
					break;

				}
				$.add(activity);
			}

			// ResultSet rs closes automatically when `stmt` closes
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		assert ($.size() <= amount);

		return $;

	}

	@Override
	public List<DBPaidService> getServicesOfferedByUser(String username,
			int start, int amount) throws InvalidParameterException,
			DatabaseUnkownFailureException {
		List<DBPaidService> $ = new ArrayList<DBPaidService>();
		List<DBPaidActivity> services = getActivitiesOfferedByUser(username,
				start, amount, ActivityType.SERVICE);

		for (DBPaidActivity service : services) {
			assert (service instanceof DBPaidService);
			if (!(service instanceof DBPaidService)) {
				continue;
			}
			$.add((DBPaidService) service);
		}

		return $;
	}

	@Override
	public List<DBPaidTask> getTasksOfferedByUser(String username, int start,
			int amount) throws DatabaseUnkownFailureException,
			InvalidParameterException {
		List<DBPaidTask> $ = new ArrayList<DBPaidTask>();
		List<DBPaidActivity> tasks = getActivitiesOfferedByUser(username,
				start, amount, ActivityType.TASK);

		for (DBPaidActivity task : tasks) {
			assert (task instanceof DBPaidTask);
			if (!(task instanceof DBPaidTask)) {
				continue;
			}
			$.add((DBPaidTask) task);
		}

		return $;
	}

	@Override
	public void registerToActivity(int id, String username, Connection conn)
			throws ElementAlreadyExistException,
			DatabaseUnkownFailureException, InvalidParameterException {
		if (!isValidId(id) || username == null || isConnClosed(conn)) {
			throw new InvalidParameterException();
		}

		try (PreparedStatement stmt = conn
				.prepareStatement(registerToActivityQuery)) {
			stmt.setInt(0, id);
			stmt.setString(1, username);
			stmt.executeUpdate();
		} catch (SQLException e) {
			if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
				throw new ElementAlreadyExistException(e);
			}
			throw new DatabaseUnkownFailureException(e);
		}
	}

	@Override
	public void unregisterFromActivity(int id, String username, Connection conn)
			throws ElementNotExistException, DatabaseUnkownFailureException,
			InvalidParameterException {
		if (!isValidId(id) || username == null || isConnClosed(conn)) {
			throw new InvalidParameterException();
		}

		try (PreparedStatement stmt = conn
				.prepareStatement(unregisterFromActivityQuery)) {
			stmt.setInt(0, id);
			stmt.setString(1, username);
			int deleted = stmt.executeUpdate();

			if (deleted == 0) {
				throw new ElementNotExistException();
			}
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}
	}
}
