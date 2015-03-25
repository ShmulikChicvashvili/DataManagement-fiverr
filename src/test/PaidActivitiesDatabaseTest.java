package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.servicebook.database.PaidActivitiesDatabase.ActivityStatus;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.paidActivities.InvalidParameterException;
import com.servicebook.database.exceptions.paidActivities.TableCreationException;
import com.servicebook.database.implementation.PaidActivitiesDatabaseImpl;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

public class PaidActivitiesDatabaseTest {

	BasicDataSource ds;

	PaidActivitiesDatabaseImpl paidActivityDB;

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
			String dropQuery = "DROP TABLE `servicebook_db`.`activities`;";
			stmt.execute(dropQuery);
			dropQuery = "DROP TABLE `servicebook_db`.`activities_registrations`;";
			stmt.execute(dropQuery);

			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			paidActivityDB = new PaidActivitiesDatabaseImpl("activities",
					"activities_registrations", "servicebook_db", ds);
		} catch (final TableCreationException e) {
			fail("Unable to create paidActivityDB");
		}
	}

	// @After
	// public void tearDown() throws Exception {
	//
	// try (Connection conn = ds.getConnection();
	// Statement stmt = conn.createStatement()) {
	// String dropQuery = "DROP TABLE `servicebook_db`.`activities`;";
	// stmt.execute(dropQuery);
	// dropQuery = "DROP TABLE `servicebook_db`.`activities_registrations`;";
	// stmt.execute(dropQuery);
	//
	// conn.commit();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }

	@Test
	public void testAddPaidService() throws DatabaseUnkownFailureException,
			InvalidParameterException {
		try {
			paidActivityDB.addPaidService(null);
			fail();
		} catch (InvalidParameterException e) {
		}

		DBPaidService sBad1 = new DBPaidService("s1", "u1", 0, 1, 0);
		DBPaidService sBad2 = new DBPaidService("s1", "u1", 1, 0, 0);

		try {
			paidActivityDB.addPaidService(sBad1);
			fail();
		} catch (InvalidParameterException e) {
		}

		try {
			paidActivityDB.addPaidService(sBad2);
			fail();
		} catch (InvalidParameterException e) {
		}

		int count = 0;
		for (int i = 1; i <= 100; i++) {
			DBPaidService s = new DBPaidService("s" + Integer.toString(i),
					"user", i, i / 2 + 1, i);

			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(i));
			assertEquals(++count, paidActivityDB.addPaidService(s));
			assertEquals(ActivityStatus.SERVICE,
					paidActivityDB.getActivityStatus(count));
		}
	}

	@Test
	public void testAddPaidTask() throws DatabaseUnkownFailureException,
			InvalidParameterException {
		try {
			paidActivityDB.addPaidTask(null);
			fail();
		} catch (InvalidParameterException e) {
		}

		DBPaidTask tBad1 = new DBPaidTask("t1", "u1", 0, 1, 0);
		DBPaidTask tBad2 = new DBPaidTask("t1", "u1", 1, 0, 0);

		try {
			paidActivityDB.addPaidTask(tBad1);
			fail();
		} catch (InvalidParameterException e) {
		}

		try {
			paidActivityDB.addPaidTask(tBad2);
			fail();
		} catch (InvalidParameterException e) {
		}

		int count = 0;
		for (int i = 1; i <= 100; i++) {
			DBPaidTask t = new DBPaidTask("s" + Integer.toString(i), "user", i,
					i / 2 + 1, i);

			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(i));
			assertEquals(++count, paidActivityDB.addPaidTask(t));
			assertEquals(ActivityStatus.TASK,
					paidActivityDB.getActivityStatus(count));
		}
	}

	@Test
	public void testDeletePaidActivity() throws DatabaseUnkownFailureException,
			InvalidParameterException, SQLException {
		int id;
		Connection conn = ds.getConnection();

		DBPaidService s1 = new DBPaidService("s1", "u1", 1, 1, 0);
		DBPaidTask t1 = new DBPaidTask("t1", "u2", 2, 3, 1);

		id = paidActivityDB.addPaidService(s1);
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id));
		paidActivityDB.deletePaidActivity(id, conn);
		conn.commit();
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(id));

		id = paidActivityDB.addPaidTask(t1);
		assertEquals(ActivityStatus.TASK, paidActivityDB.getActivityStatus(id));
		paidActivityDB.deletePaidActivity(id, conn);
		conn.commit();
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(id));

		id = paidActivityDB.addPaidService(s1);
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id));
		paidActivityDB.deletePaidActivity(id, conn);
		conn.rollback();
		assertEquals(ActivityStatus.SERVICE,
				paidActivityDB.getActivityStatus(id));

		paidActivityDB.deletePaidActivity(id, conn);
		conn.commit();
		assertEquals(ActivityStatus.NOT_EXIST,
				paidActivityDB.getActivityStatus(id));

		int firstBatchId = id + 1;
		for (int i = 1; i <= 100; i++) {
			id = paidActivityDB.addPaidService(s1);
			id = paidActivityDB.addPaidTask(t1);
		}

		for (int i = id - 100 * 2 + 1; i <= id; i += 3) {
			assertEquals((i - firstBatchId) % 2 == 0 ? ActivityStatus.SERVICE
					: ActivityStatus.TASK, paidActivityDB.getActivityStatus(i));
			paidActivityDB.deletePaidActivity(i, conn);
			conn.commit();
			assertEquals(ActivityStatus.NOT_EXIST,
					paidActivityDB.getActivityStatus(i));
		}

		for (int i = id - 100 * 2 + 2; i <= id; i += 3) {
			ActivityStatus status = (i - firstBatchId) % 2 == 0 ? ActivityStatus.SERVICE
					: ActivityStatus.TASK;
			assertEquals(status, paidActivityDB.getActivityStatus(i));
			paidActivityDB.deletePaidActivity(i, conn);
			assertEquals(status, paidActivityDB.getActivityStatus(i));
		}
	}

	@Test
	public void testGetActivityStatusInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActivityStatusIntConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetServicesOfferedToUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTasksOfferedToUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetServicesOfferedByUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTasksOfferedByUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterToActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnregisterFromActivity() {
		fail("Not yet implemented");
	}

}
