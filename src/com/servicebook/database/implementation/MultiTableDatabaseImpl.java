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
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;
import com.servicebook.database.exceptions.paidActivities.ElementNotExistException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.primitives.DBPaidActivity;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

public class MultiTableDatabaseImpl extends AbstractMySqlDatabase implements
		MultiTableDatabase {

	public MultiTableDatabaseImpl(String schema, BasicDataSource datasource,
			FriendshipsDatabase friendsDB, PaidActivitiesDatabase activitiesDB,
			UsersDatabase usersDB) {
		super(schema, datasource);
		this.friendsDB = friendsDB;
		this.activitiesDB = activitiesDB;
		this.usersDB = usersDB;
	}

	@Override
	public boolean registerToActivity(int id, String username)
			throws InvalidParameterException, DatabaseUnkownFailureException {
		try (Connection conn = getConnection()) {
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			DBPaidActivity activity = activitiesDB.getActivity(id, conn);
			if (activity == null) {
				// activity doesn't exist
				return false;
			}
			assert (activity.getCapacity() >= activity.getNumRegistered());
			if (activity.isFull()) {
				// activity is full
				return false;
			}
			if (!friendsDB.areFriends(username, activity.getUsername(), conn)) {
				// users are not friends
				return false;
			}
			// Those are just asserts, will not run in release
			try {
				assert (usersDB.isUserExists(username));
				assert (usersDB.isUserExists(activity.getUsername()));
			} catch (
					DatabaseUnkownFailureException
					| com.servicebook.database.exceptions.users.InvalidParamsException e) {
				// TODO Assert auto generated catch block.
				e.printStackTrace();
			}

			activitiesDB.registerToActivity(id, username, conn);

			int toAdd = 1;
			if (activity instanceof DBPaidService) {
				usersDB.updateBalance(conn, username, -toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), toAdd);
			} else if (activity instanceof DBPaidTask) {
				usersDB.updateBalance(conn, username, toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), -toAdd);
			} else {
				assert false;
			}

			conn.commit();
		} catch (com.servicebook.database.exceptions.paidActivities.ElementAlreadyExistException e) {
			// User is already registered to this activity
			return false;
		} catch (
				com.servicebook.database.exceptions.paidActivities.InvalidParameterException
				| com.servicebook.database.exceptions.users.InvalidParamsException e1) {
			throw new InvalidParameterException();
		} catch (SQLException e) {
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}

		return true;
	}

	@Override
	public boolean unregisterFromActivity(int id, String username)
			throws InvalidParameterException, DatabaseUnkownFailureException {
		try (Connection conn = getConnection()) {
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			DBPaidActivity activity = activitiesDB.getActivity(id, conn);
			if (activity == null) {
				// no such activity
				return false;
			}

			activitiesDB.unregisterFromActivity(id, username, conn);

			int toAdd = 1;
			if (activity instanceof DBPaidService) {
				usersDB.updateBalance(conn, username, toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), -toAdd);
			} else if (activity instanceof DBPaidTask) {
				usersDB.updateBalance(conn, username, -toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), toAdd);
			} else {
				assert false;
			}

			conn.commit();
		} catch (ElementNotExistException e) {
			// User was not registered to the activity
			return false;
		} catch (
				com.servicebook.database.exceptions.users.InvalidParamsException
				| com.servicebook.database.exceptions.paidActivities.InvalidParameterException e) {
			throw new InvalidParameterException();
		} catch (SQLException e) {
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}

		return true;
	}

	@Override
	public void deleteUser(String username) throws InvalidParameterException,
			DatabaseUnkownFailureException {
		try (Connection conn = getConnection()) {
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			// Delete all his activities and registrations
			activitiesDB.deleteUserPaidActivities(username, conn);
			activitiesDB.deleteUserRegistrations(username, conn);

			// Delete all his friendships
			friendsDB.deleteFriendships(username, conn);

			// Fatality! Delete the user! Muhahaha
			usersDB.deleteUser(conn, username);

			conn.commit();
		} catch (
				InvalidParamsException
				| com.servicebook.database.exceptions.paidActivities.InvalidParameterException e) {
			throw new InvalidParameterException();
		} catch (SQLException e) {
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
	}

	@Override
	public void deleteActivity(int id)
			throws DatabaseUnkownFailureException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException {
		try (Connection conn = getConnection()) {
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			activitiesDB.deletePaidActivity(id, conn);
			conn.commit();
		} catch (SQLException e) {
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
	}

	private FriendshipsDatabase friendsDB;

	private PaidActivitiesDatabase activitiesDB;

	private UsersDatabase usersDB;

}
