package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.servicebook.database.PaidActivitiesDatabase.ActivityStatus;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.paidActivities.ElementAlreadyExistException;
import com.servicebook.database.exceptions.paidActivities.ElementNotExistException;
import com.servicebook.database.exceptions.paidActivities.FriendshipsTableNotExist;
import com.servicebook.database.exceptions.paidActivities.InvalidParameterException;
import com.servicebook.database.exceptions.paidActivities.TableCreationException;
import com.servicebook.database.implementation.PaidActivitiesDatabaseImpl;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

@SuppressWarnings("javadoc")
public class PaidActivitiesDatabaseTest {

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
			creationQuery = String
					.format("CREATE TABLE IF NOT EXISTS %s (`%s` VARCHAR(255) NOT NULL, `%s` VARCHAR(255) NOT NULL, PRIMARY KEY(`%s`, `%s`))",
							tableName, userColumn, friendColumn, userColumn,
							friendColumn);
		}

		public String creationQuery;

		public String tableName;

		public String userColumn;

		public String friendColumn;
	}

	private final FriendsTableInfo friendsTableInfo;

	/**
	 *
	 */
	public PaidActivitiesDatabaseTest() {
		friendsTableInfo = new FriendsTableInfo(
				"`servicebook_db`.`friendships`", "first_username",
				"second_userame");
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
			String dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`activities`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`activities_registrations`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS " + friendsTableInfo.tableName
					+ ";";
			stmt.execute(dropQuery);

			stmt.execute(friendsTableInfo.creationQuery);

			conn.commit();
		} catch (final SQLException e) {
			e.printStackTrace();
			fail();
		}

		try {
			paidActivityDB = new PaidActivitiesDatabaseImpl("activities",
					"activities_registrations", "servicebook_db", ds,
					"friendships", friendsTableInfo.userColumn,
					friendsTableInfo.friendColumn);
		} catch (final TableCreationException e) {
			fail("Unable to create paidActivityDB");
		}
	}

	@After
	public void tearDown() throws Exception {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
		ds.close();
		// try (
		// Connection conn = ds.getConnection();
		// Statement stmt = conn.createStatement())
		// {
		// String dropQuery = "DROP TABLE `servicebook_db`.`activities`;";
		// stmt.execute(dropQuery);
		// dropQuery =
		// "DROP TABLE `servicebook_db`.`activities_registrations`;";
		// stmt.execute(dropQuery);
		//
		// conn.commit();
		// } catch (final SQLException e)
		// {
		// e.printStackTrace();
		// }
	}

	@Test
	public void testAddPaidService() throws DatabaseUnkownFailureException,
			InvalidParameterException, SQLException {
		conn = ds.getConnection();
		try {
			paidActivityDB.addPaidService(null, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		final DBPaidService sBad1 = new DBPaidService("s1", "u1", 0, 1, 0);
		final DBPaidService sBad2 = new DBPaidService("s1", "u1", 1, 0, 0);

		try {
			paidActivityDB.addPaidService(sBad1, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		try {
			paidActivityDB.addPaidService(sBad2, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		try (Connection conn = ds.getConnection()) {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			int count = 0;
			for (int i = 1; i <= 100; i++) {
				final DBPaidService s = new DBPaidService("s"
						+ Integer.toString(i), "user", i, i / 2 + 1, i);

				assertEquals(ActivityStatus.NOT_EXIST,
						paidActivityDB.getActivityStatus(i));
				assertEquals(++count, paidActivityDB.addPaidService(s, conn));
				conn.commit();
				assertEquals(ActivityStatus.SERVICE,
						paidActivityDB.getActivityStatus(count));
				s.setNumRegistered((short) 0);
				assertEquals(s, paidActivityDB.getActivity(i, conn));
			}
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		}
		conn.close();
	}

	@Test
	public void testAddPaidTask() throws DatabaseUnkownFailureException,
			InvalidParameterException, SQLException {
		conn = ds.getConnection();
		try {
			paidActivityDB.addPaidTask(null, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		final DBPaidTask tBad1 = new DBPaidTask("t1", "u1", 0, 1, 0);
		final DBPaidTask tBad2 = new DBPaidTask("t1", "u1", 1, 0, 0);

		try {
			paidActivityDB.addPaidTask(tBad1, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		try {
			paidActivityDB.addPaidTask(tBad2, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		try (Connection conn = ds.getConnection()) {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			int count = 0;
			for (int i = 1; i <= 100; i++) {
				final DBPaidTask t = new DBPaidTask("s" + Integer.toString(i),
						"user", i, i / 2 + 1, i);

				assertEquals(ActivityStatus.NOT_EXIST,
						paidActivityDB.getActivityStatus(i));
				assertEquals(++count, paidActivityDB.addPaidTask(t, conn));
				conn.commit();
				assertEquals(ActivityStatus.TASK,
						paidActivityDB.getActivityStatus(count));
				t.setNumRegistered((short) 0);
				assertEquals(t, paidActivityDB.getActivity(t.getId(), conn));
			}
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

		}
		conn.close();
	}

	@Test
	public void testSimpleDeletePaidActivity()
			throws DatabaseUnkownFailureException, InvalidParameterException,
			SQLException {
		int id;
		conn = ds.getConnection();

		// Deleting not existing
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(1));
		paidActivityDB.deletePaidActivity(1, conn);
		conn.commit();
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(1));

		final DBPaidService s1 = new DBPaidService("s1", "u1", 1, 1, 0);
		final DBPaidTask t1 = new DBPaidTask("t1", "u2", 2, 3, 1);

		// Adding and deleting with commit and with roll back
		id = paidActivityDB.addPaidService(s1, conn);
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id, conn));
		paidActivityDB.deletePaidActivity(id, conn);
		conn.commit();
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(id, conn));

		id = paidActivityDB.addPaidTask(t1, conn);
		assertEquals(ActivityStatus.TASK,
				paidActivityDB.getActivityStatus(id, conn));
		paidActivityDB.deletePaidActivity(id, conn);
		conn.commit();
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(id, conn));

		id = paidActivityDB.addPaidService(s1, conn);
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id, conn));
		conn.commit();
		paidActivityDB.deletePaidActivity(id, conn);
		conn.rollback();
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id, conn));

		paidActivityDB.deletePaidActivity(id, conn);
		conn.commit();
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(id));

		conn.close();
	}

	@Test
	public void testBatchDeletePaidActivity()
			throws DatabaseUnkownFailureException, InvalidParameterException,
			SQLException {
		conn = ds.getConnection();

		final DBPaidService s1 = new DBPaidService("s1", "u1", 1, 1, 0);
		final DBPaidTask t1 = new DBPaidTask("t1", "u2", 2, 3, 1);
		int id = 1;

		// adding services and tasks
		final int firstBatchId = 1;
		for (int i = 1; i <= 100; i++) {
			id = paidActivityDB.addPaidService(s1, conn);
			id = paidActivityDB.addPaidTask(t1, conn);
		}
		conn.commit();

		// deleting with commit after each command
		for (int i = id - 100 * 2 + 1; i <= id; i += 3) {
			assertEquals((i - firstBatchId) % 2 == 0 ? ActivityStatus.SERVICE
					: ActivityStatus.TASK, paidActivityDB.getActivityStatus(i));
			paidActivityDB.deletePaidActivity(i, conn);
			conn.commit();
			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(i));
			paidActivityDB.deletePaidActivity(i, conn);
			conn.commit();
			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(i));
		}

		// Deleting and commit only at the end
		for (int i = id - 100 * 2 + 2; i <= id; i += 3) {
			final ActivityStatus status = (i - firstBatchId) % 2 == 0 ? ActivityStatus.SERVICE
					: ActivityStatus.TASK;
			assertEquals(status, paidActivityDB.getActivityStatus(i));
			paidActivityDB.deletePaidActivity(i, conn);
			assertEquals(status, paidActivityDB.getActivityStatus(i));
		}
		conn.commit();
		for (int i = id - 100 * 2 + 2; i <= id; i += 3) {
			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(i));
		}

		conn.close();
	}

	@Test
	public void testDeleteUserPaidActivities()
			throws DatabaseUnkownFailureException, InvalidParameterException,
			SQLException {
		conn = ds.getConnection();
		final String u1 = "u1";

		try {
			paidActivityDB.deleteUserPaidActivities(null, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try (Connection c = ds.getConnection()) {
			c.close();
			paidActivityDB.deleteUserPaidActivities(u1, c);
			fail();
		} catch (final InvalidParameterException e) {
		}

		final List<DBPaidService> services = new ArrayList<DBPaidService>();
		final List<DBPaidTask> tasks = new ArrayList<DBPaidTask>();

		for (int i = 0; i < 4; i++) {
			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(i + 1));
			final DBPaidService s = new DBPaidService("s" + i, u1, i * 2 + 3,
					i * 4 + 1, 0);
			services.add(s);
			paidActivityDB.addPaidService(s, conn);
			conn.commit();
			assertEquals(ActivityStatus.SERVICE,
					paidActivityDB.getActivityStatus(s.getId()));
		}
		for (int i = 0; i < 7; i++) {
			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(i + 1 + services.size()));
			final DBPaidTask t = new DBPaidTask("t" + i, u1, i * 7 + 5,
					i * 3 + 9, 0);
			tasks.add(t);
			paidActivityDB.addPaidTask(t, conn);
			conn.commit();
			assertEquals(ActivityStatus.TASK,
					paidActivityDB.getActivityStatus(t.getId()));
		}

		paidActivityDB.deleteUserPaidActivities(u1, conn);
		conn.commit();

		for (final DBPaidService s : services) {
			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(s.getId()));
		}
		for (final DBPaidTask t : tasks) {
			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(t.getId()));
		}

		assertEquals(new ArrayList<DBPaidService>(),
				paidActivityDB.getServicesOfferedByUser(u1, 0, 10));
		assertEquals(new ArrayList<DBPaidTask>(),
				paidActivityDB.getTasksOfferedByUser(u1, 0, 10));

	}

	@Test
	public void testGetActivityStatus() throws DatabaseUnkownFailureException,
			SQLException, InvalidParameterException {
		conn = ds.getConnection();

		try {
			paidActivityDB.getActivityStatus(-1);
			fail();
		} catch (final InvalidParameterException e) {
		}

		try {
			paidActivityDB.getActivityStatus(-1, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		try {
			conn.close();
			paidActivityDB.getActivityStatus(20, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		conn = ds.getConnection();
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(1));
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(1, conn));

		final DBPaidService s1 = new DBPaidService("s1", "u1", 4, 4, 2);
		final int id = paidActivityDB.addPaidService(s1, conn);

		// w/o this commit the add is not seen, thus using this connection fails
		// to see the service was added
		conn.commit();
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id));
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id));
		// conn.commit();
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id, conn));

		conn.close();

	}

	@Test
	public void testRegisterToActivity() throws SQLException,
			DatabaseUnkownFailureException, InvalidParameterException,
			ElementAlreadyExistException {
		conn = ds.getConnection();

		try {
			paidActivityDB.registerToActivity(-1, "u1", conn);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try {
			paidActivityDB.registerToActivity(1, null, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try {
			conn.close();
			paidActivityDB.registerToActivity(1, "u1", conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		conn = ds.getConnection();

		paidActivityDB.registerToActivity(1, "u3", conn);
		try {
			paidActivityDB.registerToActivity(1, "u3", conn);
			fail();
		} catch (final ElementAlreadyExistException e) {
		}
		conn.rollback();

		final DBPaidService s1 = new DBPaidService("s1", "u1", 3, 1, 1);
		final DBPaidService s2 = new DBPaidService("S2", "u2", 2, 3, 0);
		final DBPaidTask t1 = new DBPaidTask("t1", "u1", 10, 5, 5);

		paidActivityDB.addPaidService(s1, conn);
		paidActivityDB.addPaidService(s2, conn);
		paidActivityDB.addPaidTask(t1, conn);

		paidActivityDB.registerToActivity(s1.getId(), "u2", conn);
		paidActivityDB.registerToActivity(s1.getId(), "u3", conn);
		paidActivityDB.registerToActivity(s2.getId(), "u3", conn);
		paidActivityDB.registerToActivity(t1.getId(), "u1", conn);

		conn.commit();

		try {
			paidActivityDB.registerToActivity(s1.getId(), "u2", conn);
			fail();
		} catch (final ElementAlreadyExistException e) {
		}
		try {
			paidActivityDB.registerToActivity(s2.getId(), "u3", conn);
			fail();
		} catch (final ElementAlreadyExistException e) {
		}
		try {
			paidActivityDB.registerToActivity(t1.getId(), "u1", conn);
			fail();
		} catch (final ElementAlreadyExistException e) {
		}

		conn.close();
	}

	@Test
	public void testUnregisterFromActivity() throws SQLException,
			DatabaseUnkownFailureException, InvalidParameterException,
			ElementAlreadyExistException, ElementNotExistException {
		conn = ds.getConnection();

		try {
			paidActivityDB.unregisterFromActivity(-1, "u1", conn);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try {
			paidActivityDB.unregisterFromActivity(1, null, conn);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try {
			paidActivityDB.unregisterFromActivity(1, "u1", conn);
			fail();
		} catch (ElementNotExistException e) {
		}
		try {
			conn.close();
			paidActivityDB.unregisterFromActivity(1, "u1", conn);
			fail();
		} catch (final InvalidParameterException e) {
		}

		conn = ds.getConnection();

		final DBPaidService s1 = new DBPaidService("s1", "u1", 3, 1, 1);
		final DBPaidService s2 = new DBPaidService("S2", "u2", 2, 3, 0);
		final DBPaidTask t1 = new DBPaidTask("t1", "u1", 10, 5, 5);

		paidActivityDB.addPaidService(s1, conn);
		paidActivityDB.addPaidService(s2, conn);
		paidActivityDB.addPaidTask(t1, conn);

		// register and unregister
		paidActivityDB.registerToActivity(s1.getId(), "u2", conn);
		conn.commit();
		paidActivityDB.unregisterFromActivity(s1.getId(), "u2", conn);
		conn.commit();

		// can register after unregister
		paidActivityDB.registerToActivity(s1.getId(), "u2", conn);
		paidActivityDB.registerToActivity(s1.getId(), "u3", conn);
		paidActivityDB.unregisterFromActivity(s1.getId(), "u2", conn);
		paidActivityDB.registerToActivity(s1.getId(), "u2", conn);
		try {
			paidActivityDB.registerToActivity(s1.getId(), "u2", conn);
			fail();
		} catch (final ElementAlreadyExistException e) {
		}

		// register with some users, and unregister only part of them.
		for (int i = 0; i < 20; i++) {
			paidActivityDB.registerToActivity(t1.getId(), "u" + i, conn);
		}
		conn.commit();
		for (int i = 0; i < 20; i += 3) {
			paidActivityDB.unregisterFromActivity(t1.getId(), "u" + i, conn);
		}
		conn.commit();

		for (int i = 0; i < 20; i++) {
			if (i % 3 == 0) {
				paidActivityDB.registerToActivity(t1.getId(), "u" + i, conn);
			}
			try {
				paidActivityDB.registerToActivity(t1.getId(), "u" + i, conn);
				fail();
			} catch (final ElementAlreadyExistException e) {
			}
		}

		conn.close();
	}

	@Test
	public void testGetActivitiesOfferedByUserExceptions()
			throws DatabaseUnkownFailureException {
		try {
			paidActivityDB.getServicesOfferedByUser(null, 0, 1);
			paidActivityDB.getTasksOfferedByUser(null, 0, 1);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try {
			paidActivityDB.getServicesOfferedByUser("u1", -1, 1);
			paidActivityDB.getTasksOfferedByUser("u1", -1, 1);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try {
			paidActivityDB.getServicesOfferedByUser("u1", 0, 0);
			paidActivityDB.getTasksOfferedByUser("u1", 0, 0);
			fail();
		} catch (final InvalidParameterException e) {
		}
	}

	@Test
	public void testGetActivitiesOfferedByUser()
			throws DatabaseUnkownFailureException, InvalidParameterException,
			SQLException, ElementAlreadyExistException {

		conn = ds.getConnection();

		final String u1 = "u1";
		final String u2 = "u2";

		assertEquals(new ArrayList<DBPaidService>(),
				paidActivityDB.getServicesOfferedByUser(u1, 0, 10));
		assertEquals(new ArrayList<DBPaidTask>(),
				paidActivityDB.getTasksOfferedByUser(u1, 0, 10));

		final Map<String, List<DBPaidService>> user2services = new HashMap<String, List<DBPaidService>>();
		final HashMap<String, List<DBPaidTask>> user2tasks = new HashMap<String, List<DBPaidTask>>();
		List<DBPaidService> services = new ArrayList<DBPaidService>();
		List<DBPaidTask> tasks = new ArrayList<DBPaidTask>();

		services.add(new DBPaidService("s1", u1, 5, 1, 0));
		services.add(new DBPaidService("s2", u1, 2, 2, 0));
		services.add(new DBPaidService("s3", u1, 3, 1, 0));
		tasks.add(new DBPaidTask("t1", u1, 1, 1, 0));
		tasks.add(new DBPaidTask("t2", u1, 1, 1, 0));

		user2services.put(u1, services);
		user2tasks.put(u1, tasks);

		services = new ArrayList<DBPaidService>();
		tasks = new ArrayList<DBPaidTask>();

		services.add(new DBPaidService("s1", u2, 1, 3, 0));
		services.add(new DBPaidService("s5", u2, 10, 4, 0));
		tasks.add(new DBPaidTask("t10", u2, 2, 1, 0));
		tasks.add(new DBPaidTask("t12", u2, 3, 5, 0));

		user2services.put(u2, services);
		user2tasks.put(u2, tasks);

		for (final List<DBPaidService> l : user2services.values()) {
			for (final DBPaidService s : l) {
				paidActivityDB.addPaidService(s, conn);
			}
		}
		for (final List<DBPaidTask> l : user2tasks.values()) {
			for (final DBPaidTask t : l) {
				paidActivityDB.addPaidTask(t, conn);
			}
		}

		/******** END OF INITIALIZATION ***********/

		/* test all activities for users */
		for (final Entry<String, List<DBPaidService>> e : user2services
				.entrySet()) {
			assertEquals(e.getValue(),
					paidActivityDB.getServicesOfferedByUser(e.getKey(), 0, 50));
		}
		for (final Entry<String, List<DBPaidTask>> e : user2tasks.entrySet()) {
			assertEquals(e.getValue(),
					paidActivityDB.getTasksOfferedByUser(e.getKey(), 0, 50));
		}

		/* test start and amount */
		assertEquals(user2services.get(u1).subList(1, 2),
				paidActivityDB.getServicesOfferedByUser(u1, 1, 1));
		assertEquals(new ArrayList<DBPaidTask>(),
				paidActivityDB.getServicesOfferedByUser(u2, 2, 19));

		/* test activities with registered users */

		DBPaidService s = user2services.get(u1).get(1);
		paidActivityDB.registerToActivity(s.getId(), u2, conn);
		paidActivityDB.registerToActivity(s.getId(), "u3", conn);
		conn.commit();
		s.setNumRegistered((short) 2);

		assertEquals(user2services.get(u1),
				paidActivityDB.getServicesOfferedByUser(u1, 0, 10));

		s = user2services.get(u1).get(0);
		paidActivityDB.registerToActivity(s.getId(), "u5", conn);
		conn.commit();
		s.setNumRegistered((short) 1);

		assertEquals(user2services.get(u1),
				paidActivityDB.getServicesOfferedByUser(u1, 0, 10));

		final DBPaidTask t = user2tasks.get(u2).get(1);
		paidActivityDB.registerToActivity(t.getId(), u1, conn);
		conn.commit();
		t.setNumRegistered((short) 1);

		assertEquals(user2tasks.get(u2).subList(0, 1),
				paidActivityDB.getTasksOfferedByUser(u2, 0, 1));
		assertEquals(user2tasks.get(u2).subList(1, 2),
				paidActivityDB.getTasksOfferedByUser(u2, 1, 1));

		/* test deleting an activity */
		s = user2services.get(u1).get(1);
		paidActivityDB.deletePaidActivity(s.getId(), conn);
		conn.commit();
		user2services.get(u1).remove(s);

		assertEquals(user2services.get(u1),
				paidActivityDB.getServicesOfferedByUser(u1, 0, 5));

		conn.close();
	}

	@Test
	public void testGetActivitiesOfferedToUserExceptions()
			throws DatabaseUnkownFailureException, FriendshipsTableNotExist,
			SQLException, InvalidParameterException {
		try {
			paidActivityDB.getServicesOfferedToUser(null, 0, 1);
			paidActivityDB.getTasksOfferedToUser(null, 0, 1);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try {
			paidActivityDB.getServicesOfferedToUser("u1", -1, 1);
			paidActivityDB.getTasksOfferedToUser("u1", -1, 1);
			fail();
		} catch (final InvalidParameterException e) {
		}
		try {
			paidActivityDB.getServicesOfferedToUser("u1", 0, 0);
			paidActivityDB.getTasksOfferedToUser("u1", 0, 0);
			fail();
		} catch (final InvalidParameterException e) {
		}

		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute("DROP TABLE IF EXISTS " + friendsTableInfo.tableName);
			conn.commit();

			paidActivityDB.getServicesOfferedToUser("u1", 0, 1);
			paidActivityDB.getTasksOfferedToUser("u1", 0, 1);
			fail();
		} catch (final FriendshipsTableNotExist e) {
		}
	}

	private final <T> List<T> joinLists(List<T> l1, List<T> l2) {
		final List<T> l = new ArrayList<T>(l1);
		l.addAll(l2);
		return l;
	}

	@Test
	public void testGetActivitiesOfferedToUser()
			throws DatabaseUnkownFailureException, InvalidParameterException,
			SQLException, ElementAlreadyExistException,
			FriendshipsTableNotExist {

		conn = ds.getConnection();

		final String u1 = "u1";
		final String u2 = "u2";
		final String u3 = "u3";
		final String u4 = "u4";

		assertEquals(new ArrayList<DBPaidService>(),
				paidActivityDB.getServicesOfferedToUser(u1, 0, 10));
		assertEquals(new ArrayList<DBPaidTask>(),
				paidActivityDB.getTasksOfferedToUser(u1, 0, 10));

		final Map<String, List<DBPaidService>> user2services = new HashMap<String, List<DBPaidService>>();
		final HashMap<String, List<DBPaidTask>> user2tasks = new HashMap<String, List<DBPaidTask>>();
		List<DBPaidService> services = new ArrayList<DBPaidService>();
		List<DBPaidTask> tasks = new ArrayList<DBPaidTask>();

		services.add(new DBPaidService("s1", u1, 5, 1, 0));
		services.add(new DBPaidService("s2", u1, 2, 2, 0));
		services.add(new DBPaidService("s3", u1, 3, 1, 0));
		tasks.add(new DBPaidTask("t1", u1, 1, 1, 0));
		tasks.add(new DBPaidTask("t2", u1, 1, 1, 0));
		user2services.put(u1, services);
		user2tasks.put(u1, tasks);

		services = new ArrayList<DBPaidService>();
		tasks = new ArrayList<DBPaidTask>();
		services.add(new DBPaidService("s1", u2, 1, 3, 0));
		services.add(new DBPaidService("s5", u2, 10, 4, 0));
		services.add(new DBPaidService("s6", u2, 8, 3, 0));
		tasks.add(new DBPaidTask("t12", u2, 3, 5, 0));
		user2services.put(u2, services);
		user2tasks.put(u2, tasks);

		services = new ArrayList<DBPaidService>();
		tasks = new ArrayList<DBPaidTask>();
		services.add(new DBPaidService("s20", u3, 12, 3, 0));
		services.add(new DBPaidService("s25", u3, 7, 4, 0));
		services.add(new DBPaidService("s26", u3, 16, 5, 0));
		services.add(new DBPaidService("s27", u3, 12, 8, 0));
		tasks.add(new DBPaidTask("t20", u3, 5, 6, 0));
		tasks.add(new DBPaidTask("t22", u3, 3, 5, 0));
		user2services.put(u3, services);
		user2tasks.put(u3, tasks);

		services = new ArrayList<DBPaidService>();
		tasks = new ArrayList<DBPaidTask>();
		services.add(new DBPaidService("s90", u4, 1, 3, 0));
		services.add(new DBPaidService("s93", u4, 10, 4, 0));
		tasks.add(new DBPaidTask("t91", u4, 7, 9, 0));
		tasks.add(new DBPaidTask("t97", u4, 12, 35, 0));
		user2services.put(u4, services);
		user2tasks.put(u4, tasks);

		for (final List<DBPaidService> l : user2services.values()) {
			for (final DBPaidService s : l) {
				paidActivityDB.addPaidService(s, conn);
			}
		}
		for (final List<DBPaidTask> l : user2tasks.values()) {
			for (final DBPaidTask t : l) {
				paidActivityDB.addPaidTask(t, conn);
			}
		}

		/******** END OF INITIALIZATION ***********/

		// test everyones offered activites are empty
		for (final String u : user2services.keySet()) {
			assertEquals(new ArrayList<DBPaidService>(),
					paidActivityDB.getServicesOfferedToUser(u, 0, 10));
			assertEquals(new ArrayList<DBPaidService>(),
					paidActivityDB.getTasksOfferedToUser(u, 0, 10));
		}

		// add u2 and u3 as friends
		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute(String.format(
					"INSERT INTO %s (%s,%s) VALUES ('%s','%s')",
					friendsTableInfo.tableName, friendsTableInfo.userColumn,
					friendsTableInfo.friendColumn, u2, u3));
			stmt.execute(String.format(
					"INSERT INTO %s (%s,%s) VALUES ('%s','%s')",
					friendsTableInfo.tableName, friendsTableInfo.userColumn,
					friendsTableInfo.friendColumn, u3, u2));

			conn.commit();
		}

		assertEquals(user2services.get(u3),
				paidActivityDB.getServicesOfferedToUser(u2, 0, 10));
		assertEquals(user2tasks.get(u3),
				paidActivityDB.getTasksOfferedToUser(u2, 0, 10));
		assertEquals(user2services.get(u2),
				paidActivityDB.getServicesOfferedToUser(u3, 0, 10));
		assertEquals(user2tasks.get(u2),
				paidActivityDB.getTasksOfferedToUser(u3, 0, 10));

		assertEquals(user2services.get(u3).subList(1, 3),
				paidActivityDB.getServicesOfferedToUser(u2, 1, 2));
		assertEquals(new ArrayList<DBPaidTask>(),
				paidActivityDB.getTasksOfferedToUser(u2, 5, 2));

		// add u3 and u4 as friends
		try (Connection conn = ds.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute(String.format(
					"INSERT INTO %s (%s,%s) VALUES ('%s','%s')",
					friendsTableInfo.tableName, friendsTableInfo.userColumn,
					friendsTableInfo.friendColumn, u4, u3));
			stmt.execute(String.format(
					"INSERT INTO %s (%s,%s) VALUES ('%s','%s')",
					friendsTableInfo.tableName, friendsTableInfo.userColumn,
					friendsTableInfo.friendColumn, u3, u4));

			conn.commit();
		}

		// test u2 and u4 have only u3 services and tasks
		assertEquals(user2services.get(u3),
				paidActivityDB.getServicesOfferedToUser(u2, 0, 10));
		assertEquals(user2tasks.get(u3),
				paidActivityDB.getTasksOfferedToUser(u2, 0, 10));
		assertEquals(user2services.get(u3),
				paidActivityDB.getServicesOfferedToUser(u4, 0, 10));
		assertEquals(user2tasks.get(u3),
				paidActivityDB.getTasksOfferedToUser(u4, 0, 10));

		// test u3 now has both u2 and u4 services and tasks
		assertEquals(joinLists(user2services.get(u2), user2services.get(u4)),
				paidActivityDB.getServicesOfferedToUser(u3, 0, 10));
		assertEquals(joinLists(user2tasks.get(u2), user2tasks.get(u4)),
				paidActivityDB.getTasksOfferedToUser(u3, 0, 10));

		DBPaidService s = user2services.get(u2).get(1);
		paidActivityDB.registerToActivity(s.getId(), "u5", conn);
		paidActivityDB.registerToActivity(s.getId(), "u6", conn);
		s.setNumRegistered((short) 2);

		s = user2services.get(u4).get(0);
		paidActivityDB.registerToActivity(s.getId(), "u5", conn);
		paidActivityDB.registerToActivity(s.getId(), "u7", conn);
		paidActivityDB.registerToActivity(s.getId(), "u13", conn);
		s.setNumRegistered((short) 3);

		DBPaidTask t = user2tasks.get(u2).get(0);
		paidActivityDB.registerToActivity(t.getId(), "u5", conn);
		t.setNumRegistered((short) 1);

		t = user2tasks.get(u3).get(1);
		paidActivityDB.registerToActivity(t.getId(), "u12", conn);
		paidActivityDB.registerToActivity(t.getId(), "u15", conn);
		paidActivityDB.registerToActivity(t.getId(), "u123", conn);
		t.setNumRegistered((short) 3);

		conn.commit();

		// test u2 and u4 have updated u3 services and tasks
		assertEquals(user2services.get(u3),
				paidActivityDB.getServicesOfferedToUser(u2, 0, 10));
		assertEquals(user2tasks.get(u3),
				paidActivityDB.getTasksOfferedToUser(u2, 0, 10));
		assertEquals(user2services.get(u3),
				paidActivityDB.getServicesOfferedToUser(u4, 0, 10));
		assertEquals(user2tasks.get(u3),
				paidActivityDB.getTasksOfferedToUser(u4, 0, 10));

		// test u3 now has both u2 and u4 updated services and tasks
		assertEquals(joinLists(user2services.get(u2), user2services.get(u4)),
				paidActivityDB.getServicesOfferedToUser(u3, 0, 10));
		assertEquals(joinLists(user2tasks.get(u2), user2tasks.get(u4)),
				paidActivityDB.getTasksOfferedToUser(u3, 0, 10));

		s = user2services.get(u4).get(0);
		paidActivityDB.deletePaidActivity(s.getId(), conn);
		conn.commit();
		user2services.get(u4).remove(s);

		// test u3 now has both u2 and u4 updated services and tasks
		assertEquals(joinLists(user2services.get(u2), user2services.get(u4)),
				paidActivityDB.getServicesOfferedToUser(u3, 0, 10));
		assertEquals(joinLists(user2tasks.get(u2), user2tasks.get(u4)),
				paidActivityDB.getTasksOfferedToUser(u3, 0, 10));

		conn.close();
	}

	BasicDataSource ds;

	Connection conn;

	PaidActivitiesDatabaseImpl paidActivityDB;

}
