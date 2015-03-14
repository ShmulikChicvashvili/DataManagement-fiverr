
package com.servicebook.database;


import java.util.List;

import com.servicebook.database.exceptions.UsersDatabaseExceptions.UsersDatabaseInvalidParamsException;
import com.servicebook.database.exceptions.UsersDatabaseExceptions.UsersDatabaseUnkownFailureException;
import com.servicebook.database.exceptions.UsersDatabaseExceptions.UsersDatabaseUserAlreadyExistsException;
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
	 * @throws UsersDatabaseUserAlreadyExistsException
	 *             User already exists exception
	 * @throws UsersDatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws UsersDatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public void addUser(final DBUser user)
		throws UsersDatabaseUserAlreadyExistsException,
		UsersDatabaseUnkownFailureException,
		UsersDatabaseInvalidParamsException;
	
	
	/**
	 * Gets the user from the database.
	 *
	 * @param username
	 *            the user's username
	 * @return the user with the given username, or null if non existent
	 * @throws UsersDatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws UsersDatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public DBUser getUser(String username)
		throws UsersDatabaseUnkownFailureException,
		UsersDatabaseInvalidParamsException;
	
	
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
	 * @throws UsersDatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws UsersDatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public List<DBUser> getUsers(int start, int amount)
		throws UsersDatabaseUnkownFailureException,
		UsersDatabaseInvalidParamsException;
	
	
	/**
	 * Checks if the user exists.
	 *
	 * @param username
	 *            the username to be checked.
	 * @return {@code True} if is user exists, {@code False} otherwise.
	 * @throws UsersDatabaseUnkownFailureException
	 *             Unknown failure
	 * @throws UsersDatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public boolean isUserExists(String username)
		throws UsersDatabaseUnkownFailureException,
		UsersDatabaseInvalidParamsException;
	
	
	/**
	 * Validates such a user exists with the given password.
	 *
	 * @param username
	 *            the user's username
	 * @param password
	 *            the user's password
	 * @return {@code True} if a username-login pair is registered (able to
	 *         login), {@code False} otherwise.
	 * @throws UsersDatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws UsersDatabaseInvalidParamsException
	 *             Invalid parameters exception
	 */
	public boolean validateUser(String username, String password)
		throws UsersDatabaseUnkownFailureException,
		UsersDatabaseInvalidParamsException;
}
