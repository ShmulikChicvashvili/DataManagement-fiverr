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
import com.servicebook.database.exceptions.friendships.InvalidParamsException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;
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
				// TODO Auto-generated catch block
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
		} catch (DatabaseUnkownFailureException e) {
			// Who knows why... it's unknown
			throw e;
		}

		return true;
	}

	@Override
	public boolean unregisterFromActivity(int id, String username)
			throws InvalidParameterException, DatabaseUnkownFailureException {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void deleteUser(String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteActivity(int id) {
		// TODO Auto-generated method stub

	}

	private FriendshipsDatabase friendsDB;

	private PaidActivitiesDatabase activitiesDB;

	private UsersDatabase usersDB;

}
