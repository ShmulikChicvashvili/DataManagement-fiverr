package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.UsersDatabase;
import com.servicebook.database.PaidActivitiesDatabase.ActivityStatus;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.ReflexiveFriendshipException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;
import com.servicebook.database.exceptions.users.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.implementation.FriendshipsDatabaseImpl;
import com.servicebook.database.implementation.MultiTableDatabaseImpl;
import com.servicebook.database.implementation.PaidActivitiesDatabaseImpl;
import com.servicebook.database.implementation.UsersDatabaseImpl;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;
import com.servicebook.database.primitives.DBUser;

public class MultiTableTDatabaseTest {

	private class FriendsTableInfo {
		/**
		 * @param tableName
		 * @param userColumn
		 * @param friendColumn
		 */
		public FriendsTableInfo(String tableName, String userColumn,
				String friendColumn) {
			super();
			this.tableName = tableName;
			this.userColumn = userColumn;
			this.friendColumn = friendColumn;
		}

		public String tableName;

		public String userColumn;

		public String friendColumn;
	}

	public MultiTableTDatabaseTest() {
		schema = "servicebook_db";
		friendsTableInfo = new FriendsTableInfo("friendships",
				"first_username", "second_userame");
	}

	@Before
	public void setUp() throws Exception {
		ds = new BasicDataSource();
		ds.setDefaultAutoCommit(false);
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername("root");
		ds.setPassword("root");
		ds.setUrl("jdbc:mysql://localhost/");

		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement()) {
			String dropQuery = "";

			dropQuery = "DROP TABLE IF EXISTS `" + schema + "`.`users`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `" + schema + "`.`"
					+ friendsTableInfo.tableName + "`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `" + schema + "`.`activities`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `" + schema
					+ "`.`activities_registrations`;";
			stmt.execute(dropQuery);

			conn.commit();
		}

		usersDB = new UsersDatabaseImpl("users", schema, ds);
		friendsDB = new FriendshipsDatabaseImpl(friendsTableInfo.tableName,
				"users", schema, ds);
		activitiesDB = new PaidActivitiesDatabaseImpl("activities",
				"activities_registrations", schema, ds,
				friendsTableInfo.tableName, friendsTableInfo.userColumn,
				friendsTableInfo.friendColumn);
		multiDB = new MultiTableDatabaseImpl(schema, ds, friendsDB,
				activitiesDB, usersDB);
	}

	@After
	public void tearDown() throws Exception {

		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement()) {
			String dropQuery = "";

			dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`users`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `" + schema + "`.`"
					+ friendsTableInfo.tableName + "`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`activities`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`activities_registrations`;";
			stmt.execute(dropQuery);

			conn.commit();
		}

		ds.close();
	}

	@Test
	public void testAddPaidActivity()
			throws DatabaseUnkownFailureException,
			ElementAlreadyExistsException,
			InvalidParamsException,
			InvalidParameterException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException {
		try {
			multiDB.addPaidService(null);
			fail();
		} catch (InvalidParameterException e) {
		}
		try {
			multiDB.addPaidTask(null);
			fail();
		} catch (InvalidParameterException e) {
		}

		String username = "u1";
		DBUser user = new DBUser(username, "123", username, 0);
		DBPaidService s = new DBPaidService("s1", username, 2, 3, 0);
		DBPaidTask t = new DBPaidTask("t1", username, 21, 5, 0);
		try {
			multiDB.addPaidService(s);
			fail();
		} catch (InvalidParameterException e) {
		}
		try {
			multiDB.addPaidTask(t);
			fail();
		} catch (InvalidParameterException e) {
		}

		List<DBPaidService> services = new ArrayList<DBPaidService>();
		List<DBPaidTask> tasks = new ArrayList<DBPaidTask>();

		usersDB.addUser(user);

		int num = 10;
		for (int i = 0; i < num; i++) {
			services.add(new DBPaidService("s" + i, username, i + 3, i * 3 + 1,
					0));
			tasks.add(new DBPaidTask("t" + i, username, i * 2 + 5, i + 3, 0));
		}

		for (int i = 0; i < num; i++) {
			multiDB.addPaidService(services.get(i));
			assertEquals(ActivityStatus.SERVICE,
					activitiesDB.getActivityStatus(services.get(i).getId()));

			multiDB.addPaidTask(tasks.get(i));
			assertEquals(ActivityStatus.TASK,
					activitiesDB.getActivityStatus(tasks.get(i).getId()));
		}

		assertEquals(services,
				activitiesDB.getServicesOfferedByUser(username, 0, num));
		assertEquals(tasks,
				activitiesDB.getTasksOfferedByUser(username, 0, num));

	}

	@Test
	public void testDeleteActivity()
			throws DatabaseUnkownFailureException,
			InvalidParameterException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException,
			ElementAlreadyExistsException,
			InvalidParamsException,
			com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException,
			ReflexiveFriendshipException {
		try {
			multiDB.deleteActivity(0);
			fail();
		} catch (InvalidParameterException e) {
		}

		String username1 = "u1", username2 = "u2";
		DBUser u1 = new DBUser(username1, "123", "user", 0);
		DBUser u2 = new DBUser(username2, "321", "bla", 0);

		List<DBPaidService> services = new ArrayList<DBPaidService>();
		List<DBPaidTask> tasks = new ArrayList<DBPaidTask>();

		int num = 10;
		for (int i = 0; i < num; i++) {
			services.add(new DBPaidService("s" + i, username1, i + 3,
					i * 3 + 1, 0));
			tasks.add(new DBPaidTask("t" + i, username1, i * 2 + 5, i + 3, 0));
		}

		usersDB.addUser(u1);
		usersDB.addUser(u2);
		multiDB.addFriendship(username1, username2);

		for (int i = 0; i < num; i++) {
			multiDB.addPaidService(services.get(i));
			multiDB.addPaidTask(tasks.get(i));

			multiDB.registerToActivity(services.get(i).getId(), username2);
			services.get(i).setNumRegistered((short) 1);
		}

		for (int i = num - 1; i >= 0; i -= 2) {
			multiDB.deleteActivity(services.get(i).getId());
			services.remove(i);
			multiDB.deleteActivity(tasks.get(i).getId());
			tasks.remove(i);
		}

		assertEquals(services,
				activitiesDB.getServicesOfferedByUser(username1, 0, num));
		assertEquals(tasks,
				activitiesDB.getTasksOfferedByUser(username1, 0, num));

	}

	@Test
	public void testAddFriendship()
			throws com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException,
			DatabaseUnkownFailureException, ReflexiveFriendshipException,
			InvalidParameterException, ElementAlreadyExistsException,
			InvalidParamsException {
		String username1 = "u1", username2 = "u2";
		DBUser u1 = new DBUser(username1, "123", "Dan", 0);
		DBUser u2 = new DBUser(username2, "321", "Gal", 0);
		try {
			multiDB.addFriendship(null, username2);
			multiDB.addFriendship(username1, null);
			fail();
		} catch (InvalidParameterException e) {
		}
		try {
			multiDB.addFriendship(username1, username1);
			fail();
		} catch (ReflexiveFriendshipException e) {
		}
		try {
			// users don't exist
			multiDB.addFriendship(username1, username2);
			fail();
		} catch (InvalidParameterException e) {
		}

		usersDB.addUser(u1);
		try {
			// u2 still doesn't exist
			multiDB.addFriendship(username1, username2);
			multiDB.addFriendship(username2, username1);
			fail();
		} catch (InvalidParameterException e) {
		}
		usersDB.addUser(u2);

		multiDB.addFriendship(username1, username2);
		try {
			// already friends
			multiDB.addFriendship(username1, username2);
			fail();
		} catch (com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException e) {
		}
		try {
			// already friends. check other way.
			multiDB.addFriendship(username2, username1);
			fail();
		} catch (com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException e) {
		}

	}

	@Test
	public void testRegisterToActivity()
			throws DatabaseUnkownFailureException,
			InvalidParameterException,
			ElementAlreadyExistsException,
			InvalidParamsException,
			com.servicebook.database.exceptions.paidActivities.InvalidParameterException,
			com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException,
			ReflexiveFriendshipException {
		List<DBUser> users = new ArrayList<DBUser>();
		Map<String, List<DBPaidService>> user2service = new HashMap<String, List<DBPaidService>>();
		Map<String, List<DBPaidTask>> user2task = new HashMap<String, List<DBPaidTask>>();

		try {
			multiDB.registerToActivity(0, "username");
			fail();
		} catch (InvalidParameterException e) {
		}
		try {
			multiDB.registerToActivity(1, null);
			fail();
		} catch (InvalidParameterException e) {
		}

		for (int i = 0; i < 4; i++) {
			DBUser user = new DBUser("u" + i, "pass" + i, "name" + i, 0);
			users.add(user);
			usersDB.addUser(user);
		}
		DBPaidService s = new DBPaidService("s1", users.get(0).getUsername(),
				2, 3, 0);
		multiDB.addPaidService(s);

		// Either the activity or the user does not exist
		assertFalse(multiDB.registerToActivity(s.getId() + 1, "username"));
		assertFalse(multiDB.registerToActivity(s.getId(), "username"));
		assertFalse(multiDB.registerToActivity(s.getId() + 1, users.get(0)
				.getUsername()));

		multiDB.deleteActivity(s.getId());
		assertEquals(ActivityStatus.NOT_EXIST,
				activitiesDB.getActivityStatus(s.getId()));

		// TODO Not finished.

	}

	@Test
	public void testUnregisterFromActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteUser() {
		fail("Not yet implemented");
	}

	private String schema;

	private UsersDatabase usersDB;
	private FriendshipsDatabase friendsDB;
	private PaidActivitiesDatabase activitiesDB;
	private MultiTableDatabaseImpl multiDB;

	private final FriendsTableInfo friendsTableInfo;

	private BasicDataSource ds;

}
