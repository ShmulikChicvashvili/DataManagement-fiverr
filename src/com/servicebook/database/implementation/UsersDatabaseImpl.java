/**
 *
 */

package com.servicebook.database.implementation;


import java.io.PrintWriter;
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
public class UsersDatabaseImpl extends AbstractMySqlDatabase
	implements
		UsersDatabase
{
	
	/**
	 * @author Shmulik
	 *
	 *         The columns of users database
	 */
	private enum Columns
	{
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
		BALANCE
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
	public UsersDatabaseImpl(
		String table,
		String schema,
		BasicDataSource datasource) throws TableCreationException
	{
		super(schema, datasource);
		this.table = schema + "." + "`" + table + "`";
		initQueries();
		
		try (
			Connection conn = getConnection();
			Statement stmt = conn.createStatement())
		{
			stmt.execute(creationQuery);
			stmt.execute(indexingQuery);
			conn.commit();
		} catch (final SQLException e)
		{
			if (e.getErrorCode() != 1061)
			{
				throw new TableCreationException(e);
			}
		}
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#addUser(com.servicebook
	 * .database.primitives.DBUser) */
	@Override
	public void addUser(DBUser user)
		throws ElementAlreadyExistsException,
		DatabaseUnkownFailureException,
		InvalidParamsException
	{
		if (user == null
			|| user.getUsername() == null
			|| user.getPassword() == null
			|| user.getName() == null) { throw new InvalidParamsException(); }
		
		try (
			Connection conn = getConnection();
			PreparedStatement prpdStmt = conn.prepareStatement(insertionQuery))
		{
			prpdStmt.setString(1, user.getUsername());
			prpdStmt.setString(2, user.getPassword());
			prpdStmt.setString(3, user.getName());
			prpdStmt.setInt(4, user.getBalance());
			prpdStmt.executeUpdate();
			
			conn.commit();
		} catch (final SQLException e)
		{
			if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY)
			{
				throw new ElementAlreadyExistsException(e);
			}
			
			throw new DatabaseUnkownFailureException(e);
		}
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#getUser(java.lang.String) */
	@Override
	public DBUser getUser(String username)
		throws DatabaseUnkownFailureException,
		InvalidParamsException
	{
		if (username == null) { throw new InvalidParamsException(); }
		DBUser $ = null;
		
		try (
			Connection conn = getConnection();
			PreparedStatement prpdStmt =
				conn.prepareStatement(gettingUserQuery))
		{
			prpdStmt.setString(1, username);
			final ResultSet res = prpdStmt.executeQuery();
			if (res.next())
			{
				$ =
					new DBUser(res.getString(Columns.USERNAME
						.toString()
						.toLowerCase()), res.getString(Columns.PASSWORD
						.toString()
						.toLowerCase()), res.getString(Columns.NAME
						.toString()
						.toLowerCase()), res.getInt(Columns.BALANCE
						.toString()
						.toLowerCase()));
			}
			
			res.close();
			
			conn.commit();
			
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
		
		return $;
	}
	
	
	/* (non-Javadoc) @see com.servicebook.database.UsersDatabase#getUsers(int,
	 * int) */
	@Override
	public List<DBUser> getUsers(int start, int amount)
		throws DatabaseUnkownFailureException,
		InvalidParamsException
	{
		if (start < 0 || amount < 0) { throw new InvalidParamsException(); }
		final ArrayList<DBUser> $ = new ArrayList<>();
		
		try (
			Connection conn = getConnection();
			PreparedStatement prpdStmt =
				conn.prepareStatement(gettingMultipleUsersQuery))
		{
			prpdStmt.setInt(1, start);
			prpdStmt.setInt(2, amount);
			final ResultSet res = prpdStmt.executeQuery();
			while (res.next())
			{
				final DBUser u =
					new DBUser(res.getString(Columns.USERNAME
						.toString()
						.toLowerCase()), res.getString(Columns.PASSWORD
						.toString()
						.toLowerCase()), res.getString(Columns.NAME
						.toString()
						.toLowerCase()), res.getInt(Columns.BALANCE
						.toString()
						.toLowerCase()));
				
				$.add(u);
			}
			res.close();
			
			conn.commit();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
		
		return $;
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#isUserExists(java.lang.String) */
	@Override
	public boolean isUserExists(String username)
		throws DatabaseUnkownFailureException,
		InvalidParamsException
	{
		if (username == null) { throw new InvalidParamsException(); }
		boolean $ = false;
		
		try (
			Connection conn = getConnection();
			PreparedStatement prpdStmt =
				conn.prepareStatement(gettingUserQuery))
		{
			prpdStmt.setString(1, username);
			final ResultSet res = prpdStmt.executeQuery();
			if (res.next())
			{
				$ = true;
			}
			res.close();
			
			conn.commit();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
		
		return $;
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#validateUser(java.lang.String,
	 * java.lang.String) */
	@Override
	public boolean validateUser(String username, String password)
		throws InvalidParamsException,
		DatabaseUnkownFailureException
	{
		if (username == null || password == null) { throw new InvalidParamsException(); }
		boolean $ = false;
		
		try (
			Connection conn = getConnection();
			PreparedStatement prpdStmt = conn.prepareStatement(validationQuery))
		{
			
			prpdStmt.setString(1, username);
			prpdStmt.setString(2, password);
			final ResultSet res = prpdStmt.executeQuery();
			
			if (res.next())
			{
				$ = true;
			}
			
			res.close();
			
			conn.commit();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
		
		return $;
	}
	
	
	/**
	 * After setting the table scheme and name the method will be called for
	 * initialising the query string
	 */
	@SuppressWarnings("nls")
	private void initQueries()
	{
		creationQuery =
			String
				.format(
					"CREATE TABLE IF NOT EXISTS %s (`%s` VARCHAR(255) NOT NULL, `%s` VARCHAR(255) NOT NULL, `%s` VARCHAR(255) NOT NULL, `%s` INT NOT NULL, PRIMARY KEY (`%s`))"
						+ "ENGINE = MyISAM",
					table,
					Columns.USERNAME.toString().toLowerCase(),
					Columns.PASSWORD.toString().toLowerCase(),
					Columns.NAME.toString().toLowerCase(),
					Columns.BALANCE.toString().toLowerCase(),
					Columns.USERNAME.toString().toLowerCase());
		
		indexingQuery =
			String.format(
				"CREATE INDEX indexed_username ON %s (`%s`) USING HASH",
				table,
				Columns.USERNAME.toString().toLowerCase());
		
		insertionQuery =
			String.format(
				"INSERT INTO %s (%s, %s, %s, %s) VALUES(?, ?, ?, ?)",
				table,
				Columns.USERNAME.toString().toLowerCase(),
				Columns.PASSWORD.toString().toLowerCase(),
				Columns.NAME.toString().toLowerCase(),
				Columns.BALANCE.toString().toLowerCase());
		
		gettingUserQuery =
			String.format(
				"SELECT * FROM %s WHERE %s = ?",
				table,
				Columns.USERNAME.toString().toLowerCase());
		
		validationQuery =
			String.format(
				"SELECT * FROM %s WHERE %s = ? AND %s = ?",
				table,
				Columns.USERNAME.toString().toLowerCase(),
				Columns.PASSWORD.toString().toLowerCase());
		
		gettingMultipleUsersQuery =
			String.format(
				"SELECT * FROM %s ORDER BY `%s` LIMIT ?, ?",
				table,
				Columns.NAME.toString().toLowerCase());
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
	
}
