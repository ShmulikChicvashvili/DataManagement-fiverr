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
import com.servicebook.database.UsersDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.users.TableCreationException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.exceptions.users.ElementAlreadyExistsException;
import com.servicebook.database.primitives.DBUser;

/**
 * @author Shmulik
 *
 */
public class UsersDatabaseImpl extends AbstractMySqlDatabase implements
		UsersDatabase {

	/**
	 * @author Shmulik
	 *
	 *         The columns of users database
	 */
	private enum Columns {
		/**
		 * Username column representation
		 */
		USERNAME,
		/**
		 * Password column representation
		 */
		PASSWORD,
		/**
		 * Name column representation
		 */
		NAME,
		/**
		 * Balance column representation
		 */
		BALANCE,
	}

	/**
	 * @param table
	 *            The table name
	 * @param schema
	 *            The scheme name
	 * @param datasource
	 *            The connection handler
	 * @throws TableCreationException
	 *             The exception in case of failure
	 */
	@SuppressWarnings("nls")
	public UsersDatabaseImpl(String table, String schema,
			BasicDataSource datasource) throws TableCreationException {
		super(schema, datasource);
		this.table = schema + "." + "`" + table + "`";
		initQueries();

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute(creationQuery);
			stmt.execute(indexingQuery);
			conn.commit();
		} catch (final SQLException e) {
			if (e.getErrorCode() != 1061) {
				throw new TableCreationException(e);
			}
		}
	}

	/*
	 * (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#addUser(com.servicebook
	 * .database.primitives.DBUser)
	 */
	@Override
	public void addUser(DBUser user) throws ElementAlreadyExistsException,
			DatabaseUnkownFailureException, InvalidParamsException {
		if (user == null || user.getUsername() == null
				|| user.getPassword() == null || user.getName() == null) {
			throw new InvalidParamsException();
		}

		try (Connection conn = getConnection();
				PreparedStatement prpdStmt = conn
						.prepareStatement(insertionQuery)) {
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			prpdStmt.setString(1, user.getUsername());
			prpdStmt.setString(2, user.getPassword());
			prpdStmt.setString(3, user.getName());
			prpdStmt.setInt(4, user.getBalance());
			prpdStmt.executeUpdate();

			conn.commit();
		} catch (final SQLException e) {
			if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
				throw new ElementAlreadyExistsException(e);
			}

			throw new DatabaseUnkownFailureException(e);
		}
	}

	/*
	 * (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#getUser(java.lang.String)
	 */
	@Override
	public DBUser getUser(String username)
			throws DatabaseUnkownFailureException, InvalidParamsException {
		if (username == null) {
			throw new InvalidParamsException();
		}
		DBUser $ = null;

		try (Connection conn = getConnection();
				PreparedStatement prpdStmt = conn
						.prepareStatement(gettingUserQuery)) {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			prpdStmt.setString(1, username);
			final ResultSet res = prpdStmt.executeQuery();
			if (res.next()) {
				$ = new DBUser(res.getString(Columns.USERNAME.toString()
						.toLowerCase()), res.getString(Columns.PASSWORD
						.toString().toLowerCase()), res.getString(Columns.NAME
						.toString().toLowerCase()), res.getInt(Columns.BALANCE
						.toString().toLowerCase()));
			}

			res.close();

			conn.commit();
		} catch (final SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	/*
	 * (non-Javadoc) @see com.servicebook.database.UsersDatabase#getUsers(int,
	 * int)
	 */
	@Override
	public List<DBUser> getUsers(int start, int amount)
			throws DatabaseUnkownFailureException, InvalidParamsException {
		if (start < 0 || amount < 0) {
			throw new InvalidParamsException();
		}
		List<DBUser> $ = null;

		try (Connection conn = getConnection();
				PreparedStatement prpdStmt = conn
						.prepareStatement(gettingMultipleUsersQuery)) {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

			$ = getUsers(start, amount, conn);

			conn.commit();
		} catch (final SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	@Override
	public List<DBUser> getUsers(int start, int amount, Connection conn)
			throws DatabaseUnkownFailureException, InvalidParamsException {
		if (start < 0 || amount < 0 || isConnClosed(conn)) {
			throw new InvalidParamsException();
		}
		ArrayList<DBUser> $ = new ArrayList<DBUser>();

		try (PreparedStatement prpdStmt = conn
				.prepareStatement(gettingMultipleUsersQuery)) {
			prpdStmt.setInt(1, start);
			prpdStmt.setInt(2, amount);
			final ResultSet res = prpdStmt.executeQuery();
			while (res.next()) {
				final DBUser u = new DBUser(
						res.getString(Columns.USERNAME.toString().toLowerCase()),
						res.getString(Columns.PASSWORD.toString().toLowerCase()),
						res.getString(Columns.NAME.toString().toLowerCase()),
						res.getInt(Columns.BALANCE.toString().toLowerCase()));

				$.add(u);
			}
			res.close();
		} catch (final SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	/*
	 * (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#isUserExists(java.lang.String)
	 */
	@Override
	public boolean isUserExists(String username, Connection conn)
			throws DatabaseUnkownFailureException, InvalidParamsException {
		if (username == null || isConnClosed(conn)) {
			throw new InvalidParamsException();
		}
		boolean $ = false;

		try (PreparedStatement prpdStmt = conn
				.prepareStatement(isUserExistsQuery)) {
			prpdStmt.setString(1, username);
			final ResultSet res = prpdStmt.executeQuery();
			if (res.next()) {
				$ = true;
			}
			res.close();
		} catch (final SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	/*
	 * (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#validateUser(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean validateUser(String username, String password)
			throws InvalidParamsException, DatabaseUnkownFailureException {
		if (username == null || password == null) {
			throw new InvalidParamsException();
		}
		boolean $ = false;

		try (Connection conn = getConnection();
				PreparedStatement prpdStmt = conn
						.prepareStatement(validationQuery)) {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			prpdStmt.setString(1, username);
			prpdStmt.setString(2, password);
			final ResultSet res = prpdStmt.executeQuery();

			if (res.next()) {
				$ = true;
			}

			res.close();

			conn.commit();
		} catch (final SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	/*
	 * (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#updateBalance(java.sql.Connection,
	 * java.lang.String, int)
	 */
	@Override
	public void updateBalance(Connection conn, String username, int balance)
			throws InvalidParamsException, DatabaseUnkownFailureException {
		if (conn == null || username == null) {
			throw new InvalidParamsException();
		}
		if (isConnClosed(conn)) {
			throw new InvalidParamsException();
		}
		try (PreparedStatement prpdStmt = conn
				.prepareStatement(updateBalanceQuery)) {
			prpdStmt.setInt(1, balance);
			prpdStmt.setString(2, username);
			prpdStmt.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}
	}

	@Override
	public boolean isUsernameTaken(String username)
			throws InvalidParamsException, DatabaseUnkownFailureException {
		if (username == null) {
			throw new InvalidParamsException();
		}
		boolean $ = false;

		try (Connection conn = getConnection();
				PreparedStatement prpdStmt = conn
						.prepareStatement(gettingUserQuery)) {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			prpdStmt.setString(1, username);
			final ResultSet res = prpdStmt.executeQuery();
			if (res.next()) {
				$ = true;
			}
			res.close();

			conn.commit();
		} catch (final SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	/*
	 * (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#deleteUser(java.sql.Connection,
	 * java.lang.String)
	 */
	@Override
	public void deleteUser(Connection conn, String username)
			throws InvalidParamsException, DatabaseUnkownFailureException {
		if (conn == null || username == null) {
			throw new InvalidParamsException();
		}
		if (isConnClosed(conn)) {
			throw new InvalidParamsException();
		}
		try (PreparedStatement prpdStmt = conn
				.prepareStatement(userDeletionQuery)) {
			prpdStmt.setString(1, username);
			prpdStmt.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}
	}

	/* (non-Javadoc) @see com.servicebook.database.UsersDatabase#getUsersCount() */
	@Override
	public int getUsersCount() throws DatabaseUnkownFailureException {
		int $ = 0;

		String sqlExpression = "SELECT COUNT(*) FROM " + table;
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement()) {
			ResultSet res = stmt.executeQuery(sqlExpression);
			if (res.next()) {
				$ = res.getInt(1);
			}
		} catch (SQLException e) {
			throw new DatabaseUnkownFailureException(e);
		}

		return $;
	}

	/**
	 * After setting the table scheme and name the method will be called for
	 * initialising the query string
	 */
	@SuppressWarnings("nls")
	private void initQueries() {
		creationQuery = String
				.format("CREATE TABLE IF NOT EXISTS %s (`%s` VARCHAR(255) NOT NULL, `%s` VARCHAR(255) NOT NULL, `%s` VARCHAR(255) NOT NULL, `%s` INT NOT NULL, PRIMARY KEY (`%s`))"
				/* + "ENGINE = MyISAM" */, table, Columns.USERNAME.toString()
						.toLowerCase(), Columns.PASSWORD.toString()
						.toLowerCase(), Columns.NAME.toString().toLowerCase(),
						Columns.BALANCE.toString().toLowerCase(),
						Columns.USERNAME.toString().toLowerCase());

		indexingQuery = String.format(
				"CREATE INDEX indexed_username ON %s (`%s`) USING HASH", table,
				Columns.USERNAME.toString().toLowerCase());

		insertionQuery = String.format(
				"INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)", table,
				Columns.USERNAME.toString().toLowerCase(), Columns.PASSWORD
						.toString().toLowerCase(), Columns.NAME.toString()
						.toLowerCase(), Columns.BALANCE.toString()
						.toLowerCase());

		gettingUserQuery = String.format("SELECT * FROM %s WHERE %s = ?",
				table, Columns.USERNAME.toString().toLowerCase());

		isUserExistsQuery = String.format("SELECT * FROM %s WHERE %s = ?",
				table, Columns.USERNAME.toString().toLowerCase());

		validationQuery = String.format(
				"SELECT * FROM %s WHERE %s = ? AND %s = ?", table,
				Columns.USERNAME.toString().toLowerCase(), Columns.PASSWORD
						.toString().toLowerCase());

		gettingMultipleUsersQuery = String.format(
				"SELECT * FROM %s ORDER BY `%s` LIMIT ?, ?", table,
				Columns.NAME.toString().toLowerCase());

		updateBalanceQuery = String.format(
				"UPDATE %s SET %s = %s+? WHERE %s = ?;", table, Columns.BALANCE
						.toString().toLowerCase(), Columns.BALANCE.toString()
						.toLowerCase(), Columns.USERNAME.toString()
						.toLowerCase());

		userDeletionQuery = String.format("DELETE FROM %s WHERE %s = ?", table,
				Columns.USERNAME.toString().toLowerCase());
	}

	/**
	 * The table name. Including the scheme name
	 */
	private final String table;

	/**
	 * The statement which will handle creation of the table
	 */
	private String creationQuery;

	/**
	 * The statement which will handle indexing of the database
	 */
	private String indexingQuery;

	/**
	 * The statement which will handle inserting to the database
	 */
	private String insertionQuery;

	/**
	 * The statement which will handle selecting single user from the database
	 */
	private String gettingUserQuery;

	/**
	 * The statement which will handle validation of a user
	 */
	private String validationQuery;

	/**
	 * The statement which will handle getting some amount of users
	 */
	private String gettingMultipleUsersQuery;

	private String updateBalanceQuery;

	private String isUserExistsQuery;

	private String userDeletionQuery;

}
