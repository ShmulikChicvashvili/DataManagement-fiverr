
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
import com.servicebook.database.exceptions.paidActivities.FriendshipsTableNotExist;
import com.servicebook.database.exceptions.paidActivities.InvalidParameterException;
import com.servicebook.database.exceptions.paidActivities.TableCreationException;
import com.servicebook.database.primitives.DBPaidActivity;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;




public class PaidActivitiesDatabaseImpl extends AbstractMySqlDatabase
	implements
		PaidActivitiesDatabase
{
	private class FriendsTableInfo
	{
		/**
		 * @param tableName
		 * @param userColumn
		 * @param friendColumn
		 */
		public FriendsTableInfo(
			String tableName,
			String userColumn,
			String friendColumn)
		{
			super();
			this.tableName = tableName;
			this.userColumn = userColumn;
			this.friendColumn = friendColumn;
		}



		public String tableName;

		public String userColumn;

		public String friendColumn;
	}



	private enum ActivityTableColumn
	{
		ID,
		TITLE,
		USERNAME,
		CAPACITY,
		DISTANCE,
		TYPE;

		public String columnName()
		{
			return toString().toLowerCase();
		}
	}



	private enum ActivityType
	{
		SERVICE
		{
			@Override
			public String toDB()
			{
				return "SERVICE";
			}
		},
		TASK
		{
			@Override
			public String toDB()
			{
				return "TASK";
			}
		};

		public abstract String toDB();
	}



	private enum RegistrationTableColumn
	{
		ID,
		USERNAME;

		public String columnName()
		{
			return toString().toLowerCase();
		}
	}



	public PaidActivitiesDatabaseImpl(
		String activityTable,
		String registrationTable,
		String schema,
		BasicDataSource datasource,
		String friendsTableName,
		String friendsTableUsernameColumn,
		String friendsTableFriendColumn) throws TableCreationException
	{
		super(schema, datasource);
		this.activityTable = this.schema + ".`" + activityTable + "`";
		this.registrationTable = this.schema + ".`" + registrationTable + "`";

		friendsTableInfo =
			new FriendsTableInfo(
				this.schema + ".`" + friendsTableName + "`",
				friendsTableUsernameColumn,
				friendsTableFriendColumn);

		initializeQueries();

		try (
			Connection conn = getConnection();
			Statement stmt = conn.createStatement())
		{

			stmt.execute(genActivityTableCreationQuery());
			stmt.execute(genRegistrationTableCreationQuery());

			conn.commit();
		} catch (final SQLException e)
		{
			throw new TableCreationException(e);
		}
	}


	@Override
	public int addPaidService(DBPaidService service, Connection conn)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		return AddPaidActivity(service, ActivityType.SERVICE, conn);
	}


	@Override
	public int addPaidTask(DBPaidTask task, Connection conn)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		return AddPaidActivity(task, ActivityType.TASK, conn);
	}


	@Override
	public void deletePaidActivity(int id, Connection conn)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		if (!isValidId(id) || isConnClosed(conn)) { throw new InvalidParameterException(); }

		try (
			PreparedStatement stmt = conn.prepareStatement(deleteActivityQuery))
		{
			stmt.setInt(1, id);

			stmt.executeUpdate();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
	}


	@Override
	public void deleteUserPaidActivities(String username, Connection conn)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (username == null || isConnClosed(conn)) { throw new InvalidParameterException(); }
		try (
			PreparedStatement stmt =
				conn.prepareStatement(deleteAllUserActivitiesQuery))
		{
			stmt.setString(1, username);

			stmt.executeUpdate();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}

	}


	@Override
	public void deleteUserRegistrations(String username, Connection conn)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (username == null || isConnClosed(conn)) { throw new InvalidParameterException(); }

		try (
			PreparedStatement stmt =
				conn.prepareStatement(deleteUserRegistrationsQuery))
		{
			stmt.setString(1, username);

			stmt.executeUpdate();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
	}


	@Override
	public ActivityStatus getActivityStatus(int id)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		if (!isValidId(id)) { throw new InvalidParameterException(); }
		ActivityStatus $ = ActivityStatus.NOT_EXIST;

		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			$ = getActivityStatus(id, conn);
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}


	@Override
	public ActivityStatus getActivityStatus(int id, Connection conn)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		if (!isValidId(id) || isConnClosed(conn)) { throw new InvalidParameterException(); }

		ActivityStatus $ = ActivityStatus.NOT_EXIST;
		try (
			PreparedStatement stmt =
				conn.prepareStatement(getActivityStatusQuery))
		{
			stmt.setInt(1, id);

			final ResultSet rs = stmt.executeQuery();

			if (rs.next())
			{
				final String type =
					rs.getString(ActivityTableColumn.TYPE.columnName());
				if (type.equals(ActivityType.SERVICE.toDB()))
				{
					$ = ActivityStatus.SERVICE;
				} else if (type.equals(ActivityType.TASK.toDB()))
				{
					$ = ActivityStatus.TASK;
				} else
				{
					assert false;
				}
			}

		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}


	@Override
	public DBPaidActivity getActivity(int id, Connection conn)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (!isValidId(id) || isConnClosed(conn)) { throw new InvalidParameterException(); }

		DBPaidActivity $ = null;

		try (PreparedStatement stmt = conn.prepareStatement(getActivityQuery))
		{
			stmt.setInt(1, id);

			final ResultSet rs = stmt.executeQuery();

			if (rs.next())
			{
				$ = rsRowToActivity(rs);
			}
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
		return $;
	}


	/* (non-Javadoc) @see
	 * com.servicebook.database.PaidActivitiesDatabase#getServicesUserRegistered
	 * (java.lang.String, int, int) */
	@Override
	public List<DBPaidService> getServicesUserRegistered(
		String username,
		int start,
		int amount)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		final List<DBPaidService> $ = new ArrayList<DBPaidService>();
		final List<DBPaidActivity> services =
			getActivitiesUserRegistered(
				username,
				start,
				amount,
				ActivityType.SERVICE);

		for (final DBPaidActivity service : services)
		{
			assert service instanceof DBPaidService;
			if (!(service instanceof DBPaidService))
			{
				continue;
			}
			$.add((DBPaidService) service);
		}

		return $;
	}


	/* (non-Javadoc) @see
	 * com.servicebook.database.PaidActivitiesDatabase#getTasksUserRegistered
	 * (java.lang.String, int, int) */
	@Override
	public List<DBPaidTask> getTasksUserRegistered(
		String username,
		int start,
		int amount)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		final List<DBPaidTask> $ = new ArrayList<DBPaidTask>();
		final List<DBPaidActivity> tasks =
			getActivitiesUserRegistered(
				username,
				start,
				amount,
				ActivityType.TASK);

		for (final DBPaidActivity task : tasks)
		{
			assert task instanceof DBPaidTask;
			if (!(task instanceof DBPaidTask))
			{
				continue;
			}
			$.add((DBPaidTask) task);
		}

		return $;
	}


	@Override
	public List<DBPaidService> getServicesOfferedByUser(
		String username,
		int start,
		int amount)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		final List<DBPaidService> $ = new ArrayList<DBPaidService>();
		final List<DBPaidActivity> services =
			getActivitiesOfferedByUser(
				username,
				start,
				amount,
				ActivityType.SERVICE);

		for (final DBPaidActivity service : services)
		{
			assert service instanceof DBPaidService;
			if (!(service instanceof DBPaidService))
			{
				continue;
			}
			$.add((DBPaidService) service);
		}

		return $;
	}


	@Override
	public List<DBPaidService> getServicesOfferedToUser(
		String username,
		int start,
		int amount)
		throws InvalidParameterException,
		DatabaseUnkownFailureException,
		FriendshipsTableNotExist
	{
		final List<DBPaidService> $ = new ArrayList<DBPaidService>();
		final List<DBPaidActivity> services =
			getActivitiesOfferedToUser(
				username,
				start,
				amount,
				ActivityType.SERVICE);

		for (final DBPaidActivity service : services)
		{
			assert service instanceof DBPaidService;
			if (!(service instanceof DBPaidService))
			{
				continue;
			}
			$.add((DBPaidService) service);
		}

		return $;
	}


	@Override
	public List<DBPaidTask> getTasksOfferedByUser(
		String username,
		int start,
		int amount)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		final List<DBPaidTask> $ = new ArrayList<DBPaidTask>();
		final List<DBPaidActivity> tasks =
			getActivitiesOfferedByUser(
				username,
				start,
				amount,
				ActivityType.TASK);

		for (final DBPaidActivity task : tasks)
		{
			assert task instanceof DBPaidTask;
			if (!(task instanceof DBPaidTask))
			{
				continue;
			}
			$.add((DBPaidTask) task);
		}

		return $;
	}


	@Override
	public List<DBPaidTask> getTasksOfferedToUser(
		String username,
		int start,
		int amount)
		throws InvalidParameterException,
		DatabaseUnkownFailureException,
		FriendshipsTableNotExist
	{
		final List<DBPaidTask> $ = new ArrayList<DBPaidTask>();
		final List<DBPaidActivity> tasks =
			getActivitiesOfferedToUser(
				username,
				start,
				amount,
				ActivityType.TASK);

		for (final DBPaidActivity task : tasks)
		{
			assert task instanceof DBPaidTask;
			if (!(task instanceof DBPaidTask))
			{
				continue;
			}
			$.add((DBPaidTask) task);
		}

		return $;
	}


	@Override
	public void registerToActivity(int id, String username, Connection conn)
		throws ElementAlreadyExistException,
		DatabaseUnkownFailureException,
		InvalidParameterException
	{
		if (!isValidId(id) || username == null || isConnClosed(conn)) { throw new InvalidParameterException(); }

		try (
			PreparedStatement stmt =
				conn.prepareStatement(registerToActivityQuery))
		{
			stmt.setInt(1, id);
			stmt.setString(2, username);
			stmt.executeUpdate();
		} catch (final SQLException e)
		{
			if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) { throw new ElementAlreadyExistException(
				e); }
			throw new DatabaseUnkownFailureException(e);
		}
	}


	@Override
	public
		void
		unregisterFromActivity(int id, String username, Connection conn)
			throws DatabaseUnkownFailureException,
			InvalidParameterException,
			ElementNotExistException
	{
		if (!isValidId(id) || username == null || isConnClosed(conn)) { throw new InvalidParameterException(); }

		try (
			PreparedStatement stmt =
				conn.prepareStatement(unregisterFromActivityQuery))
		{
			stmt.setInt(1, id);
			stmt.setString(2, username);
			final int numDeleted = stmt.executeUpdate();

			if (numDeleted == 0) { throw new ElementNotExistException(); }

		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
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
	 * @return the id of the added activity. also sets the activity id.
	 * @throws InvalidParameterException
	 *             In case the activity is null, or capacity or distance are non
	 *             positive.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unexpected SQL error occurs
	 */
	private int AddPaidActivity(
		DBPaidActivity activity,
		ActivityType type,
		Connection conn)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (activity == null
			|| activity.getCapacity() <= 0
			|| activity.getDistance() <= 0
			|| isConnClosed(conn)) { throw new InvalidParameterException(); }

		int $ = -1;

		assert activity.getDistance() == 1;
		try (
			PreparedStatement stmt =
				conn.prepareStatement(
					addActivityQuery,
					Statement.RETURN_GENERATED_KEYS))
		{
			stmt.setString(1, activity.getTitle());
			stmt.setString(2, activity.getUsername());
			stmt.setShort(3, activity.getCapacity());
			stmt.setShort(4, activity.getDistance());
			stmt.setString(5, type.toDB());

			stmt.executeUpdate();
			final ResultSet rs = stmt.getGeneratedKeys();

			if (rs.next())
			{
				$ = rs.getInt(1);
				activity.setId($);
			}
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}


	private String genActivityTableCreationQuery()
	{
		final String $ =
			String.format(
				"CREATE TABLE IF NOT EXISTS %s ("
					+ "`%s` INT UNSIGNED NOT NULL AUTO_INCREMENT,"
					+ "`%s` VARCHAR(255) NOT NULL,"
					+ "`%s` VARCHAR(255) NOT NULL,"
					+ "`%s` SMALLINT UNSIGNED NOT NULL,"
					+ "`%s` SMALLINT UNSIGNED NOT NULL,"
					+ "`%s` ENUM('SERVICE','TASK') NOT NULL,"
					+ "PRIMARY KEY (`%s`),"
					+ "INDEX `USERNAME` (`%s` ASC))",
				activityTable,
				ActivityTableColumn.ID.columnName(),
				ActivityTableColumn.TITLE.columnName(),
				ActivityTableColumn.USERNAME.columnName(),
				ActivityTableColumn.CAPACITY.columnName(),
				ActivityTableColumn.DISTANCE.columnName(),
				ActivityTableColumn.TYPE.columnName(),
				ActivityTableColumn.ID.columnName(),
				ActivityTableColumn.USERNAME.columnName());

		return $;
	}


	private String genRegistrationTableCreationQuery()
	{
		final String $ =
			String.format(
				"CREATE TABLE IF NOT EXISTS %s ("
					+ "`%s` INT UNSIGNED NOT NULL,"
					+ "`%s` VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (`%s`, `%s`));",
				registrationTable,
				RegistrationTableColumn.ID.columnName(),
				RegistrationTableColumn.USERNAME.columnName(),
				RegistrationTableColumn.ID.columnName(),
				RegistrationTableColumn.USERNAME.columnName());

		return $;
	}


	/**
	 * Converts a resultset row to an activity
	 *
	 * @param rs
	 *            the rs to be used. must be positioned on the line to convert
	 * @return the activity that corresponds to that line. Either
	 *         {@link DBPaidService} or {@link DBPaidTask}.
	 * @throws SQLException
	 *             the SQL exception
	 */
	private DBPaidActivity rsRowToActivity(final ResultSet rs)
		throws SQLException
	{
		final int id = rs.getInt(ActivityTableColumn.ID.columnName());
		final String title =
			rs.getString(ActivityTableColumn.TITLE.columnName());
		final String username =
			rs.getString(ActivityTableColumn.USERNAME.columnName());
		final short capacity =
			rs.getShort(ActivityTableColumn.CAPACITY.columnName());
		final short distance =
			rs.getShort(ActivityTableColumn.DISTANCE.columnName());
		final short numRegistered = rs.getShort(queryNumRegisteredField);

		final String typeString =
			rs.getString(ActivityTableColumn.TYPE.columnName());
		ActivityType type;
		if (typeString.equals(ActivityType.SERVICE.toDB()))
		{
			type = ActivityType.SERVICE;
		} else if (typeString.equals(ActivityType.TASK.toDB()))
		{
			type = ActivityType.TASK;
		} else
		{
			return null;
		}

		DBPaidActivity $ = null;

		switch (type)
		{
			case SERVICE:

				$ =
					new DBPaidService(
						id,
						title,
						username,
						capacity,
						distance,
						numRegistered);
				break;
			case TASK:
				$ =
					new DBPaidTask(
						id,
						title,
						username,
						capacity,
						distance,
						numRegistered);
				break;
		}
		return $;
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
	private List<DBPaidActivity> getActivitiesOfferedByUser(
		String username,
		int start,
		int amount,
		ActivityType type)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (username == null || !isStartAmountInRanges(start, amount)) { throw new InvalidParameterException(); }

		final List<DBPaidActivity> $ = new ArrayList<DBPaidActivity>();
		try (
			Connection conn = getConnection();
			PreparedStatement stmt =
				conn.prepareStatement(getActivitiesOfferedByUserQuery))
		{
			conn
				.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

			stmt.setString(1, username);
			stmt.setString(2, type.toDB());
			stmt.setInt(3, start);
			stmt.setInt(4, amount);

			final ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				assert username.equals(rs
					.getString(ActivityTableColumn.USERNAME.columnName()));
				assert type.toDB().equals(
					rs.getString(ActivityTableColumn.TYPE.columnName()));

				final DBPaidActivity activity = rsRowToActivity(rs);
				$.add(activity);
			}

			// ResultSet rs closes automatically when `stmt` closes
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}

		assert $.size() <= amount;

		return $;

	}


	/**
	 * Gets the activities the user registered to. The activities are ordered by
	 * their title.
	 *
	 * @param username
	 *            the user's username
	 * @param start
	 *            the index for the first activity.
	 * @param amount
	 *            the maximum amount of activities to retrieve.
	 * @param type
	 *            the type of the activity {@link ActivityType}
	 * @return the activities the user is registered to.
	 * @throws InvalidParameterException
	 *             In case a null was passed, or the ranges are bad.
	 * @throws DatabaseUnkownFailureException
	 *             In case of an unknown SQL exception
	 */
	private List<DBPaidActivity> getActivitiesUserRegistered(
		String username,
		int start,
		int amount,
		ActivityType type)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (username == null || !isStartAmountInRanges(start, amount)) { throw new InvalidParameterException(); }

		final List<DBPaidActivity> $ = new ArrayList<DBPaidActivity>();
		try (
			Connection conn = getConnection();
			PreparedStatement stmt =
				conn.prepareStatement(getActivitiesUserRegisteredQuery))
		{
			conn
				.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

			stmt.setString(1, type.toDB());
			stmt.setString(2, username);
			stmt.setInt(3, start);
			stmt.setInt(4, amount);

			final ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				assert type.toDB().equals(
					rs.getString(ActivityTableColumn.TYPE.columnName()));

				final DBPaidActivity activity = rsRowToActivity(rs);
				$.add(activity);
			}

			// ResultSet rs closes automatically when `stmt` closes
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}

		assert $.size() <= amount;

		return $;

	}
	
	public int getUserOfferedActivitiesCount(String username) {
		int $ = 0;
		if(username == null) { throw new InvalidParameterException(); }
		
		String sqlExpression = "SELECT COUNT(*) FROM servicebook_db.activities JOIN servicebook_db.friendships ON (servicebook_db.activities.username = servicebook_db.friendships.first_username AND servicebook_db.friendships.second_username = ?);";
		try(Connection conn = getConnection();
			PreparedStatement prpdStmt = )
		
		return $;
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
	 * @throws FriendshipsTableNotExist
	 *             the friendships table does not exist
	 */
	private List<DBPaidActivity> getActivitiesOfferedToUser(
		String username,
		int start,
		int amount,
		ActivityType type)
		throws InvalidParameterException,
		DatabaseUnkownFailureException,
		FriendshipsTableNotExist
	{
		if (username == null || !isStartAmountInRanges(start, amount)) { throw new InvalidParameterException(); }

		final List<DBPaidActivity> $ = new ArrayList<DBPaidActivity>();
		try (
			Connection conn = getConnection();
			PreparedStatement stmt =
				conn.prepareStatement(getActivitiesOfferedToUserQuery))
		{
			conn
				.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

			stmt.setString(1, username);
			stmt.setString(2, type.toDB());
			stmt.setString(3, username);
			stmt.setInt(4, start);
			stmt.setInt(5, amount);

			final ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				assert type.toDB().equals(
					rs.getString(ActivityTableColumn.TYPE.columnName()));
				final DBPaidActivity activity = rsRowToActivity(rs);
				$.add(activity);
			}

			// ResultSet rs closes automatically when `stmt` closes
		} catch (final SQLException e)
		{
			if (e.getErrorCode() == MysqlErrorNumbers.ER_NO_SUCH_TABLE) { throw new FriendshipsTableNotExist(
				e); }
			throw new DatabaseUnkownFailureException(e);
		}

		assert $.size() <= amount;

		return $;
	}


	private void initializeQueries()
	{
		addActivityQuery =
			String.format(
				"INSERT INTO %s (`%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?);"
				/* + " SELECT LAST_INSERT_ID()" */,
				activityTable,
				ActivityTableColumn.TITLE.columnName(),
				ActivityTableColumn.USERNAME.columnName(),
				ActivityTableColumn.CAPACITY.columnName(),
				ActivityTableColumn.DISTANCE.columnName(),
				ActivityTableColumn.TYPE.columnName());

		final String deleteQueryPrefix =
			String.format(
				"DELETE %s,%s FROM %s LEFT OUTER JOIN %s ON ("
					+ activityTable
					+ "."
					+ ActivityTableColumn.ID.columnName()
					+ "="
					+ registrationTable
					+ "."
					+ RegistrationTableColumn.ID
					+ ")",
				activityTable,
				registrationTable,
				activityTable,
				registrationTable);

		deleteActivityQuery =
			deleteQueryPrefix
				+ String.format(
					"WHERE %s.`%s`=?;",
					activityTable,
					ActivityTableColumn.ID.columnName());

		deleteAllUserActivitiesQuery =
			deleteQueryPrefix
				+ String.format(
					"WHERE %s.`%s`=?;",
					activityTable,
					ActivityTableColumn.USERNAME.columnName());

		deleteUserRegistrationsQuery =
			String.format(
				"DELETE FROM %s WHERE %s.`%s`=?;",
				registrationTable,
				registrationTable,
				RegistrationTableColumn.USERNAME.columnName());

		getActivityStatusQuery =
			String.format(
				"SELECT %s FROM %s WHERE `%s`=?",
				ActivityTableColumn.TYPE.columnName(),
				activityTable,
				ActivityTableColumn.ID.columnName());

		final String getActivityPrefix =
			String.format(
				"SELECT %s.*, count(%s.%s) AS "
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
					+ ") ",
				activityTable,
				registrationTable,
				RegistrationTableColumn.USERNAME.columnName());

		final String getActivitySuffix =
			String.format(
				" GROUP BY %s.`%s` ORDER BY `%s` LIMIT ?,?",
				activityTable,
				ActivityTableColumn.ID.columnName(),
				ActivityTableColumn.TITLE.columnName());

		getActivityQuery =
			getActivityPrefix
				+ String.format(
					" WHERE %s.`%s`=? HAVING %s.`%s` IS NOT NULL",
					activityTable,
					ActivityTableColumn.ID.columnName(),
					activityTable,
					ActivityTableColumn.ID);

		getActivitiesOfferedByUserQuery =
			getActivityPrefix
				+ String.format(
					" WHERE %s.`%s`=? AND `%s`=? " + getActivitySuffix,
					activityTable,
					ActivityTableColumn.USERNAME.columnName(),
					ActivityTableColumn.TYPE.columnName());

		getActivitiesUserRegisteredQuery =
			getActivityPrefix
				+ String.format(
					" WHERE `%s`=? AND EXISTS (SELECT * FROM %s WHERE %s.%s=%s.%s AND %s.%s=?) "
						+ getActivitySuffix,
					ActivityTableColumn.TYPE.columnName(),
					registrationTable,
					registrationTable,
					RegistrationTableColumn.ID.columnName(),
					activityTable,
					ActivityTableColumn.ID.columnName(),
					registrationTable,
					RegistrationTableColumn.USERNAME.columnName());

		getActivitiesOfferedToUserQuery =
			String.format(
				"SELECT %s.*, count(%s.%s) AS "
					+ queryNumRegisteredField
					+ " FROM "
					// join friends with activities to get all 1-distance
					// activities
					+ activityTable
					+ " JOIN "
					+ friendsTableInfo.tableName
					+ " ON ("
					+ String.format(
						"%s.%s=%s.%s",
						activityTable,
						ActivityTableColumn.USERNAME.columnName(),
						friendsTableInfo.tableName,
						friendsTableInfo.friendColumn) + ") LEFT OUTER JOIN "
					// join on registrations to get number of registered users
					+ registrationTable
					+ " ON ("
					+ activityTable
					+ "."
					+ ActivityTableColumn.ID.columnName()
					+ "="
					+ registrationTable
					+ "."
					+ RegistrationTableColumn.ID.columnName()
					+ ")"
					// username and type must correspond
					+ " WHERE %s.`%s`=? AND `%s`=? "
					// User is not registered to the activity
					+ "AND NOT EXISTS (SELECT * FROM "
					+ registrationTable
					+ " WHERE "
					+ registrationTable
					+ "."
					+ RegistrationTableColumn.ID.columnName()
					+ "="
					+ activityTable
					+ "."
					+ ActivityTableColumn.ID.columnName()
					+ " AND "
					+ registrationTable
					+ "."
					+ RegistrationTableColumn.USERNAME.columnName()
					+ "=?) "
					// count number of registered and limit result
					+ getActivitySuffix,
				activityTable,
				registrationTable,
				RegistrationTableColumn.USERNAME.columnName(),
				friendsTableInfo.tableName,
				friendsTableInfo.userColumn,
				ActivityTableColumn.TYPE.columnName());

		registerToActivityQuery =
			String.format(
				"INSERT INTO %s (%s,%s) VALUES (?,?)",
				registrationTable,
				RegistrationTableColumn.ID.columnName(),
				RegistrationTableColumn.USERNAME.columnName());

		unregisterFromActivityQuery =
			String.format(
				"DELETE FROM %s WHERE `%s`=? and `%s`=?",
				registrationTable,
				RegistrationTableColumn.ID.columnName(),
				RegistrationTableColumn.USERNAME.columnName());
	}


	private boolean isStartAmountInRanges(int start, int amount)
	{
		return start >= 0 && amount > 0;
	}


	private boolean isValidId(int id)
	{
		return id >= 0;
	}



	private final String activityTable;

	private final String registrationTable;

	private final FriendsTableInfo friendsTableInfo;

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
	 * The get activity query.
	 *
	 * @param id
	 *            int
	 */
	private String getActivityQuery;

	/**
	 * The delete activity query.
	 *
	 * @param id
	 *            int
	 */
	private String deleteActivityQuery;

	/**
	 * The delete all user activities query.
	 *
	 * @param username
	 *            String
	 */
	private String deleteAllUserActivitiesQuery;

	/**
	 * The delete user registrations query.
	 *
	 * @param username
	 *            String
	 */
	private String deleteUserRegistrationsQuery;

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
	 */
	private String getActivitiesOfferedByUserQuery;

	/**
	 * The get activities user registered query.
	 *
	 * @param type
	 *            Enum
	 * @param username
	 *            String
	 * @param offset
	 *            int
	 * @param amount
	 *            int
	 */
	private String getActivitiesUserRegisteredQuery;

	/**
	 * The get activities offered to user query.
	 *
	 * @param username
	 *            String
	 * @param type
	 *            Enum
	 * @param username
	 *            String
	 * @param offset
	 *            int
	 * @param amount
	 *            int
	 */
	private String getActivitiesOfferedToUserQuery;

	private final String queryNumRegisteredField = "numRegistered";

	/**
	 * The registration to an activity query
	 *
	 * @param id
	 *            int
	 * @param username
	 *            String
	 */
	private String registerToActivityQuery;

	/**
	 * The unregister from an activity query
	 *
	 * @param id
	 *            int
	 * @param username
	 *            String
	 */
	private String unregisterFromActivityQuery;

}
