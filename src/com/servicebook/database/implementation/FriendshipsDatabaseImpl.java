/**
 * 
 */

package com.servicebook.database.implementation;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.AbstractMySqlDatabase.SQLErrorCodes;
import com.servicebook.database.exceptions.friendships.TableCreationException;
import com.servicebook.database.primitives.DBUser;




/**
 * @author Shmulik
 *
 */
public class FriendshipsDatabaseImpl extends AbstractMySqlDatabase
	implements
		FriendshipsDatabase
{
	
	private enum Columns
	{
		FIRST_USERNAME,
		SECOND_USERNAME
	}
	
	
	
	/**
	 * @param schema
	 * @param datasource
	 * @throws TableCreationException 
	 */
	public FriendshipsDatabaseImpl(
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
			conn.commit();
		} catch (final SQLException e)
		{
			if (e.getErrorCode() != SQLErrorCodes.CREATION_ERROR.getCode())
			{
				throw new TableCreationException(e);
			}
		}
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.FriendshipsDatabase#addFriendship
	 * (com.servicebook.database.primitives.DBUser,
	 * com.servicebook.database.primitives.DBUser) */
	@Override
	public void addFriendship(DBUser user1, DBUser user2)
	{
		// TODO Auto-generated method stub
		
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.FriendshipsDatabase#addFriendship
	 * (java.lang.String, java.lang.String) */
	@Override
	public void addFriendship(String username1, String username2)
	{
		// TODO Auto-generated method stub
		
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.FriendshipsDatabase#getFriends(java.lang.String) */
	@Override
	public List<DBUser> getFriends(String username)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.FriendshipsDatabase#getFriends
	 * (com.servicebook.database.primitives.DBUser) */
	@Override
	public List<DBUser> getFriends(DBUser user)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void initQueries()
	{
		creationQuery =
			String
				.format(
					"CREATE TABLE IF NOT EXISTS %s (`%s` VARCHAR(255) NOT NULL, `%s` VARCHAR(255) NOT NULL, PRIMARY KEY(`%s`, `%s`))",
					table,
					Columns.FIRST_USERNAME.toString().toLowerCase(),
					Columns.SECOND_USERNAME.toString().toLowerCase(),
					Columns.FIRST_USERNAME.toString().toLowerCase(),
					Columns.SECOND_USERNAME.toString().toLowerCase());
		
		insertionQuery =
			String.format(
				"INSERT INTO %s (%s, %s) VALUES(?, ?)",
				table,
				Columns.FIRST_USERNAME.toString().toLowerCase(),
				Columns.SECOND_USERNAME.toString().toLowerCase());
		
		gettingQuery =
			String.format(
				"SELECT * FROM %s WHERE %s = ? OR %s = ?",
				table,
				Columns.FIRST_USERNAME.toString().toLowerCase(),
				Columns.SECOND_USERNAME.toString().toLowerCase());
	}
	
	
	
	private final String table;
	
	private String creationQuery;
	
	private String insertionQuery;
	
	private String gettingQuery;

	/* (non-Javadoc) @see com.servicebook.database.FriendshipsDatabase#addFriendship(com.servicebook.database.primitives.DBUser, com.servicebook.database.primitives.DBUser) */
	@Override
	public void addFriendship(DBUser user1, DBUser user2)
	{
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc) @see com.servicebook.database.FriendshipsDatabase#getFriends(com.servicebook.database.primitives.DBUser) */
	@Override
	public List<DBUser> getFriends(DBUser user)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
