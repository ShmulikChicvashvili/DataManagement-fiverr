
package com.servicebook.database;


import java.util.List;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.users.DatabaseInvalidParamsException;
import com.servicebook.database.exceptions.users.DatabaseAlreadyExistsException;
import com.servicebook.database.primitives.DBUser;




/**
 * @author Shmulik
 *
 */
public interface UsersDatabase
{
	
	/**
	 * Adds the user to the database.
	 *
	 * @param user
	 *            the user to be added
	 * @throws DatabaseAlreadyExistsException
	 *             User already exists exception
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws DatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public void addUser(final DBUser user)
		throws DatabaseAlreadyExistsException,
		DatabaseUnkownFailureException,
		DatabaseInvalidParamsException;
	
	
	/**
	 * Gets the user from the database.
	 *
	 * @param username
	 *            the user's username
	 * @return the user with the given username, or null if non existent
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws DatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public DBUser getUser(String username)
		throws DatabaseUnkownFailureException,
		DatabaseInvalidParamsException;
	
	
	/**
	 * Gets the users.
	 *
	 * @param start
	 *            the offset to the first user. 0 is the first user.
	 * @param amount
	 *            the amount of users to retrieve from the database. -1 returns
	 *            all users from 'start' to the end.
	 * @return a list of up to 'amount' users, starting from 'start'. If no
	 *         users match the criteria an empty list is returned.
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws DatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public List<DBUser> getUsers(int start, int amount)
		throws DatabaseUnkownFailureException,
		DatabaseInvalidParamsException;
	
	
	/**
	 * Checks if the user exists.
	 *
	 * @param username
	 *            the username to be checked.
	 * @return {@code True} if is user exists, {@code False} otherwise.
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure
	 * @throws DatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public boolean isUserExists(String username)
		throws DatabaseUnkownFailureException,
		DatabaseInvalidParamsException;
	
	
	/**
	 * Validates such a user exists with the given password.
	 *
	 * @param username
	 *            the user's username
	 * @param password
	 *            the user's password
	 * @return {@code True} if a username-login pair is registered (able to
	 *         login), {@code False} otherwise.
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws DatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public boolean validateUser(String username, String password)
		throws DatabaseUnkownFailureException,
		DatabaseInvalidParamsException;
}
