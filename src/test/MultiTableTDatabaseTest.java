package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.UsersDatabase;
import com.servicebook.database.implementation.FriendshipsDatabaseImpl;
import com.servicebook.database.implementation.MultiTableDatabaseImpl;
import com.servicebook.database.implementation.PaidActivitiesDatabaseImpl;
import com.servicebook.database.implementation.UsersDatabaseImpl;

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
		friendsTableInfo = new FriendsTableInfo("`" + schema
				+ "`.`friendships`", "first_username", "second_userame");
	}

	@Before
	public void setUp() throws Exception {
		ds = new BasicDataSource();
		ds.setDefaultAutoCommit(false);
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername("root");
		ds.setPassword("root");
		ds.setUrl("jdbc:mysql://localhost/");

		conn = ds.getConnection();
		try (Statement stmt = conn.createStatement()) {
			String dropQuery = "";

			dropQuery = "DROP TABLE IF EXISTS `" + schema + "`.`users`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS " + friendsTableInfo.tableName
					+ ";";
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

			dropQuery = "DROP TABLE IF EXISTS " + friendsTableInfo.tableName
					+ ";";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`activities`;";
			stmt.execute(dropQuery);

			dropQuery = "DROP TABLE IF EXISTS `servicebook_db`.`activities_registrations`;";
			stmt.execute(dropQuery);

			conn.commit();
		}

		conn.close();
		ds.close();
	}

	@Test
	public void testRegisterToActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnregisterFromActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPaidActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddFriendship() {
		fail("Not yet implemented");
	}

	private String schema;

	private UsersDatabase usersDB;
	private FriendshipsDatabase friendsDB;
	private PaidActivitiesDatabase activitiesDB;
	private MultiTableDatabaseImpl multiDB;

	private final FriendsTableInfo friendsTableInfo;

	private BasicDataSource ds;
	private Connection conn;

}
