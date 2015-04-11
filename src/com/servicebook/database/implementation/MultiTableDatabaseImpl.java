
package com.servicebook.database.implementation;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.MultiTableDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.UsersDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.friendships.ReflexiveFriendshipException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;
import com.servicebook.database.exceptions.paidActivities.ElementNotExistException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.primitives.DBPaidActivity;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;




public class MultiTableDatabaseImpl extends AbstractMySqlDatabase
	implements
		MultiTableDatabase
{
	
	public MultiTableDatabaseImpl(
		String schema,
		BasicDataSource datasource,
		FriendshipsDatabase friendsDB,
		PaidActivitiesDatabase activitiesDB,
		UsersDatabase usersDB)
	{
		super(schema, datasource);
		this.friendsDB = friendsDB;
		this.activitiesDB = activitiesDB;
		this.usersDB = usersDB;
	}
	
	
	@Override
	public boolean registerToActivity(int id, String username)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (!isValidId(id) || username == null) { throw new InvalidParameterException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			final DBPaidActivity activity = activitiesDB.getActivity(id, conn);
			if (activity == null)
			{
				// activity doesn't exist
				return false;
			}
			assert activity.getCapacity() >= activity.getNumRegistered();
			if (activity.isFull()) { return false; }
			if (!friendsDB.areFriends(username, activity.getUsername(), conn)) { return false; }
			// Those are just asserts, will not run in release
			try
			{
				assert usersDB.isUserExists(username, conn);
				assert usersDB.isUserExists(activity.getUsername(), conn);
			} catch (
				DatabaseUnkownFailureException
				| com.servicebook.database.exceptions.users.InvalidParamsException e)
			{
				e.printStackTrace();
			}
			
			activitiesDB.registerToActivity(id, username, conn);
			
			final int toAdd = 1;
			if (activity instanceof DBPaidService)
			{
				usersDB.updateBalance(conn, username, -toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), toAdd);
			} else if (activity instanceof DBPaidTask)
			{
				usersDB.updateBalance(conn, username, toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), -toAdd);
			} else
			{
				assert false;
			}
			
			conn.commit();
		} catch (final com.servicebook.database.exceptions.paidActivities.ElementAlreadyExistException e)
		{
			// User is already registered to this activity
			return false;
		} catch (
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException
			| com.servicebook.database.exceptions.friendships.InvalidParamsException
			| com.servicebook.database.exceptions.users.InvalidParamsException e1)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
		
		return true;
	}
	
	
	@Override
	public boolean unregisterFromActivity(int id, String username)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (!isValidId(id) || username == null) { throw new InvalidParameterException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			final DBPaidActivity activity = activitiesDB.getActivity(id, conn);
			if (activity == null)
			{
				// no such activity
				return false;
			}
			try
			{
				// assert (usersDB.isUserExists(username, conn));
				assert usersDB.isUserExists(activity.getUsername(), conn);
			} catch (
				DatabaseUnkownFailureException
				| com.servicebook.database.exceptions.users.InvalidParamsException e)
			{
				e.printStackTrace();
			}
			
			activitiesDB.unregisterFromActivity(id, username, conn);
			
			final int toAdd = 1;
			if (activity instanceof DBPaidService)
			{
				usersDB.updateBalance(conn, username, toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), -toAdd);
			} else if (activity instanceof DBPaidTask)
			{
				usersDB.updateBalance(conn, username, -toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), toAdd);
			} else
			{
				assert false;
			}
			
			conn.commit();
		} catch (final ElementNotExistException e)
		{
			// User was not registered to the activity
			return false;
		} catch (
			com.servicebook.database.exceptions.users.InvalidParamsException
			| com.servicebook.database.exceptions.paidActivities.InvalidParameterException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
		
		return true;
	}
	
	
	@Override
	public void deleteUser(String username)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (username == null) { throw new InvalidParameterException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			if (!usersDB.isUserExists(username, conn))
			{
				// Already not exist
				return;
			}
			
			// Delete all his activities and registrations
			activitiesDB.deleteUserPaidActivities(username, conn);
			activitiesDB.deleteUserRegistrations(username, conn);
			
			// Delete all his friendships
			friendsDB.deleteFriendships(username, conn);
			
			// Fatality! Delete the user! Muhahaha
			usersDB.deleteUser(conn, username);
			
			conn.commit();
		} catch (
			com.servicebook.database.exceptions.friendships.InvalidParamsException
			| com.servicebook.database.exceptions.users.InvalidParamsException
			| com.servicebook.database.exceptions.paidActivities.InvalidParameterException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
	}
	
	
	@Override
	public int addPaidService(DBPaidService service)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		return addPaidActivity(service);
	}
	
	
	@Override
	public int addPaidTask(DBPaidTask task)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		return addPaidActivity(task);
	}
	
	
	@Override
	public void deleteActivity(int id)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		if (!isValidId(id)) { throw new InvalidParameterException(); }
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			activitiesDB.deletePaidActivity(id, conn);
			conn.commit();
		} catch (final com.servicebook.database.exceptions.paidActivities.InvalidParameterException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
	}
	
	
	@Override
	public void addFriendship(String username1, String username2)
		throws ElementAlreadyExistsException,
		DatabaseUnkownFailureException,
		InvalidParameterException,
		ReflexiveFriendshipException
	{
		if (username1 == null || username2 == null) { throw new InvalidParameterException(); }
		if (username1.equalsIgnoreCase(username2)) { throw new ReflexiveFriendshipException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			if (!usersDB.isUserExists(username1, conn)
				|| !usersDB.isUserExists(username2, conn)) { throw new InvalidParameterException(); }
			
			friendsDB.addFriendship(username1, username2, conn);
			
			conn.commit();
		} catch (
			com.servicebook.database.exceptions.friendships.InvalidParamsException
			| com.servicebook.database.exceptions.users.InvalidParamsException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
	}
	
	
	
	private final FriendshipsDatabase friendsDB;
	
	private final PaidActivitiesDatabase activitiesDB;
	
	private final UsersDatabase usersDB;
	
	
	
	private int addPaidActivity(DBPaidActivity activity)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (activity == null
			|| activity.getUsername() == null
			|| activity.getCapacity() <= 0
			|| activity.getDistance() <= 0) { throw new InvalidParameterException(); }
		
		int $ = -1;
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			if (!usersDB.isUserExists(activity.getUsername(), conn)) { throw new InvalidParameterException(); }
			
			if (activity instanceof DBPaidService)
			{
				$ = activitiesDB.addPaidService((DBPaidService) activity, conn);
			} else if (activity instanceof DBPaidTask)
			{
				$ = activitiesDB.addPaidTask((DBPaidTask) activity, conn);
			}
			
			conn.commit();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		} catch (
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException
			| InvalidParamsException e)
		{
			throw new InvalidParameterException();
		}
		return $;
	}
	
	
	private boolean isValidId(int id)
	{
		return id > 0;
	}
	
}
