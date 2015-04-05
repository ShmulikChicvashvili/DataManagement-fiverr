package com.servicebook.database;

import java.sql.Connection;
import java.util.List;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.paidActivities.ElementAlreadyExistException;
import com.servicebook.database.exceptions.paidActivities.ElementNotExistException;
import com.servicebook.database.exceptions.paidActivities.FriendshipsTableNotExist;
import com.servicebook.database.exceptions.paidActivities.InvalidParameterException;
import com.servicebook.database.primitives.DBPaidActivity;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

public interface PaidActivitiesDatabase {

	public enum ActivityStatus {
		NOT_EXIST, SERVICE, TASK
	}

	/**
	 * Adds the paid service to the database.
	 *
	 * @param service
	 *            the service to be added.
	 * @throws InvalidParameterException
	 *             In case the activity is null, or capacity or distance are non
	 *             positive.
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
	 *             positive.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unexpected SQL error occurs
	 */
	public int addPaidTask(final DBPaidTask task)
			throws DatabaseUnkownFailureException, InvalidParameterException;

	/**
	 * Deletes the paid activity and all registrations to it. does
	 * <b><u>not</u></b> update the balance for the participators.
	 *
	 * @param id
	 *            the id of the activity to be deleted.
	 * @param conn
	 *            the Sql connection to be used. Used for transactions.
	 * @throws InvalidParameterException
	 *             the id is negative or conn is closed.
	 * @throws DatabaseUnkownFailureException
	 *             the database unkown failure exception
	 */
	public void deletePaidActivity(int id, Connection conn)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Deletes all of the user's paid activities and all registrations to them.
	 * does <b><u>not</u></b> update the balance for the participators.
	 *
	 * @param username
	 *            the username whose activities should be deleted.
	 * @param conn
	 *            the Sql connection to be used. Used for transactions.
	 * @throws InvalidParameterException
	 *             the username is null or conn is closed.
	 * @throws DatabaseUnkownFailureException
	 *             the database unkown failure exception
	 */
	public void deleteUserPaidActivities(String username, Connection conn)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Gets the activity.
	 *
	 * @param id
	 *            the id of the activity. null if non existent
	 * @param conn
	 *            the conn to be used
	 * @return the desired activity. will be of type {@link DBPaidService} or
	 *         {@link DBPaidTask}.
	 * @throws InvalidParameterException
	 *             the id is negative or conn is closed
	 * @throws DatabaseUnkownFailureException
	 *             In case an unexpected SQL error occurs
	 */
	public DBPaidActivity getActivity(int id, Connection conn)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Gets the activity status in the database.
	 *
	 * @param id
	 *            the id for the activity
	 * @return the activity status - NOT_EXIST if no such activity exists. If it
	 *         does exist returns SERVICE or TASK according to its type.
	 * @throws InvalidParameterException
	 *             id is negative.
	 * @throws DatabaseUnkownFailureException
	 *             the database unkown failure exception
	 */
	public ActivityStatus getActivityStatus(int id)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Gets the activity status in the database.
	 *
	 * @param id
	 *            the id for the activity
	 * @param conn
	 *            the conn to be used (as part of a transaction).
	 * @return the activity status - NOT_EXIST if no such activity exists. If it
	 *         does exist returns SERVICE or TASK according to its type
	 * @throws InvalidParameterException
	 *             id is negative or conn is closed.
	 * @throws DatabaseUnkownFailureException
	 *             the database unkown failure exception
	 */
	public ActivityStatus getActivityStatus(int id, Connection conn)
			throws InvalidParameterException, DatabaseUnkownFailureException;

	/**
	 * Gets the services offered to the specified user. the services are ordered
	 * by their title.
	 *
	 * @param username
	 *            the user's username
	 * @param start
	 *            the starting index for the first service. The first service in
	 *            the database has index 0.
	 * @param amount
	 *            the maximum amount of services to return. must be at least 1.
	 * @return the services offered to the user, including the amount of
	 *         registered users to the service. If no services are offered an
	 *         empty set is returned.
	 * @throws InvalidParameterException
	 *             In case the username was null, start is negative or amount is
	 *             less than 1.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unknown SQL exception occurred.
	 * @throws FriendshipsTableNotExist
	 *             the friendships table does not exist
	 */
	public List<DBPaidService> getServicesOfferedToUser(final String username,
			int start, int amount) throws InvalidParameterException,
			DatabaseUnkownFailureException, FriendshipsTableNotExist;

	/**
	 * Gets the tasks offered to the specified user. the tasks are ordered by
	 * their title.
	 *
	 * @param username
	 *            the user's username
	 * @param start
	 *            the starting index for the first task. The first task in the
	 *            database has index 0.
	 * @param amount
	 *            the maximum amount of tasks to return. must be at least 1.
	 * @return the tasks offered to the user, including the amount of registered
	 *         users to the task. If no tasks are offered an empty set is
	 *         returned.
	 * @throws InvalidParameterException
	 *             In case the username was null, start is negative or amount is
	 *             less than 1.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unknown SQL exception occurred.
	 * @throws FriendshipsTableNotExist
	 *             the friendships table does not exist
	 */
	public List<DBPaidTask> getTasksOfferedToUser(final String username,
			int start, int amount) throws InvalidParameterException,
			DatabaseUnkownFailureException, FriendshipsTableNotExist;

	/**
	 * Gets the services offered by the specified user. the services are ordered
	 * by their title.
	 *
	 * @param username
	 *            the user's username
	 * @param start
	 *            the starting index for the first service. The first service in
	 *            the database has index 0.
	 * @param amount
	 *            the maximum amount of services to return. must be at least 1.
	 * @return the services offered by the user, including the amount of
	 *         registered users to the service.
	 * @throws InvalidParameterException
	 *             In case the username was null, start is negative or amount is
	 *             less than 1.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unknown SQL exception occurred.
	 */
	public List<DBPaidService> getServicesOfferedByUser(final String username,
			int start, int amount) throws InvalidParameterException,
			DatabaseUnkownFailureException;

	/**
	 * Gets the tasks offered by the specified user. the tasks are ordered by
	 * their title.
	 *
	 * @param username
	 *            the user's username
	 * @param start
	 *            the starting index for the first task. The first task in the
	 *            database has index 0.
	 * @param amount
	 *            the maximum amount of tasks to return. must be at least 1.
	 * @return the tasks offered by the user, including the amount of registered
	 *         users to the task.
	 * @throws InvalidParameterException
	 *             In case the username was null, start is negative or amount is
	 *             less than 1.
	 * @throws DatabaseUnkownFailureException
	 *             In case an unknown SQL exception occurred.
	 */
	public List<DBPaidTask> getTasksOfferedByUser(final String username,
			int start, int amount) throws DatabaseUnkownFailureException,
			InvalidParameterException;

	/**
	 * Register a user to an activity. does <b><u>not</u></b> update the balance
	 * of the users.
	 *
	 * @param id
	 *            the id of the desired activity. must be non-negative.
	 * @param username
	 *            the username of the user to be registered
	 * @param conn
	 *            The connection to be used. Must be open.
	 * @throws InvalidParameterException
	 *             the id is negative, username is null or conn is closed
	 * @throws ElementAlreadyExistException
	 *             the user is already registered to the activity
	 * @throws DatabaseUnkownFailureException
	 *             an unknown SQL exception was thrown
	 */
	public void registerToActivity(int id, String username, Connection conn)
			throws InvalidParameterException, ElementAlreadyExistException,
			DatabaseUnkownFailureException;

	/**
	 * Unregister a user from an activity. does <b><u>not</u></b> update the
	 * balance of the users.
	 *
	 * @param id
	 *            the id of the desired activity. must be non-negative.
	 * @param username
	 *            the username of the user to be unregistered.
	 * @param conn
	 *            The connection to be used. Must be open.
	 * @throws InvalidParameterException
	 *             the id is negative, username is null or conn is closed
	 * @throws DatabaseUnkownFailureException
	 *             an unknown SQL exception was thrown
	 * @throws ElementNotExistException
	 *             The user is not registered to the activity.
	 */
	public void unregisterFromActivity(int id, String username, Connection conn)
			throws InvalidParameterException, DatabaseUnkownFailureException,
			ElementNotExistException;
}
