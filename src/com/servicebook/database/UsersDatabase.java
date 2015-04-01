
package com.servicebook.database;


import java.sql.Connection;
import java.util.List;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.exceptions.users.ElementAlreadyExistsException;
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
	 * @throws ElementAlreadyExistsException
	 *             User already exists exception
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws InvalidParamsException
	 *             Invalid parameters exception
	 */
	public void addUser(final DBUser user)
		throws ElementAlreadyExistsException,
		DatabaseUnkownFailureException,
		InvalidParamsException;
	
	
	/**
	 * Gets the user from the database.
	 *
	 * @param username
	 *            the user's username
	 * @return the user with the given username, or null if non existent
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure exception
	 * @throws InvalidParamsException
	 *             Invalid parameters exception
	 */
	public DBUser getUser(String username)
		throws DatabaseUnkownFailureException,
		InvalidParamsException;
	
	
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
	 * @throws InvalidParamsException
	 *             Invalid parameters exception
	 */
	public List<DBUser> getUsers(int start, int amount)
		throws DatabaseUnkownFailureException,
		InvalidParamsException;
	
	
	/**
	 * Checks if the user exists.
	 *
	 * @param username
	 *            the username to be checked.
	 * @return {@code True} if is user exists, {@code False} otherwise.
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure
	 * @throws InvalidParamsException
	 *             Invalid parameters exception
	 */
	public boolean isUserExists(String username)
		throws DatabaseUnkownFailureException,
		InvalidParamsException;
	
	
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
	 * @throws InvalidParamsException
	 *             Invalid parameters exception
	 */
	public boolean validateUser(String username, String password)
		throws DatabaseUnkownFailureException,
		InvalidParamsException;
	
	
	/**
	 * Updates the balance for a specific user, the balance parameter is the
	 * difference to be set. The query will be a part of a transction, therefore
	 * the function will get the connection.
	 * 
	 * @param conn
	 *            The connection in this specific transaction
	 * @param username
	 *            The username of the user to be updated
	 * @param balance
	 *            The balance to be added or subtracted
	 * @throws InvalidParamsException
	 *             Invalid parameters exception
	 * @throws DatabaseUnkownFailureException
	 *             Unknown failure exception
	 */
	public void updateBalance(Connection conn, String username, int balance)
		throws InvalidParamsException,
		DatabaseUnkownFailureException;
}
