/**
 *
 */

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
import com.servicebook.database.UsersDatabase;
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
	 */
	@SuppressWarnings("nls")
	public UsersDatabaseImpl(
		String table,
		String schema,
		BasicDataSource datasource)
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
		} catch (final SQLException e)
		{
			// TODO Decide what to do in case of failure in the creation or
			// indexing
		}
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#addUser(com.servicebook
	 * .database.primitives.DBUser) */
	@Override
	public void addUser(DBUser user)
	{
		if (user == null
			|| user.getUsername() == null
			|| user.getPassword() == null
			|| user.getName() == null) { return; }

		try (
			Connection conn = getConnection();
			PreparedStatement prpdStmt = conn.prepareStatement(insertionQuery))
		{
			prpdStmt.setString(1, user.getUsername());
			prpdStmt.setString(2, user.getPassword());
			prpdStmt.setString(3, user.getName());
			prpdStmt.setInt(4, user.getBalance());
			prpdStmt.executeUpdate();
		} catch (final SQLException e)
		{
			// TODO Decide what to do in case of failure in inserting of a user
		}
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#getUser(java.lang.String) */
	@Override
	public DBUser getUser(String username)
	{
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
			
		} catch (final SQLException e)
		{
			// TODO Decide what to do in case of failure in selecting single
			// user
		}

		return $;
	}
	
	
	/* (non-Javadoc) @see com.servicebook.database.UsersDatabase#getUsers(int,
	 * int) */
	@Override
	public List<DBUser> getUsers(int start, int amount)
	{
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
		} catch (final SQLException e)
		{
			// TODO Decide what to do in case of failure in getting multiple
			// users
		}

		return $;
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#isUserExists(java.lang.String) */
	@Override
	public boolean isUserExists(String username)
	{
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
		} catch (final SQLException e)
		{
			// TODO Decide what to do in case of failure in selecting
		}
		
		return $;
	}


	/* (non-Javadoc) @see
	 * com.servicebook.database.UsersDatabase#validateUser(java.lang.String,
	 * java.lang.String) */
	@Override
	public boolean validateUser(String username, String password)
	{
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
		} catch (final SQLException e)
		{
			// TODO Decide what to do in case of failure in validation query
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
					"CREATE TABLE IF NOT EXISTS %s (`%s` VARCHAR(255) NOT NULL, `%s` VARCHAR(255) NOT NULL, `%s` VARCHAR(255), `%s` INT, PRIMARY KEY (`%s`))",
					table,
					Columns.USERNAME.toString().toLowerCase(),
					Columns.PASSWORD.toString().toLowerCase(),
					Columns.NAME.toString().toLowerCase(),
					Columns.BALANCE.toString().toLowerCase(),
					Columns.USERNAME.toString().toLowerCase());

		indexingQuery =
			String.format(
				"CREATE INDEX indexed_username ON `%s` (`%s`) USING HASH",
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
			String.format("SELECT * FROM %s LIMIT ?, ?", table);
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
