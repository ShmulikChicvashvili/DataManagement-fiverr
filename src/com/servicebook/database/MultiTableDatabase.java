package com.servicebook.database;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;

// TODO: Auto-generated Javadoc
/**
 * The Interface MultiTableDatabase.
 */
public interface MultiTableDatabase {

	/**
	 * Register a user to an activity. Updates the balance for the users.
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
	 * Unregister from activity. Updates the balance for the users.
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
	 * @throws InvalidParameterException 
	 * @throws DatabaseUnkownFailureException 
	 */
	public void deleteUser(String username) throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Delete an activity.
	 *
	 * @param id
	 *            the id for the activity to be deleted.
	 * @throws com.servicebook.database.exceptions.paidActivities.InvalidParameterException 
	 * @throws DatabaseUnkownFailureException 
	 */
	public void deleteActivity(int id) throws DatabaseUnkownFailureException, com.servicebook.database.exceptions.paidActivities.InvalidParameterException;
}
