package com.servicebook.database;

import java.sql.Connection;
import java.util.List;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.*;
import com.servicebook.database.primitives.DBUser;

/**
 * The Interface FriendshipsDatabase.
 */
public interface FriendshipsDatabase {

	/**
	 * Adds a friendship relationship to the database. users must differ by
	 * their username. Does not validate users exist.
	 *
	 * @param user1
	 *            the first user
	 * @param user2
	 *            the second user
	 * @param conn
	 *            the connection to be used
	 * @throws ElementAlreadyExistsException
	 *             the element already exists exception
	 * @throws DatabaseUnkownFailureException
	 *             the database unkown failure exception
	 * @throws InvalidParamsException
	 *             the invalid params exception
	 * @throws ReflexiveFriendshipException
	 *             the reflexive friendship exception
	 */
	public void addFriendship(DBUser user1, DBUser user2, Connection conn)
			throws ElementAlreadyExistsException,
			DatabaseUnkownFailureException, InvalidParamsException,
			ReflexiveFriendshipException;

	/**
	 * Adds a friendship relationship to the database. Usernames can't be equal.
	 * Does not validate users exist.
	 *
	 * @param username1
	 *            the first username
	 * @param username2
	 *            the second username
	 * @param conn
	 *            the connection to be used
	 * @throws ElementAlreadyExistsException
	 *             the element already exists exception
	 * @throws DatabaseUnkownFailureException
	 *             the database unkown failure exception
	 * @throws InvalidParamsException
	 *             the invalid params exception
	 * @throws ReflexiveFriendshipException
	 *             the reflexive friendship exception
	 */
	public void addFriendship(String username1, String username2,
			Connection conn) throws ElementAlreadyExistsException,
			DatabaseUnkownFailureException, InvalidParamsException,
			ReflexiveFriendshipException;

	/**
	 * Gets the friends for the given user.
	 *
	 * @param username
	 *            the user's username.
	 * @return the friends of the user. Empty list if the user has no friends.
	 * @throws InvalidParamsException
	 * @throws DatabaseUnkownFailureException
	 */
	public List<DBUser> getFriends(String username)
			throws InvalidParamsException, DatabaseUnkownFailureException;

	/**
	 * Gets the friends for the given user.
	 *
	 * @param user
	 *            the user.
	 * @return the friends of the user. Empty list if the user has no friends.
	 * @throws InvalidParamsException
	 * @throws DatabaseUnkownFailureException
	 */
	public List<DBUser> getFriends(DBUser user) throws InvalidParamsException,
			DatabaseUnkownFailureException;

	/**
	 * Checks if the users are friends.
	 *
	 * @param username1
	 *            the first username
	 * @param username2
	 *            the second username
	 * @param conn
	 *            the connection to be used
	 * @return true, if successful
	 * @throws InvalidParamsException
	 *             If the strings are null or the connection is closed.
	 * @throws DatabaseUnkownFailureException
	 *             the database unknown failure exception
	 */
	public boolean areFriends(String username1, String username2,
			Connection conn) throws InvalidParamsException,
			DatabaseUnkownFailureException;

	/**
	 * Delete all user's friendships.
	 *
	 * @param username
	 *            the username to be deleted
	 * @param conn
	 *            the connection to be used
	 * @throws InvalidParamsException
	 *             the username is null or the connection is closed.
	 * @throws DatabaseUnkownFailureException
	 *             the database unknown failure exception
	 */
	public void deleteFriendships(String username, Connection conn)
			throws InvalidParamsException, DatabaseUnkownFailureException;
}
