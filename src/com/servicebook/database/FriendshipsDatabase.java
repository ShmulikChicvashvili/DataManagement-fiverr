package com.servicebook.database;

import java.sql.Connection;
import java.util.List;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.*;
import com.servicebook.database.primitives.DBUser;

// TODO: Auto-generated Javadoc
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
	 * @throws ElementAlreadyExistsException
	 * @throws DatabaseUnkownFailureException
	 * @throws InvalidParamsException
	 * @throws ReflexiveFriendshipException
	 */
	public void addFriendship(DBUser user1, DBUser user2)
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
	 * @throws ElementAlreadyExistsException
	 * @throws DatabaseUnkownFailureException
	 * @throws InvalidParamsException
	 * @throws ReflexiveFriendshipException
	 */
	public void addFriendship(String username1, String username2)
			throws ElementAlreadyExistsException,
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

	public boolean areFriends(String user1, String user2, Connection conn);
}
