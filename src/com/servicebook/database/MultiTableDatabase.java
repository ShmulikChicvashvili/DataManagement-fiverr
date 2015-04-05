package com.servicebook.database;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;

// TODO: Auto-generated Javadoc
/**
 * The Interface MultiTableDatabase.
 */
public interface MultiTableDatabase {

	/**
	 * Register a user to an activity.
	 *
	 * @param id
	 *            the id for the activity
	 * @param username
	 *            the username to be registered into
	 * @return true, if successful, false otherwise
	 * @throws InvalidParameterException
	 *             The id is negative or username is null
	 * @throws DatabaseUnkownFailureException
	 */
	public boolean registerToActivity(int id, String username)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Unregister from activity.
	 *
	 * @param id
	 *            the id for the activity
	 * @param username
	 *            the username to be unregistered from the activity
	 * @return true, if successful, false otherwise
	 * @throws InvalidParameterException
	 *             The id is negative or username is null
	 * @throws DatabaseUnkownFailureException
	 */
	public boolean unregisterFromActivity(int id, String username)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Delete a user.
	 *
	 * @param username
	 *            the username to be deleted.
	 */
	public void deleteUser(String username);

	/**
	 * Delete an activity.
	 *
	 * @param id
	 *            the id for the activity to be deleted.
	 */
	public void deleteActivity(int id);
}
