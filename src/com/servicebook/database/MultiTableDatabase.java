package com.servicebook.database;

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
	 */
	public void registerToActivity(int id, String username);

	/**
	 * Unregister from activity.
	 *
	 * @param id
	 *            the id for the activity
	 * @param username
	 *            the username to be unregistered from the activity
	 */
	public void unregisterFromActivity(int id, String username);

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
