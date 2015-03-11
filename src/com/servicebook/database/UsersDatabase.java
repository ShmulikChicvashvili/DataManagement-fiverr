package com.servicebook.database;

import com.servicebook.database.primitives.DBUser;

public interface UsersDatabase {

	/**
	 * Adds the user to the database.
	 *
	 * @param user
	 *            the user to be added
	 */
	public void addUser(final DBUser user);

	/**
	 * Gets the user from the database.
	 *
	 * @param username
	 *            the user's username
	 * @return the user with the given username, or null if non existent
	 */
	public DBUser getUser(String username);
}
