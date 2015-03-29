/**
 * 
 */

package test;


import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.friendships.InvalidParamsException;
import com.servicebook.database.exceptions.friendships.ReflexiveFriendshipException;
import com.servicebook.database.exceptions.friendships.TableCreationException;
import com.servicebook.database.implementation.FriendshipsDatabaseImpl;
import com.servicebook.database.implementation.UsersDatabaseImpl;
import com.servicebook.database.primitives.DBUser;




/**
 * @author Shmulik
 *
 */
public class FriendshipsDatabaseTest
{
	
	private BasicDataSource ds;
	
	private FriendshipsDatabase friendshipDB;
	
	private UsersDatabaseImpl userDB;
	
	DBUser userShmulik = new DBUser("Shmulik", "1", "Shmulik", 0);
	
	DBUser userEyal = new DBUser("Eyal", "1", "eyal", 3);
	
	DBUser userYannay = new DBUser("Yannay", "im_dumb", "yannay", -10);
	
	DBUser userItay = new DBUser("Itay", "lol", "itay", 2);
	
	DBUser userSavi = new DBUser("Savi", "saviBalata", "savi", 1);
	
	DBUser userYarden = new DBUser("Yarden", "jordan", "yarden", -4);
	
	DBUser userGal = new DBUser("Gal", "mucha", "gal", 11);
	
	
	
	@Before
	public void Before()
	{
		ds = new BasicDataSource();
		ds.setDefaultAutoCommit(false);
		ds
			.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername("root");
		ds.setPassword("root");
		ds.setUrl("jdbc:mysql://localhost/");
		
		try
		{
			userDB = new UsersDatabaseImpl("users", "servicebook_db", ds);
		} catch (com.servicebook.database.exceptions.users.TableCreationException e1)
		{
			fail();
		}
		try
		{
			userDB.addUser(userShmulik);
			userDB.addUser(userEyal);
			userDB.addUser(userYannay);
			userDB.addUser(userItay);
			userDB.addUser(userSavi);
			userDB.addUser(userYarden);
			userDB.addUser(userGal);
		} catch (
			com.servicebook.database.exceptions.users.ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| com.servicebook.database.exceptions.users.InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		try
		{
			friendshipDB =
				new FriendshipsDatabaseImpl(
					"friendships",
					"users",
					"servicebook_db",
					ds);
			
		} catch (TableCreationException e)
		{
			fail("fuck");
		}
	}
	
	
	@org.junit.After
	public void After()
	{
		String dropQueryUsers = "DROP TABLE `servicebook_db`.`users`;";
		String dropQueryFriendships =
			"DROP TABLE `servicebook_db`.`friendships`;";
		try (
			Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement())
		{
			stmt.execute(dropQueryUsers);
			stmt.execute(dropQueryFriendships);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void addFriendshipBadParams()
	{
		try
		{
			friendshipDB.addFriendship((String) null, (String) null);
			friendshipDB.addFriendship((String) null, "Shmulik");
			friendshipDB.addFriendship("Shmulik", (String) null);
			friendshipDB.addFriendship((DBUser) null, (DBUser) null);
			friendshipDB.addFriendship(
				new DBUser("1", "1", "2", 0),
				new DBUser(null, "2", "3", 0));
			friendshipDB.addFriendship(
				new DBUser("1", "1", "2", 0),
				new DBUser("2", null, "3", 0));
			friendshipDB.addFriendship(
				new DBUser("1", "1", "1", 0),
				new DBUser("3", "1", null, 0));
		} catch (ElementAlreadyExistsException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			// success
		} catch (ReflexiveFriendshipException e)
		{
			e.printStackTrace();
			fail();
		}
		
	}
	
	
	@Test
	public void addSimetricFriendshipTest()
	{
		try
		{
			friendshipDB.addFriendship(
				new DBUser("1", "2", "3", 0),
				new DBUser("1", "3", "4", -1));
		} catch (ElementAlreadyExistsException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		} catch (ReflexiveFriendshipException e)
		{
			// success
		}
		
		try
		{
			friendshipDB.addFriendship(
				new DBUser("Shm", "2", "3", 0),
				new DBUser("sHm", "3", "4", -1));
		} catch (ElementAlreadyExistsException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		} catch (ReflexiveFriendshipException e)
		{
			// success
			return;
		}
		fail();
	}
	
	
	@Test
	public void addFriendshipAlreadyExistTest()
	{
		try
		{
			friendshipDB.addFriendship(
				new DBUser("Shm", "1", "2", 0),
				new DBUser("Eyalz", "1", "2", 0));
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException
			| ReflexiveFriendshipException e)
		{
			e.printStackTrace();
			fail();
		}
		
		try
		{
			friendshipDB.addFriendship(
				new DBUser("EyAlz", "1", "2", 0),
				new DBUser("Shm", "1", "2", 0));
		} catch (ElementAlreadyExistsException e)
		{
			return;
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		} catch (ReflexiveFriendshipException e)
		{
			e.printStackTrace();
			fail();
		}
		
		fail();
	}
	
	
	@Test
	public void addLegalFriendshipTest()
	{
		try
		{
			friendshipDB.addFriendship(userShmulik, userSavi);
			friendshipDB.addFriendship(
				userSavi.getUsername(),
				userEyal.getUsername());
			friendshipDB.addFriendship(userYarden, userYannay);
			friendshipDB.addFriendship(userYannay.getUsername(), "Dvir");
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException
			| ReflexiveFriendshipException e)
		{
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Test
	public void getFriendsInvalidParamsTest()
	{
		try
		{
			friendshipDB.getFriends((String) null);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			// success
		}
		
		try
		{
			friendshipDB.getFriends((DBUser) null);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			// success
		}
		
		try
		{
			friendshipDB.getFriends(new DBUser(null, "1", "1", 0));
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			// success
		}
		
		try
		{
			friendshipDB.getFriends(new DBUser("1", null, "1", 0));
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			// success
		}
		
		try
		{
			friendshipDB.getFriends(new DBUser("1", "1", null, 0));
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (InvalidParamsException e)
		{
			// success
		}
	}
	
	
	@SuppressWarnings({ "boxing", "null" })
	@Test
	public void getFriendsTest()
	{
		try
		{
			friendshipDB.addFriendship(userShmulik, userSavi);
			friendshipDB.addFriendship(
				userSavi.getUsername(),
				userEyal.getUsername());
			friendshipDB.addFriendship(userYarden, userYannay);
			friendshipDB.addFriendship(userYannay.getUsername(), "Dvir");
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException
			| ReflexiveFriendshipException e)
		{
			e.printStackTrace();
			fail();
		}
		
		List<DBUser> res = null;
		try
		{
			res = friendshipDB.getFriends(userShmulik);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		if (res == null)
		{
			fail();
		}
		assertEquals(1, res.size());
		assertEquals(true, res.contains(userSavi));
		assertEquals(false, res.contains(userShmulik));
		
		try
		{
			res = friendshipDB.getFriends("Yannay");
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		if (res == null)
		{
			fail();
		}
		assertEquals(1, res.size());
		assertEquals(true, res.contains(userYarden));
		assertEquals(false, res.contains(userEyal));
		assertEquals(false, res.contains(userYannay));
	}
	
	
	@Test
	public void friendshipDBBigDataTest()
	{
		for (int i = 0; i < 1000; i++)
		{
			String s = new Integer(i).toString();
			DBUser u = new DBUser(s, s, s, i);
			try
			{
				userDB.addUser(u);
			} catch (
				com.servicebook.database.exceptions.users.ElementAlreadyExistsException
				| DatabaseUnkownFailureException
				| com.servicebook.database.exceptions.users.InvalidParamsException e)
			{
				e.printStackTrace();
				fail();
			}
			try
			{
				friendshipDB.addFriendship(userShmulik, u);
			} catch (
				ElementAlreadyExistsException
				| DatabaseUnkownFailureException
				| InvalidParamsException
				| ReflexiveFriendshipException e)
			{
				e.printStackTrace();
				fail();
			}
		}
		List<DBUser> res = null;
		try
		{
			res = friendshipDB.getFriends(userShmulik);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		if (res == null)
		{
			fail();
		}
		assertEquals(1000, res.size());
		for(int i = 0; i < 1000; i++) {
			String s = new Integer(i).toString();
			DBUser u = new DBUser(s, s, s, i);
			assertEquals(true, res.contains(u));
		}
	}
}
