package com.servicebook.database;

import org.w3c.dom.Document;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.friendships.ReflexiveFriendshipException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

/**
 * The Interface MultiTableDatabase.
 */
public interface MultiTableDatabase {

	/**
	 * Register a user to an activity. Updates the balance for the users. The
	 * activity and the user must exist, and the user and the creator for the
	 * activity must be friends. Also the user must not have been registered to
	 * this activity before.
	 *
	 * @param id
	 *            the id for the activity
	 * @param username
	 *            the username to be registered into
	 * @return <code>true</code>, if successful, <code>false</code> In any of
	 *         the forbidden cases described above.
	 * @throws InvalidParameterException
	 *             The id is negative or username is null
	 * @throws DatabaseUnkownFailureException
	 */
	public boolean registerToActivity(int id, String username)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Unregister from activity. Updates the balance for the users. The user
	 * must have been registered to this activity.
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
	 *             if the user is null
	 * @throws DatabaseUnkownFailureException
	 *             In case of an unexpected SQL error.
	 */
	public void deleteUser(String username) throws InvalidParameterException,
			DatabaseUnkownFailureException;

	/**
	 * Adds the paid service to the database.
	 *
	 * @param service
	 *            the service to be added.
	 * @throws InvalidParameterException
	 *             In case the activity is null, or capacity or distance are non
	 *             positive. Also in the case the user does not exist.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unexpected SQL error occurs
	 */
	public int addPaidService(final DBPaidService service)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Adds the paid task to the database.
	 *
	 * @param task
	 *            the task to be added.
	 * @throws InvalidParameterException
	 *             In case the activity is null, or capacity or distance are non
	 *             positive. Also in the case the user does not exist.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unexpected SQL error occurs
	 */
	public int addPaidTask(final DBPaidTask task)
			throws DatabaseUnkownFailureException, InvalidParameterException;

	/**
	 * Delete an activity.
	 *
	 * @param id
	 *            the id for the activity to be deleted.
	 * @throws DatabaseUnkownFailureException
	 *             the database unknown failure exception
	 * @throws InvalidParameterException
	 *             the id is negative.
	 */
	public void deleteActivity(int id) throws DatabaseUnkownFailureException,
			InvalidParameterException;

	/**
	 * Adds a friendship relationship to the database. users must differ by
	 * their username. Does not validate users exist.
	 *
	 * @param username1
	 *            the first user
	 * @param username2
	 *            the second user
	 * @throws ElementAlreadyExistsException
	 *             the users are already friends
	 * @throws DatabaseUnkownFailureException
	 *             the database unkown failure exception
	 * @throws InvalidParameterException
	 *             the users are null, or one of them does not exist.
	 * @throws ReflexiveFriendshipException
	 *             in case the usernames are equal
	 */
	public void addFriendship(String username1, String username2)
			throws ElementAlreadyExistsException,
			DatabaseUnkownFailureException, InvalidParameterException,
			ReflexiveFriendshipException;

	public Document toXML(String username)
			throws DatabaseUnkownFailureException, InvalidParameterException;
}
