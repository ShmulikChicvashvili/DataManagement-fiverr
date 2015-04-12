
package com.servicebook.database.implementation;


import graphRepresentation.xmlTags;
import graphRepresentation.xmlTags.IsOfferingOpts;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utils.Pair;

import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.MultiTableDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.UsersDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.friendships.ReflexiveFriendshipException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;
import com.servicebook.database.exceptions.paidActivities.ElementNotExistException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.primitives.DBPaidActivity;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;
import com.servicebook.database.primitives.DBUser;




public class MultiTableDatabaseImpl extends AbstractMySqlDatabase
	implements
		MultiTableDatabase
{
	
	public MultiTableDatabaseImpl(
		String schema,
		BasicDataSource datasource,
		FriendshipsDatabase friendsDB,
		PaidActivitiesDatabase activitiesDB,
		UsersDatabase usersDB)
	{
		super(schema, datasource);
		this.friendsDB = friendsDB;
		this.activitiesDB = activitiesDB;
		this.usersDB = usersDB;
	}
	
	
	@Override
	public boolean registerToActivity(int id, String username)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (!isValidId(id) || username == null) { throw new InvalidParameterException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			final DBPaidActivity activity = activitiesDB.getActivity(id, conn);
			if (activity == null)
			{
				// activity doesn't exist
				return false;
			}
			assert activity.getCapacity() >= activity.getNumRegistered();
			if (activity.isFull()) { return false; }
			if (!friendsDB.areFriends(username, activity.getUsername(), conn)) { return false; }
			// Those are just asserts, will not run in release
			try
			{
				assert usersDB.isUserExists(username, conn);
				assert usersDB.isUserExists(activity.getUsername(), conn);
			} catch (
				DatabaseUnkownFailureException
				| com.servicebook.database.exceptions.users.InvalidParamsException e)
			{
				e.printStackTrace();
			}
			
			activitiesDB.registerToActivity(id, username, conn);
			
			final int toAdd = 1;
			if (activity instanceof DBPaidService)
			{
				usersDB.updateBalance(conn, username, -toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), toAdd);
			} else if (activity instanceof DBPaidTask)
			{
				usersDB.updateBalance(conn, username, toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), -toAdd);
			} else
			{
				assert false;
			}
			
			conn.commit();
		} catch (final com.servicebook.database.exceptions.paidActivities.ElementAlreadyExistException e)
		{
			// User is already registered to this activity
			return false;
		} catch (
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException
			| com.servicebook.database.exceptions.friendships.InvalidParamsException
			| com.servicebook.database.exceptions.users.InvalidParamsException e1)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
		
		return true;
	}
	
	
	@Override
	public boolean unregisterFromActivity(int id, String username)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (!isValidId(id) || username == null) { throw new InvalidParameterException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			final DBPaidActivity activity = activitiesDB.getActivity(id, conn);
			if (activity == null)
			{
				// no such activity
				return false;
			}
			try
			{
				// assert (usersDB.isUserExists(username, conn));
				assert usersDB.isUserExists(activity.getUsername(), conn);
			} catch (
				DatabaseUnkownFailureException
				| com.servicebook.database.exceptions.users.InvalidParamsException e)
			{
				e.printStackTrace();
			}
			
			activitiesDB.unregisterFromActivity(id, username, conn);
			
			final int toAdd = 1;
			if (activity instanceof DBPaidService)
			{
				usersDB.updateBalance(conn, username, toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), -toAdd);
			} else if (activity instanceof DBPaidTask)
			{
				usersDB.updateBalance(conn, username, -toAdd);
				usersDB.updateBalance(conn, activity.getUsername(), toAdd);
			} else
			{
				assert false;
			}
			
			conn.commit();
		} catch (final ElementNotExistException e)
		{
			// User was not registered to the activity
			return false;
		} catch (
			com.servicebook.database.exceptions.users.InvalidParamsException
			| com.servicebook.database.exceptions.paidActivities.InvalidParameterException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
		
		return true;
	}
	
	
	@Override
	public void deleteUser(String username)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (username == null) { throw new InvalidParameterException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			if (!usersDB.isUserExists(username, conn))
			{
				// Already not exist
				return;
			}
			
			// Delete all his activities and registrations
			activitiesDB.deleteUserPaidActivities(username, conn);
			activitiesDB.deleteUserRegistrations(username, conn);
			
			// Delete all his friendships
			friendsDB.deleteFriendships(username, conn);
			
			// Fatality! Delete the user! Muhahaha
			usersDB.deleteUser(conn, username);
			
			conn.commit();
		} catch (
			com.servicebook.database.exceptions.friendships.InvalidParamsException
			| com.servicebook.database.exceptions.users.InvalidParamsException
			| com.servicebook.database.exceptions.paidActivities.InvalidParameterException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
	}
	
	
	@Override
	public int addPaidService(DBPaidService service)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		return addPaidActivity(service);
	}
	
	
	@Override
	public int addPaidTask(DBPaidTask task)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		return addPaidActivity(task);
	}
	
	
	@Override
	public void deleteActivity(int id)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		if (!isValidId(id)) { throw new InvalidParameterException(); }
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			activitiesDB.deletePaidActivity(id, conn);
			conn.commit();
		} catch (final com.servicebook.database.exceptions.paidActivities.InvalidParameterException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
	}
	
	
	/* (non-Javadoc) @see
	 * com.servicebook.database.MultiTableDatabase#deleteUserActivities
	 * (java.lang.String) */
	@Override
	public void deleteUserActivities(String username)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		if (username == null) { throw new InvalidParameterException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			activitiesDB.deleteUserPaidActivities(username, conn);
			conn.commit();
		} catch (final com.servicebook.database.exceptions.paidActivities.InvalidParameterException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			// Could not open connection
			throw new DatabaseUnkownFailureException(e);
		}
	}
	
	
	@Override
	public void addFriendship(String username1, String username2)
		throws ElementAlreadyExistsException,
		DatabaseUnkownFailureException,
		InvalidParameterException,
		ReflexiveFriendshipException
	{
		if (username1 == null || username2 == null) { throw new InvalidParameterException(); }
		if (username1.equalsIgnoreCase(username2)) { throw new ReflexiveFriendshipException(); }
		
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			if (!usersDB.isUserExists(username1, conn)
				|| !usersDB.isUserExists(username2, conn)) { throw new InvalidParameterException(); }
			
			friendsDB.addFriendship(username1, username2, conn);
			
			conn.commit();
		} catch (
			com.servicebook.database.exceptions.friendships.InvalidParamsException
			| com.servicebook.database.exceptions.users.InvalidParamsException e)
		{
			throw new InvalidParameterException();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		}
	}
	
	
	@Override
	public Document toXML(String username)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		if (username == null) { throw new InvalidParameterException(); }
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try
		{
			dBuilder = dFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElement(xmlTags.ROOT_TAG);
		doc.appendChild(rootElement);
		
		List<DBUser> users = null;
		Set<String> offeringUsers = null;
		List<Pair<String, String>> friendships = null;
		try (Connection conn = getConnection())
		{
			conn
				.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			users = usersDB.getUsers(0, Integer.MAX_VALUE, conn);
			offeringUsers = activitiesDB.getOfferingUsers(username, conn);
			friendships = friendsDB.getAllFriendships(conn);
		} catch (SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		} catch (
			com.servicebook.database.exceptions.friendships.InvalidParamsException
			| com.servicebook.database.exceptions.paidActivities.InvalidParameterException
			| InvalidParamsException e)
		{
			throw new InvalidParameterException();
		}
		
		Element currUserElement = doc.createElement(xmlTags.CURR_USER_TAG);
		currUserElement.appendChild(doc.createTextNode(username));
		rootElement.appendChild(currUserElement);
		
		Element usersElement = doc.createElement(xmlTags.USERS_TAG);
		rootElement.appendChild(usersElement);
		Element friendshipsElement = doc.createElement(xmlTags.FRIENDSHIPS_TAG);
		rootElement.appendChild(friendshipsElement);
		
		for (DBUser u : users)
		{
			Element uElement = doc.createElement(xmlTags.USER_TAG);
			
			Element usernameElement = doc.createElement(xmlTags.USERNAME_TAG);
			usernameElement.appendChild(doc.createTextNode(u.getUsername()));
			uElement.appendChild(usernameElement);
			
			Element nameElement = doc.createElement(xmlTags.NAME_TAG);
			nameElement.appendChild(doc.createTextNode(u.getName()));
			uElement.appendChild(nameElement);
			
			Element balanceElement = doc.createElement(xmlTags.BALANCE_TAG);
			balanceElement.appendChild(doc.createTextNode(Integer.toString((u
				.getBalance()))));
			uElement.appendChild(balanceElement);
			
			if (offeringUsers.contains(u.getUsername()))
			{
				uElement.setAttribute(
					xmlTags.IS_OFFERING_ATT,
					IsOfferingOpts.Y.toXML());
			}
			
			usersElement.appendChild(uElement);
		}
		
		for (Pair<String, String> friendship : friendships)
		{
			Element friendshipElement =
				doc.createElement(xmlTags.FRIENDSHIP_TAG);
			
			Element u1Element = doc.createElement(xmlTags.USERNAME_TAG);
			u1Element.appendChild(doc.createTextNode(friendship.getFirst()));
			friendshipElement.appendChild(u1Element);
			
			Element u2Element = doc.createElement(xmlTags.USERNAME_TAG);
			u2Element.appendChild(doc.createTextNode(friendship.getSecond()));
			friendshipElement.appendChild(u2Element);
			
			friendshipsElement.appendChild(friendshipElement);
		}
		
		try
		{
			Transformer transformer =
				TransformerFactory.newInstance().newTransformer();
			StreamResult result = new StreamResult(System.out);
			transformer.transform(new DOMSource(doc), result);
		} catch (TransformerFactoryConfigurationError | TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;
	}
	
	
	
	private final FriendshipsDatabase friendsDB;
	
	private final PaidActivitiesDatabase activitiesDB;
	
	private final UsersDatabase usersDB;
	
	
	
	private int addPaidActivity(DBPaidActivity activity)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		if (activity == null
			|| activity.getUsername() == null
			|| activity.getCapacity() <= 0
			|| activity.getDistance() <= 0) { throw new InvalidParameterException(); }
		
		int $ = -1;
		try (Connection conn = getConnection())
		{
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			if (!usersDB.isUserExists(activity.getUsername(), conn)) { throw new InvalidParameterException(); }
			
			if (activity instanceof DBPaidService)
			{
				$ = activitiesDB.addPaidService((DBPaidService) activity, conn);
			} else if (activity instanceof DBPaidTask)
			{
				$ = activitiesDB.addPaidTask((DBPaidTask) activity, conn);
			}
			
			conn.commit();
		} catch (final SQLException e)
		{
			throw new DatabaseUnkownFailureException(e);
		} catch (
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException
			| InvalidParamsException e)
		{
			throw new InvalidParameterException();
		}
		return $;
	}
	
	
	private boolean isValidId(int id)
	{
		return id > 0;
	}
	
}
