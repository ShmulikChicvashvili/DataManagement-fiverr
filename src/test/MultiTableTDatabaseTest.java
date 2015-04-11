
package test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.PaidActivitiesDatabase.ActivityStatus;
import com.servicebook.database.UsersDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.ReflexiveFriendshipException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;
import com.servicebook.database.exceptions.paidActivities.FriendshipsTableNotExist;
import com.servicebook.database.exceptions.users.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.implementation.FriendshipsDatabaseImpl;
import com.servicebook.database.implementation.MultiTableDatabaseImpl;
import com.servicebook.database.implementation.PaidActivitiesDatabaseImpl;
import com.servicebook.database.implementation.UsersDatabaseImpl;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;
import com.servicebook.database.primitives.DBUser;




public class MultiTableTDatabaseTest
{

	private class FriendsTableInfo
	{
		/**
		 * @param tableName
		 * @param userColumn
		 * @param friendColumn
		 */
		public FriendsTableInfo(
			String tableName,
			String userColumn,
			String friendColumn)
		{
			super();
			this.tableName = tableName;
			this.userColumn = userColumn;
			this.friendColumn = friendColumn;
		}



		public String tableName;

		public String userColumn;

		public String friendColumn;
	}



	public MultiTableTDatabaseTest()
	{
		schema = "servicebook_db";
		friendsTableInfo =
			new FriendsTableInfo(
				"friendships",
				"first_username",
				"second_username");
	}


	@Before
	public void setUp() throws Exception
	{
		ds = new BasicDataSource();
		ds.setDefaultAutoCommit(false);
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername("root");
		ds.setPassword("root");
		ds.setUrl("jdbc:mysql://localhost/");

		try (
			Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement())
		{
			String dropQuery = "";

			dropQuery = "DROP TABLE IF EXISTS `" + schema + "`.`users`;";
			stmt.execute(dropQuery);

			dropQuery =
				"DROP TABLE IF EXISTS `"
					+ schema
					+ "`.`"
					+ friendsTableInfo.tableName
					+ "`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `" + schema + "`.`activities`;";
			stmt.execute(dropQuery);

			dropQuery =
				"DROP TABLE IF EXISTS `"
					+ schema
					+ "`.`activities_registrations`;";
			stmt.execute(dropQuery);

			conn.commit();
		}

		usersDB = new UsersDatabaseImpl("users", schema, ds);
		friendsDB =
			new FriendshipsDatabaseImpl(
				friendsTableInfo.tableName,
				"users",
				schema,
				ds);
		activitiesDB =
			new PaidActivitiesDatabaseImpl(
				"activities",
				"activities_registrations",
				schema,
				ds,
				friendsTableInfo.tableName,
				friendsTableInfo.userColumn,
				friendsTableInfo.friendColumn);
		multiDB =
			new MultiTableDatabaseImpl(
				schema,
				ds,
				friendsDB,
				activitiesDB,
				usersDB);
	}


	@After
	public void tearDown() throws Exception
	{

		try (
			Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement())
		{
			String dropQuery = "";

			dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`users`;";
			stmt.execute(dropQuery);

			dropQuery =
				"DROP TABLE IF EXISTS `"
					+ schema
					+ "`.`"
					+ friendsTableInfo.tableName
					+ "`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`activities`;";
			stmt.execute(dropQuery);

			dropQuery =
				"DROP TABLE IF EXISTS `servicebook_db`.`activities_registrations`;";
			stmt.execute(dropQuery);

			conn.commit();
		}

		ds.close();
	}


	@Test
	public
		void
		testAddPaidActivity()
			throws DatabaseUnkownFailureException,
			ElementAlreadyExistsException,
			InvalidParamsException,
			InvalidParameterException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException
	{
		try
		{
			multiDB.addPaidService(null);
			fail();
		} catch (final InvalidParameterException e)
		{}
		try
		{
			multiDB.addPaidTask(null);
			fail();
		} catch (final InvalidParameterException e)
		{}

		final String username = "u1";
		final DBUser user = new DBUser(username, "123", username, 0);
		final DBPaidService s = new DBPaidService("s1", username, 2, 3, 0);
		final DBPaidTask t = new DBPaidTask("t1", username, 21, 5, 0);
		try
		{
			multiDB.addPaidService(s);
			fail();
		} catch (final InvalidParameterException e)
		{}
		try
		{
			multiDB.addPaidTask(t);
			fail();
		} catch (final InvalidParameterException e)
		{}

		final List<DBPaidService> services = new ArrayList<DBPaidService>();
		final List<DBPaidTask> tasks = new ArrayList<DBPaidTask>();

		usersDB.addUser(user);

		final int num = 10;
		for (int i = 0; i < num; i++)
		{
			services.add(new DBPaidService(
				"s" + i,
				username,
				i + 3,
				i * 3 + 1,
				0));
			tasks.add(new DBPaidTask("t" + i, username, i * 2 + 5, i + 3, 0));
		}

		for (int i = 0; i < num; i++)
		{
			multiDB.addPaidService(services.get(i));
			assertEquals(
				ActivityStatus.SERVICE,
				activitiesDB.getActivityStatus(services.get(i).getId()));

			multiDB.addPaidTask(tasks.get(i));
			assertEquals(
				ActivityStatus.TASK,
				activitiesDB.getActivityStatus(tasks.get(i).getId()));
		}

		assertEquals(
			services,
			activitiesDB.getServicesOfferedByUser(username, 0, num));
		assertEquals(
			tasks,
			activitiesDB.getTasksOfferedByUser(username, 0, num));

	}


	@Test
	public
		void
		testDeleteActivity()
			throws DatabaseUnkownFailureException,
			InvalidParameterException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException,
			ElementAlreadyExistsException,
			InvalidParamsException,
			com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException,
			ReflexiveFriendshipException
	{
		try
		{
			multiDB.deleteActivity(0);
			fail();
		} catch (final InvalidParameterException e)
		{}

		final String username1 = "u1", username2 = "u2";
		final DBUser u1 = new DBUser(username1, "123", "user", 0);
		final DBUser u2 = new DBUser(username2, "321", "bla", 0);

		final List<DBPaidService> services = new ArrayList<DBPaidService>();
		final List<DBPaidTask> tasks = new ArrayList<DBPaidTask>();

		final int num = 10;
		for (int i = 0; i < num; i++)
		{
			services.add(new DBPaidService(
				"s" + i,
				username1,
				i + 3,
				i * 3 + 1,
				0));
			tasks.add(new DBPaidTask("t" + i, username1, i * 2 + 5, i + 3, 0));
		}

		usersDB.addUser(u1);
		usersDB.addUser(u2);
		multiDB.addFriendship(username1, username2);

		for (int i = 0; i < num; i++)
		{
			multiDB.addPaidService(services.get(i));
			multiDB.addPaidTask(tasks.get(i));

			multiDB.registerToActivity(services.get(i).getId(), username2);
			services.get(i).setNumRegistered((short) 1);
		}

		for (int i = num - 1; i >= 0; i -= 2)
		{
			multiDB.deleteActivity(services.get(i).getId());
			services.remove(i);
			multiDB.deleteActivity(tasks.get(i).getId());
			tasks.remove(i);
		}

		assertEquals(
			services,
			activitiesDB.getServicesOfferedByUser(username1, 0, num));
		assertEquals(
			tasks,
			activitiesDB.getTasksOfferedByUser(username1, 0, num));

	}


	@Test
	public
		void
		testAddFriendship()
			throws com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException,
			DatabaseUnkownFailureException,
			ReflexiveFriendshipException,
			InvalidParameterException,
			ElementAlreadyExistsException,
			InvalidParamsException
	{
		final String username1 = "u1", username2 = "u2";
		final DBUser u1 = new DBUser(username1, "123", "Dan", 0);
		final DBUser u2 = new DBUser(username2, "321", "Gal", 0);
		try
		{
			multiDB.addFriendship(null, username2);
			multiDB.addFriendship(username1, null);
			fail();
		} catch (final InvalidParameterException e)
		{}
		try
		{
			multiDB.addFriendship(username1, username1);
			fail();
		} catch (final ReflexiveFriendshipException e)
		{}
		try
		{
			// users don't exist
			multiDB.addFriendship(username1, username2);
			fail();
		} catch (final InvalidParameterException e)
		{}

		usersDB.addUser(u1);
		try
		{
			// u2 still doesn't exist
			multiDB.addFriendship(username1, username2);
			multiDB.addFriendship(username2, username1);
			fail();
		} catch (final InvalidParameterException e)
		{}
		usersDB.addUser(u2);

		multiDB.addFriendship(username1, username2);
		try
		{
			// already friends
			multiDB.addFriendship(username1, username2);
			fail();
		} catch (final com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException e)
		{}
		try
		{
			// already friends. check other way.
			multiDB.addFriendship(username2, username1);
			fail();
		} catch (final com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException e)
		{}

	}


	@Test
	public
		void
		testRegisterToActivity()
			throws DatabaseUnkownFailureException,
			InvalidParameterException,
			ElementAlreadyExistsException,
			InvalidParamsException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException,
			com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException,
			ReflexiveFriendshipException
	{
		rand.setSeed(1234567);
		final List<DBUser> users = new ArrayList<DBUser>();
		final Map<String, List<DBPaidService>> user2service =
			new HashMap<String, List<DBPaidService>>();
		final Map<String, List<DBPaidTask>> user2task =
			new HashMap<String, List<DBPaidTask>>();
		final Map<String, List<DBPaidService>> user2regService =
			new HashMap<String, List<DBPaidService>>();
		final Map<String, List<DBPaidTask>> user2regTask =
			new HashMap<String, List<DBPaidTask>>();

		try
		{
			multiDB.registerToActivity(0, "username");
			fail();
		} catch (final InvalidParameterException e)
		{}
		try
		{
			multiDB.registerToActivity(1, null);
			fail();
		} catch (final InvalidParameterException e)
		{}

		addUsers(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask,
			4);

		final DBUser u0 = users.get(0);
		final DBUser u1 = users.get(1);
		final DBUser u2 = users.get(2);
		final DBUser u3 = users.get(3);

		final DBPaidService s =
			new DBPaidService("s1", u0.getUsername(), 2, 3, 0);
		multiDB.addPaidService(s);

		// Either the activity or the user does not exist
		assertFalse(multiDB.registerToActivity(s.getId() + 1, "username"));
		assertFalse(multiDB.registerToActivity(s.getId(), "username"));
		assertFalse(multiDB.registerToActivity(s.getId() + 1, u0.getUsername()));

		multiDB.deleteActivity(s.getId());
		assertEquals(
			ActivityStatus.NOT_EXIST,
			activitiesDB.getActivityStatus(s.getId()));

		// check it can't be registered after deletion
		assertFalse(multiDB.registerToActivity(s.getId(), u0.getUsername()));

		final int maxServices = 6;
		final int maxTasks = 4;
		fillUsersActivities(
			users,
			user2service,
			user2task,
			maxServices,
			maxTasks);
		final DBPaidService s1 =
			new DBPaidService(
				u0.getUsername() + "_s8",
				u0.getUsername(),
				1,
				1,
				0);
		final DBPaidService s2 =
			new DBPaidService(
				u0.getUsername() + "_s9",
				u0.getUsername(),
				2,
				1,
				0);
		user2service.get(u0.getUsername()).add(s1);
		user2service.get(u0.getUsername()).add(s2);

		addUserActivitiesToDB(users, user2service, user2task);

		// check users can't register because the'yre not friends.
		for (final DBUser u : users)
		{
			for (final DBPaidService service : user2service
				.get(u.getUsername()))
			{
				assertFalse(multiDB.registerToActivity(
					service.getId(),
					u.getUsername()));
			}
			for (final DBPaidTask task : user2task.get(u.getUsername()))
			{
				assertFalse(multiDB.registerToActivity(
					task.getId(),
					u.getUsername()));
			}
		}

		multiDB.addFriendship(u0.getUsername(), u1.getUsername());
		multiDB.addFriendship(u0.getUsername(), u2.getUsername());
		for (final DBPaidService service : user2service.get(u0.getUsername()))
		{
			registerToService(multiDB, service, u0, u1, user2regService);
		}
		for (final DBPaidTask task : user2task.get(u0.getUsername()))
		{
			registerToTask(multiDB, task, u0, u1, user2regTask);
		}

		assertEquals(u0, usersDB.getUser(u0.getUsername()));
		assertEquals(u1, usersDB.getUser(u1.getUsername()));

		// s2 has one more room, s1 has none
		registerToService(multiDB, s2, u0, u2, user2regService);
		assertEquals(u0, usersDB.getUser(u0.getUsername()));
		assertEquals(u2, usersDB.getUser(u2.getUsername()));
		assertFalse(multiDB.registerToActivity(s1.getId(), u2.getUsername()));

		// u1 and u2 are still not friends, u2 can't register to its services or
		// tasks
		assertFalse(multiDB.registerToActivity(
			user2service.get(u1.getUsername()).get(0).getId(),
			u2.getUsername()));
		assertFalse(multiDB.registerToActivity(user2task
			.get(u1.getUsername())
			.get(0)
			.getId(), u2.getUsername()));

		for (final DBUser u : users)
		{
			assertEquals(u, usersDB.getUser(u.getUsername()));
		}
	}


	@Test
	public
		void
		testUnregisterFromActivity()
			throws ElementAlreadyExistsException,
			DatabaseUnkownFailureException,
			InvalidParamsException,
			InvalidParameterException,
			com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException,
			ReflexiveFriendshipException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException
	{
		rand.setSeed(1234567);
		final List<DBUser> users = new ArrayList<DBUser>();
		final Map<String, List<DBPaidService>> user2service =
			new HashMap<String, List<DBPaidService>>();
		final Map<String, List<DBPaidTask>> user2task =
			new HashMap<String, List<DBPaidTask>>();
		final Map<String, List<DBPaidService>> user2regService =
			new HashMap<String, List<DBPaidService>>();
		final Map<String, List<DBPaidTask>> user2regTask =
			new HashMap<String, List<DBPaidTask>>();

		addUsers(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask,
			4);
		fillUsersActivities(users, user2service, user2task, 6, 5);
		final DBUser u0 = users.get(0);
		final DBUser u1 = users.get(1);
		final DBUser u2 = users.get(2);
		final DBUser u3 = users.get(3);

		final DBPaidService s1 =
			new DBPaidService(
				u0.getUsername() + "_s8",
				u0.getUsername(),
				1,
				1,
				0);
		final DBPaidService s2 =
			new DBPaidService(
				u0.getUsername() + "_s9",
				u0.getUsername(),
				2,
				1,
				0);
		final DBPaidTask t1 =
			new DBPaidTask(u0.getUsername() + "_t8", u0.getUsername(), 1, 1, 0);

		user2service.get(u0.getUsername()).add(s1);
		user2service.get(u0.getUsername()).add(s2);
		user2task.get(u0.getUsername()).add(t1);

		// Either user or activity doesn't exist
		assertFalse(multiDB.unregisterFromActivity(1, "username_not_exist"));
		assertFalse(multiDB.unregisterFromActivity(1, u0.getUsername()));
		addUserActivitiesToDB(users, user2service, user2task);
		assertFalse(multiDB.unregisterFromActivity(
			s1.getId(),
			"username_not_exist"));

		multiDB.addFriendship(u0.getUsername(), u1.getUsername());
		multiDB.addFriendship(u0.getUsername(), u2.getUsername());
		assertFalse(multiDB
			.unregisterFromActivity(s1.getId(), u1.getUsername()));

		// register u1 to all u0 services and to t1
		for (final DBPaidService s : user2service.get(u0.getUsername()))
		{
			registerToService(multiDB, s, u0, u1, user2regService);
		}
		registerToTask(multiDB, t1, u0, u1, user2regTask);
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		// check u2 can't unregister from s2 before he registered.
		assertFalse(multiDB
			.unregisterFromActivity(s2.getId(), u2.getUsername()));
		registerToService(multiDB, s2, u0, u2, user2regService);

		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		// check u2 can unregister from s2 and the balance is updated
		unregisterFromService(multiDB, s2, u0, u2, user2regService);
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		// check u2 can register back again to s2
		registerToService(multiDB, s2, u0, u2, user2regService);
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		final DBPaidTask t = user2task.get(u1.getUsername()).get(0);
		// check u0 can't unregister from t before he registered.
		assertFalse(multiDB.unregisterFromActivity(t.getId(), u0.getUsername()));
		registerToTask(multiDB, t, u1, u0, user2regTask);
		unregisterFromTask(multiDB, t, u1, u0, user2regTask);
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		// check u0 can register back to t
		registerToTask(multiDB, t, u1, u0, user2regTask);
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		// check u2 can register after u1 has unregistered from s1
		assertFalse(multiDB.registerToActivity(s1.getId(), u2.getUsername()));
		unregisterFromService(multiDB, s1, u0, u1, user2regService);
		registerToService(multiDB, s1, u0, u2, user2regService);

		// check u2 can register after u1 has unregistered from t1
		assertFalse(multiDB.registerToActivity(t1.getId(), u2.getUsername()));
		unregisterFromTask(multiDB, t1, u0, u1, user2regTask);
		registerToTask(multiDB, t1, u0, u2, user2regTask);

		// check u1 can unregister from s2
		unregisterFromService(multiDB, s2, u0, u1, user2regService);

		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		multiDB.deleteActivity(s1.getId());
		user2service.get(u0.getUsername()).remove(s1);
		user2regService.get(u2.getUsername()).remove(s1);
		// u2 is not registered to s1
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);
		assertFalse(multiDB
			.unregisterFromActivity(s1.getId(), u2.getUsername()));

		// u2 is still registered to s2, and u0 to t
		unregisterFromService(multiDB, s2, u0, u2, user2regService);
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);
		unregisterFromTask(multiDB, t, u1, u0, user2regTask);
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		// u1 is still not registered to s2
		assertFalse(multiDB
			.unregisterFromActivity(s2.getId(), u1.getUsername()));

	}


	@Test
	public
		void
		testDeleteUser()
			throws DatabaseUnkownFailureException,
			InvalidParameterException,
			ElementAlreadyExistsException,
			InvalidParamsException,
			com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException,
			ReflexiveFriendshipException,
			com.servicebook.database.exceptions.friendships.InvalidParamsException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException,
			FriendshipsTableNotExist
	{
		rand.setSeed(1234567);
		try
		{
			multiDB.deleteUser(null);
			fail();
		} catch (final InvalidParameterException e)
		{}
		multiDB.deleteUser("not_exist");

		final List<DBUser> users = new ArrayList<DBUser>();
		final Map<String, List<DBPaidService>> user2service =
			new HashMap<String, List<DBPaidService>>();
		final Map<String, List<DBPaidTask>> user2task =
			new HashMap<String, List<DBPaidTask>>();
		final Map<String, List<DBPaidService>> user2regService =
			new HashMap<String, List<DBPaidService>>();
		final Map<String, List<DBPaidTask>> user2regTask =
			new HashMap<String, List<DBPaidTask>>();
		addUsers(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask,
			4);
		final DBUser u0 = users.get(0);
		final DBUser u1 = users.get(1);
		final DBUser u2 = users.get(2);
		final DBUser u3 = users.get(3);
		final DBPaidService u0s1 =
			new DBPaidService(
				u0.getUsername() + "_s8",
				u0.getUsername(),
				1,
				1,
				0);
		final DBPaidService u0s2 =
			new DBPaidService(
				u0.getUsername() + "_s9",
				u0.getUsername(),
				2,
				1,
				0);
		final DBPaidTask u0t1 =
			new DBPaidTask(u0.getUsername() + "_t8", u0.getUsername(), 1, 1, 0);
		final DBPaidTask u1t1 =
			new DBPaidTask(u1.getUsername() + "_t8", u1.getUsername(), 3, 1, 0);

		// check a simple empty user deletion works
		deleteUser(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask,
			u0);
		addUser(users, u0);

		// check friendships are deleted
		multiDB.addFriendship(u0.getUsername(), u1.getUsername());
		multiDB.addFriendship(u0.getUsername(), u2.getUsername());
		deleteUser(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask,
			u0);
		assertFalse(friendsDB.areFriends(u0.getUsername(), u1.getUsername()));
		assertFalse(friendsDB.areFriends(u0.getUsername(), u2.getUsername()));
		addUser(users, u0);
		assertFalse(friendsDB.areFriends(u0.getUsername(), u1.getUsername()));
		assertFalse(friendsDB.areFriends(u0.getUsername(), u2.getUsername()));

		// check services and tasks are deleted
		fillUsersActivities(users, user2service, user2task, 6, 5);
		user2service.get(u0.getUsername()).add(u0s1);
		user2service.get(u0.getUsername()).add(u0s2);
		user2task.get(u0.getUsername()).add(u0t1);
		user2task.get(u1.getUsername()).add(u1t1);
		addUserActivitiesToDB(users, user2service, user2task);
		deleteUser(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask,
			u0);
		assertEquals(
			ActivityStatus.NOT_EXIST,
			activitiesDB.getActivityStatus(u0s1.getId()));
		assertEquals(
			ActivityStatus.NOT_EXIST,
			activitiesDB.getActivityStatus(u0s2.getId()));
		assertEquals(
			ActivityStatus.NOT_EXIST,
			activitiesDB.getActivityStatus(u0t1.getId()));

		addUser(users, u0);
		assertEquals(
			ActivityStatus.NOT_EXIST,
			activitiesDB.getActivityStatus(u0s1.getId()));
		assertEquals(
			ActivityStatus.NOT_EXIST,
			activitiesDB.getActivityStatus(u0s2.getId()));
		assertEquals(
			ActivityStatus.NOT_EXIST,
			activitiesDB.getActivityStatus(u0t1.getId()));

		fillUsersActivities(toList(u0), user2service, user2task, 6, 5);
		user2service.get(u0.getUsername()).add(u0s1);
		user2service.get(u0.getUsername()).add(u0s2);
		user2task.get(u0.getUsername()).add(u0t1);
		addUserActivitiesToDB(toList(u0), user2service, user2task);

		multiDB.addFriendship(u0.getUsername(), u1.getUsername());
		multiDB.addFriendship(u0.getUsername(), u2.getUsername());
		multiDB.addFriendship(u1.getUsername(), u2.getUsername());

		// add some registrations, and then delete u0. check balances and
		// offered services
		System.out.println("u0 services: "
			+ user2service.get(u0.getUsername()).toString());
		System.out.println("u2 services: "
			+ user2service.get(u2.getUsername()).toString());
		checkOfferedActivities(
			user2service,
			user2task,
			u0,
			user2regService,
			user2regTask,
			u1,
			u2);
		checkOfferedActivities(
			user2service,
			user2task,
			u1,
			user2regService,
			user2regTask,
			u0,
			u2);
		checkOfferedActivities(
			user2service,
			user2task,
			u2,
			user2regService,
			user2regTask,
			u0,
			u1);

		registerToService(multiDB, u0s1, u0, u1, user2regService);
		registerToService(multiDB, u0s2, u0, u1, user2regService);
		registerToService(multiDB, u0s2, u0, u2, user2regService);
		registerToTask(multiDB, u0t1, u0, u2, user2regTask);
		registerToTask(multiDB, u1t1, u1, u0, user2regTask);
		registerToTask(multiDB, u1t1, u1, u2, user2regTask);

		assertEquals(1, u0s1.getNumRegistered());
		assertEquals(2, u0s2.getNumRegistered());
		assertEquals(1, u0t1.getNumRegistered());
		assertEquals(2, u1t1.getNumRegistered());
		assertEquals(3 - 1 + 1, u0.getBalance());
		assertEquals(-2 - 2, u1.getBalance());
		assertEquals(-1 + 2, u2.getBalance());

		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);
		checkOfferedActivities(
			user2service,
			user2task,
			u0,
			user2regService,
			user2regTask,
			u1,
			u2);
		checkOfferedActivities(
			user2service,
			user2task,
			u1,
			user2regService,
			user2regTask,
			u0,
			u2);
		checkOfferedActivities(
			user2service,
			user2task,
			u2,
			user2regService,
			user2regTask,
			u0,
			u1);

		deleteUser(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask,
			u0);
		u0.setBalance(0);
		u0s1.setNumRegistered((short) 0);
		u0s2.setNumRegistered((short) 0);
		u0t1.setNumRegistered((short) 0);
		u1t1.setNumRegistered((short) 1);
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);
		checkOfferedActivities(
			user2service,
			user2task,
			u1,
			user2regService,
			user2regTask,
			u2);
		checkOfferedActivities(
			user2service,
			user2task,
			u2,
			user2regService,
			user2regTask,
			u1);

		// readd him
		addUser(users, u0);

		// check users are still ok, and their activities
		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);

		// check he has no activities offered to him
		assertEquals(
			new ArrayList<DBPaidService>(),
			activitiesDB.getServicesOfferedToUser(u0.getUsername(), 0, 100));
		assertEquals(
			new ArrayList<DBPaidTask>(),
			activitiesDB.getTasksOfferedToUser(u0.getUsername(), 0, 100));

		// check u1 and u2 still have the correct activities offered to them
		checkOfferedActivities(
			user2service,
			user2task,
			u1,
			user2regService,
			user2regTask,
			u2);
		checkOfferedActivities(
			user2service,
			user2task,
			u2,
			user2regService,
			user2regTask,
			u1);

		// befriend them again and check everything is still ok
		multiDB.addFriendship(u0.getUsername(), u1.getUsername());
		multiDB.addFriendship(u0.getUsername(), u2.getUsername());

		assertUsersEqual(
			users,
			user2service,
			user2task,
			user2regService,
			user2regTask);
		checkOfferedActivities(
			user2service,
			user2task,
			u0,
			user2regService,
			user2regTask,
			u1,
			u2);
		checkOfferedActivities(
			user2service,
			user2task,
			u1,
			user2regService,
			user2regTask,
			u0,
			u2);
		checkOfferedActivities(
			user2service,
			user2task,
			u2,
			user2regService,
			user2regTask,
			u0,
			u1);

	}


	private
		void
		checkOfferedActivities(
			Map<String, List<DBPaidService>> user2service,
			Map<String, List<DBPaidTask>> user2task,
			DBUser user,
			Map<String, List<DBPaidService>> user2regService,
			Map<String, List<DBPaidTask>> user2regTask,
			DBUser friend)
			throws com.servicebook.database.exceptions.paidActivities.InvalidParameterException,
			DatabaseUnkownFailureException,
			FriendshipsTableNotExist
	{
		final List<DBPaidService> services =
			new ArrayList<DBPaidService>(user2service.get(friend.getUsername()));
		services.removeAll(user2regService.get(user.getUsername()));

		final List<DBPaidTask> tasks =
			new ArrayList<DBPaidTask>(user2task.get(friend.getUsername()));
		tasks.removeAll(user2regTask.get(user.getUsername()));

		assertEquals(
			services,
			activitiesDB.getServicesOfferedToUser(user.getUsername(), 0, 100));
		assertEquals(
			tasks,
			activitiesDB.getTasksOfferedToUser(user.getUsername(), 0, 100));
		assertEquals(
			services.size() + tasks.size(),
			activitiesDB.getActivitiesOfferedToUserCount(user.getUsername()));

		assertEquals(
			user2regService.get(user.getUsername()),
			activitiesDB.getServicesUserRegistered(user.getUsername(), 0, 100));
		assertEquals(
			user2regTask.get(user.getUsername()),
			activitiesDB.getTasksUserRegistered(user.getUsername(), 0, 100));
		assertEquals(
			user2regService.get(user.getUsername()).size()
				+ user2regTask.get(user.getUsername()).size(),
			activitiesDB.getActivitiesUserRegisteredCount(user.getUsername()));

	}


	private
		void
		checkOfferedActivities(
			Map<String, List<DBPaidService>> user2service,
			Map<String, List<DBPaidTask>> user2task,
			DBUser user,
			Map<String, List<DBPaidService>> user2regService,
			Map<String, List<DBPaidTask>> user2regTask,
			DBUser friend1,
			DBUser friend2)
			throws com.servicebook.database.exceptions.paidActivities.InvalidParameterException,
			DatabaseUnkownFailureException,
			FriendshipsTableNotExist
	{
		final List<DBPaidService> services =
			concatLists(
				user2service.get(friend1.getUsername()),
				user2service.get(friend2.getUsername()));
		services.removeAll(user2regService.get(user.getUsername()));

		final List<DBPaidTask> tasks =
			concatLists(
				user2task.get(friend1.getUsername()),
				user2task.get(friend2.getUsername()));
		tasks.removeAll(user2regTask.get(user.getUsername()));

		assertEquals(
			services,
			activitiesDB.getServicesOfferedToUser(user.getUsername(), 0, 100));
		assertEquals(
			tasks,
			activitiesDB.getTasksOfferedToUser(user.getUsername(), 0, 100));
		assertEquals(
			services.size() + tasks.size(),
			activitiesDB.getActivitiesOfferedToUserCount(user.getUsername()));

		assertEquals(
			user2regService.get(user.getUsername()),
			activitiesDB.getServicesUserRegistered(user.getUsername(), 0, 100));
		assertEquals(
			user2regTask.get(user.getUsername()),
			activitiesDB.getTasksUserRegistered(user.getUsername(), 0, 100));
		assertEquals(
			user2regService.get(user.getUsername()).size()
				+ user2regTask.get(user.getUsername()).size(),
			activitiesDB.getActivitiesUserRegisteredCount(user.getUsername()));
	}


	private <T> List<T> toList(T o)
	{
		final List<T> $ = new ArrayList<T>();
		$.add(o);
		return $;
	}


	private final <T> List<T> concatLists(List<T> l1, List<T> l2)
	{
		final List<T> l = new ArrayList<T>(l1);
		l.addAll(l2);
		return l;
	}


	private void addUser(List<DBUser> users, DBUser u)
		throws ElementAlreadyExistsException,
		DatabaseUnkownFailureException,
		InvalidParamsException
	{
		usersDB.addUser(u);
		users.add(u);
	}


	private
		void
		deleteUser(
			List<DBUser> users,
			Map<String, List<DBPaidService>> user2service,
			Map<String, List<DBPaidTask>> user2task,
			Map<String, List<DBPaidService>> user2regService,
			Map<String, List<DBPaidTask>> user2regTask,
			DBUser u)
			throws InvalidParamsException,
			DatabaseUnkownFailureException,
			InvalidParameterException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException
	{
		assertTrue(usersDB.isUsernameTaken(u.getUsername()));
		assertTrue(usersDB.validateUser(u.getUsername(), u.getPassword()));
		assertEquals(u, usersDB.getUser(u.getUsername()));
		assertEquals(
			user2service.get(u.getUsername()),
			activitiesDB.getServicesOfferedByUser(u.getUsername(), 0, 100));
		assertEquals(
			user2task.get(u.getUsername()),
			activitiesDB.getTasksOfferedByUser(u.getUsername(), 0, 100));
		assertEquals(
			user2service.get(u.getUsername()).size()
				+ user2task.get(u.getUsername()).size(),
			activitiesDB.getActivitiesOfferedByUserCount(u.getUsername()));
		assertEquals(
			user2regService.get(u.getUsername()).size()
				+ user2regTask.get(u.getUsername()).size(),
			activitiesDB.getActivitiesUserRegisteredCount(u.getUsername()));

		multiDB.deleteUser(u.getUsername());
		users.remove(u);

		for (final DBPaidService s : user2service.get(u.getUsername()))
		{
			for (final DBUser user : users)
			{
				user2regService.get(user.getUsername()).remove(s);
			}
		}
		for (final DBPaidTask t : user2task.get(u.getUsername()))
		{
			for (final DBUser user : users)
			{
				user2regTask.get(user.getUsername()).remove(t);
			}
		}

		user2service.put(u.getUsername(), new ArrayList<DBPaidService>());
		user2task.put(u.getUsername(), new ArrayList<DBPaidTask>());
		user2regService.put(u.getUsername(), new ArrayList<DBPaidService>());
		user2regTask.put(u.getUsername(), new ArrayList<DBPaidTask>());

		assertFalse(usersDB.isUsernameTaken(u.getUsername()));
		assertFalse(usersDB.validateUser(u.getUsername(), u.getPassword()));
		assertNull(usersDB.getUser(u.getUsername()));
		assertEquals(
			user2service.get(u.getUsername()),
			activitiesDB.getServicesOfferedByUser(u.getUsername(), 0, 100));
		assertEquals(
			user2task.get(u.getUsername()),
			activitiesDB.getTasksOfferedByUser(u.getUsername(), 0, 100));
		assertEquals(
			user2service.get(u.getUsername()).size()
				+ user2task.get(u.getUsername()).size(),
			activitiesDB.getActivitiesOfferedByUserCount(u.getUsername()));
		assertEquals(
			user2regService.get(u.getUsername()).size()
				+ user2regTask.get(u.getUsername()).size(),
			activitiesDB.getActivitiesUserRegisteredCount(u.getUsername()));

	}


	private int randInt(int min, int max)
	{
		final int $ = rand.nextInt(max - min + 1) + min;
		return $;
	}


	// /**
	// * @param users
	// * @param user2service
	// * @param user2task
	// * @param i
	// * @throws InvalidParamsException
	// * @throws DatabaseUnkownFailureException
	// * @throws ElementAlreadyExistsException
	// */
	// private void addUsers(
	// List<DBUser> users,
	// Map<String, List<DBPaidService>> user2service,
	// Map<String, List<DBPaidTask>> user2task,
	// int numUsers)
	// throws ElementAlreadyExistsException,
	// DatabaseUnkownFailureException,
	// InvalidParamsException
	// {
	// for (int i = 0; i < numUsers; i++)
	// {
	// final DBUser user = new DBUser("u" + i, "pass" + i, "name" + i, 0);
	// users.add(user);
	// usersDB.addUser(user);
	// }
	// for (final DBUser u : users)
	// {
	// user2service.put(u.getUsername(), new ArrayList<DBPaidService>());
	// user2task.put(u.getUsername(), new ArrayList<DBPaidTask>());
	// }
	// }

	private void addUsers(
		List<DBUser> users,
		Map<String, List<DBPaidService>> user2service,
		Map<String, List<DBPaidTask>> user2task,
		Map<String, List<DBPaidService>> user2regService,
		Map<String, List<DBPaidTask>> user2regTask,
		int numUsers)
		throws ElementAlreadyExistsException,
		DatabaseUnkownFailureException,
		InvalidParamsException
	{
		for (int i = 0; i < numUsers; i++)
		{
			final DBUser user = new DBUser("u" + i, "pass" + i, "name" + i, 0);
			users.add(user);
			usersDB.addUser(user);
		}
		for (final DBUser u : users)
		{
			user2service.put(u.getUsername(), new ArrayList<DBPaidService>());
			user2task.put(u.getUsername(), new ArrayList<DBPaidTask>());
			user2regService
				.put(u.getUsername(), new ArrayList<DBPaidService>());
			user2regTask.put(u.getUsername(), new ArrayList<DBPaidTask>());
		}
	}


	private
		void
		assertUsersEqual(
			List<DBUser> users,
			Map<String, List<DBPaidService>> user2service,
			Map<String, List<DBPaidTask>> user2task,
			Map<String, List<DBPaidService>> user2regService,
			Map<String, List<DBPaidTask>> user2regTask)
			throws com.servicebook.database.exceptions.paidActivities.InvalidParameterException,
			DatabaseUnkownFailureException,
			InvalidParamsException
	{
		for (final DBUser u : users)
		{
			assertEquals(u, usersDB.getUser(u.getUsername()));

			assertEquals(
				user2service.get(u.getUsername()),
				activitiesDB.getServicesOfferedByUser(u.getUsername(), 0, 100));
			assertEquals(
				user2task.get(u.getUsername()),
				activitiesDB.getTasksOfferedByUser(u.getUsername(), 0, 100));
			assertEquals(
				user2service.get(u.getUsername()).size()
					+ user2task.get(u.getUsername()).size(),
				activitiesDB.getActivitiesOfferedByUserCount(u.getUsername()));

			assertEquals(
				user2regService.get(u.getUsername()),
				activitiesDB.getServicesUserRegistered(u.getUsername(), 0, 100));
			assertEquals(
				user2regTask.get(u.getUsername()),
				activitiesDB.getTasksUserRegistered(u.getUsername(), 0, 100));
			assertEquals(
				user2regService.get(u.getUsername()).size()
					+ user2regTask.get(u.getUsername()).size(),
				activitiesDB.getActivitiesUserRegisteredCount(u.getUsername()));
		}
	}


	private void addUserActivitiesToDB(
		List<DBUser> users,
		Map<String, List<DBPaidService>> user2service,
		Map<String, List<DBPaidTask>> user2task)
		throws InvalidParameterException,
		DatabaseUnkownFailureException
	{
		for (final DBUser u : users)
		{
			for (final DBPaidService service : user2service
				.get(u.getUsername()))
			{
				multiDB.addPaidService(service);
			}
			for (final DBPaidTask task : user2task.get(u.getUsername()))
			{
				multiDB.addPaidTask(task);
			}
		}
	}


	private void fillUsersActivities(
		List<DBUser> users,
		Map<String, List<DBPaidService>> user2service,
		Map<String, List<DBPaidTask>> user2task,
		int maxServices,
		int maxTasks)
	{
		int count = 0;
		for (final DBUser u : users)
		{
			count++;
			List<DBPaidService> services = null;
			List<DBPaidTask> tasks = null;
			services = user2service.get(u.getUsername());
			tasks = user2task.get(u.getUsername());

			for (int i = 0; i < randInt(1, maxServices); i++)
			{
				services.add(new DBPaidService(u.getUsername() + "_s" + i, u
					.getUsername(), randInt(1, 10), randInt(1, 6), 0));
			}
			for (int i = 0; i < randInt(1, maxTasks); i++)
			{
				tasks.add(new DBPaidTask(u.getUsername() + "_t" + i, u
					.getUsername(), randInt(1, 5), randInt(2, 4), 0));
			}
		}
	}


	// private void registerToService(
	// MultiTableDatabaseImpl multiDB,
	// DBPaidService s,
	// DBUser creator,
	// DBUser registering)
	// throws DatabaseUnkownFailureException,
	// InvalidParameterException
	// {
	// registerToService(multiDB, s, creator, registering, null);
	// }

	private void registerToService(
		MultiTableDatabaseImpl multiDB,
		DBPaidService s,
		DBUser creator,
		DBUser registering,
		Map<String, List<DBPaidService>> user2regService)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		assertTrue(multiDB.registerToActivity(
			s.getId(),
			registering.getUsername()));
		assertEquals(s.getUsername(), creator.getUsername());
		s.setNumRegistered((short) (s.getNumRegistered() + 1));
		creator.addBalance(1);
		registering.addBalance(-1);

		if (user2regService != null)
		{
			user2regService.get(registering.getUsername()).add(s);
			user2regService.get(registering.getUsername()).sort(
				(DBPaidService s1, DBPaidService s2) -> s1
					.getTitle()
					.compareTo(s2.getTitle()));
		}
	}


	//
	//
	// private void unregisterFromService(
	// MultiTableDatabaseImpl multiDB,
	// DBPaidService s,
	// DBUser creator,
	// DBUser unregistering)
	// throws DatabaseUnkownFailureException,
	// InvalidParameterException
	// {
	// unregisterFromService(multiDB, s, creator, unregistering, null);
	// }

	private void unregisterFromService(
		MultiTableDatabaseImpl multiDB,
		DBPaidService s,
		DBUser creator,
		DBUser unregistering,
		Map<String, List<DBPaidService>> user2regService)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		assertTrue(multiDB.unregisterFromActivity(
			s.getId(),
			unregistering.getUsername()));
		assertEquals(s.getUsername(), creator.getUsername());
		s.setNumRegistered((short) (s.getNumRegistered() - 1));
		creator.addBalance(-1);
		unregistering.addBalance(1);

		if (user2regService != null)
		{
			user2regService.get(unregistering.getUsername()).remove(s);
		}
	}


	// private void registerToTask(
	// MultiTableDatabaseImpl multiDB,
	// DBPaidTask t,
	// DBUser creator,
	// DBUser registering)
	// throws DatabaseUnkownFailureException,
	// InvalidParameterException
	// {
	// registerToTask(multiDB, t, creator, registering, null);
	// }

	private void registerToTask(
		MultiTableDatabaseImpl multiDB,
		DBPaidTask t,
		DBUser creator,
		DBUser registering,
		Map<String, List<DBPaidTask>> user2regTask)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		assertTrue(multiDB.registerToActivity(
			t.getId(),
			registering.getUsername()));
		assertEquals(t.getUsername(), creator.getUsername());
		t.setNumRegistered((short) (t.getNumRegistered() + 1));
		creator.addBalance(-1);
		registering.addBalance(1);

		if (user2regTask != null)
		{
			user2regTask.get(registering.getUsername()).add(t);
			user2regTask.get(registering.getUsername()).sort(
				(DBPaidTask t1, DBPaidTask t2) -> t1.getTitle().compareTo(
					t2.getTitle()));
		}
	}


	// private void unregisterFromTask(
	// MultiTableDatabaseImpl multiDB,
	// DBPaidTask t,
	// DBUser creator,
	// DBUser unregistering)
	// throws DatabaseUnkownFailureException,
	// InvalidParameterException
	// {
	// unregisterFromTask(multiDB, t, creator, unregistering, null);
	// }

	private void unregisterFromTask(
		MultiTableDatabaseImpl multiDB,
		DBPaidTask t,
		DBUser creator,
		DBUser unregistering,
		Map<String, List<DBPaidTask>> user2regTask)
		throws DatabaseUnkownFailureException,
		InvalidParameterException
	{
		assertTrue(multiDB.unregisterFromActivity(
			t.getId(),
			unregistering.getUsername()));
		assertEquals(t.getUsername(), creator.getUsername());
		t.setNumRegistered((short) (t.getNumRegistered() - 1));
		creator.addBalance(1);
		unregistering.addBalance(-1);

		if (user2regTask != null)
		{
			user2regTask.get(unregistering.getUsername()).remove(t);
		}
	}



	private final Random rand = new Random();

	private final String schema;

	private UsersDatabase usersDB;

	private FriendshipsDatabase friendsDB;

	private PaidActivitiesDatabase activitiesDB;

	private MultiTableDatabaseImpl multiDB;

	private final FriendsTableInfo friendsTableInfo;

	private BasicDataSource ds;

}
