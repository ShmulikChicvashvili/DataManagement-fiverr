package com.servicebook.database;

import java.sql.Connection;
import java.util.List;

import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

public interface PaidActivitiesDatabase {

	/**
	 * Adds the paid service to the database.
	 *
	 * @param service
	 *            the service to be added.
	 */
	public void addPaidService(final DBPaidService service);

	/**
	 * Adds the paid task to the database.
	 *
	 * @param task
	 *            the task to be added.
	 */
	public void addPaidTask(final DBPaidTask task);

	/**
	 * Deletes the paid activity. does <b><u>not</u></b> update the balance for
	 * the participators.
	 *
	 * @param id
	 *            the id of the activity to be deleted.
	 * @param conn
	 *            the Sql connection to be used. Used for transactions.
	 */
	public void deletePaidActivity(int id, Connection conn);

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
	 *            the maximum amount of services to return. -1 mentions infinite
	 *            amount.
	 * @return the services offered to the user, including the amount of
	 *         registered users to the service. If no services are offered an
	 *         empty set is returned.
	 */
	public List<DBPaidService> getServicesOfferedToUser(final String username,
			int start, int amount);

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
	 *            the maximum amount of tasks to return. -1 mentions infinite
	 *            amount.
	 * @return the tasks offered to the user, including the amount of registered
	 *         users to the task. If no tasks are offered an empty set is
	 *         returned.
	 */
	public List<DBPaidTask> getTasksOfferedToUser(final String username,
			int start, int amount);

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
	 *            the maximum amount of services to return. -1 mentions infinite
	 *            amount.
	 * @return the services offered by the user, including the amount of
	 *         registered users to the service.
	 */
	public List<DBPaidService> getServicesOfferedByUser(final String username,
			int start, int amount);

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
	 *            the maximum amount of tasks to return. -1 mentions infinite
	 *            amount.
	 * @return the tasks offered by the user, including the amount of registered
	 *         users to the task.
	 */
	public List<DBPaidTask> getTasksOfferedByUser(final String username,
			int start, int amount);

	/**
	 * Register a user to an activity. does <b><u>not</u></b> update the balance
	 * of the users.
	 *
	 * @param username
	 *            the username of the user to be registered
	 * @param id
	 *            the id of the desired activity
	 * @param conn
	 *            TODO
	 */
	public void registerToActivity(String username, long id, Connection conn);

	/**
	 * Unregister a user from an activity. does <b><u>not</u></b> update the
	 * balance of the users.
	 *
	 * @param username
	 *            the username of the user to be unregistered.
	 * @param id
	 *            the id of the desired activity.
	 * @param conn
	 *            TODO
	 */
	public void unregisterFromActivity(String username, long id, Connection conn);
}
