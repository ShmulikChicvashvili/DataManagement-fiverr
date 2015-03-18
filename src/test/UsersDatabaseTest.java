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

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.users.DatabaseAlreadyExistsException;
import com.servicebook.database.exceptions.users.DatabaseCreationException;
import com.servicebook.database.exceptions.users.DatabaseInvalidParamsException;
import com.servicebook.database.implementation.UsersDatabaseImpl;
import com.servicebook.database.primitives.DBUser;




/**
 * @author Shmulik
 *
 */
public class UsersDatabaseTest
{
	@Test
	public void AddUserBigDataTest()
	{
		for (int i = 0; i < 1000; i++)
		{
			String data = new Integer(i).toString();
			try
			{
				userDB.addUser(new DBUser(data, data, data, i));
			} catch (
				DatabaseAlreadyExistsException
				| DatabaseUnkownFailureException
				| DatabaseInvalidParamsException e)
			{
				e.printStackTrace();
				fail();
			}
		}
	}
	
	
	@Test
	public void AddUserCaseSensitiveTest()
	{
		DBUser goodUser = new DBUser("Shmulik", "123", "shmulik", 1);
		DBUser goodUserCaseUnSensitive =
			new DBUser("shmuliK", "123", "shmulik", 2);
		
		try
		{
			userDB.addUser(goodUser);
		} catch (
			DatabaseAlreadyExistsException
			| DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		
		try
		{
			userDB.addUser(goodUserCaseUnSensitive);
		} catch (DatabaseAlreadyExistsException e)
		{
			return;
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		fail();
	}
	
	
	@Test
	public void AddBadUserTest1()
	{
		final DBUser badUser = null;
		try
		{
			userDB.addUser(badUser);
		} catch (final DatabaseAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final DatabaseInvalidParamsException e)
		{}
	}
	
	
	@Test
	public void AddBadUserTest2()
	{
		
		DBUser badUser = new DBUser(null, null, null, 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final DatabaseAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final DatabaseInvalidParamsException e)
		{}
		
		badUser = new DBUser(null, "password", "name", -1);
		try
		{
			userDB.addUser(badUser);
		} catch (final DatabaseAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final DatabaseInvalidParamsException e)
		{}
		
		badUser = new DBUser("username", "password", null, 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final DatabaseAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final DatabaseInvalidParamsException e)
		{}
		
		badUser = new DBUser("username", null, "name", 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final DatabaseAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final DatabaseInvalidParamsException e)
		{}
		
	}
	
	
	@Test
	public void AddGoodUsersTest()
	{
		DBUser goodUser = new DBUser("Shmulik", "123", "shmulik", 0);
		try
		{
			userDB.addUser(goodUser);
		} catch (
			DatabaseAlreadyExistsException
			| DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		
		goodUser = new DBUser("Eyal", "123", "eyal", 0);
		try
		{
			userDB.addUser(goodUser);
		} catch (
			DatabaseAlreadyExistsException
			| DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Test
	public void AddSameUserTest()
	{
		DBUser goodUser = new DBUser("Shmulik", "123", "shmulik", 0);
		try
		{
			userDB.addUser(goodUser);
		} catch (
			DatabaseAlreadyExistsException
			| DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		
		goodUser = new DBUser("Shmulik", "1", "s", 1);
		try
		{
			userDB.addUser(goodUser);
		} catch (DatabaseAlreadyExistsException e)
		{
			// Success
		} catch (DatabaseUnkownFailureException e)
		{
			fail("Shouldnt get to UsersDatabaseUnkownFailureException exception");
		} catch (DatabaseInvalidParamsException e)
		{
			fail("Shouldnt get to UsersDatabaseInvalidParamsException");
		}
	}
	
	
	@Test
	public void GetBadUserTest()
	{
		String badUsername = null;
		DBUser res = null;
		try
		{
			res = userDB.getUser(badUsername);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{
			return;
		}
		fail();
	}
	
	
	@Test
	public void GetNonExistingUserTest()
	{
		String getUsername = "Shmulik";
		DBUser res = null;
		try
		{
			res = userDB.getUser(getUsername);
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(null, res);
		
		try
		{
			userDB.getUser(getUsername);
			userDB.addUser(new DBUser(getUsername, "123", "shm", 1));
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException
			| DatabaseAlreadyExistsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(null, res);
		
		getUsername = "Shmulik2";
		try
		{
			userDB.getUser(getUsername);
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(null, res);
	}
	
	
	@Test
	public void GetGoodUserTest()
	{
		String username1 = "Shmulik";
		String username2 = "Eyal";
		DBUser goodUser1 =
			new DBUser(username1, "1", "shmulik chicvashvili", 1);
		DBUser goodUser2 = new DBUser(username2, "2", "eyal gaylord", 2);
		try
		{
			userDB.addUser(goodUser1);
			userDB.addUser(goodUser2);
		} catch (
			DatabaseAlreadyExistsException
			| DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		
		DBUser res = null;
		try
		{
			res = userDB.getUser(username1);
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(goodUser1, res);
		
		try
		{
			res = userDB.getUser(username2);
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(goodUser2, res);
		
		try
		{
			res = userDB.getUser("sHmuLik");
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(goodUser1, res);
	}
	
	
	@Test
	public void GetUsersBadParamsTest()
	{
		int start = -1;
		int amount = 10;
		try
		{
			userDB.getUsers(start, amount);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{
			// Success
		}
		
		start = 0;
		amount = -1;
		try
		{
			userDB.getUsers(start, amount);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{
			// Success
		}
	}
	
	
	@Test
	public void GetOfLimitUsersTest()
	{
		ArrayList<DBUser> users = new ArrayList<>();
		users.add(new DBUser("1", "1", "1", 1));
		users.add(new DBUser("2", "2", "2", 2));
		users.add(new DBUser("3", "3", "3", 3));
		for (int i = 0; i < users.size(); i++)
		{
			try
			{
				userDB.addUser(users.get(i));
			} catch (
				DatabaseAlreadyExistsException
				| DatabaseUnkownFailureException
				| DatabaseInvalidParamsException e)
			{
				e.printStackTrace();
				fail();
			}
		}
		
		List<DBUser> res = null;
		try
		{
			res = userDB.getUsers(0, 100);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		
		if (res.isEmpty())
		{
			fail();
		}
		
		for (int i = 0; i < users.size(); i++)
		{
			assertEquals(users.get(i), res.get(i));
		}
	}
	
	
	@Test
	public void GetGoodUsersTest()
	{
		DBUser shmulik = new DBUser("Shmulik", "123", "shmulik", 1);
		DBUser eyal = new DBUser("Eyal", "1", "eyal", 2);
		DBUser itay = new DBUser("Itay", "imanidiot", "itay", -11);
		DBUser yannay = new DBUser("Yannay", "boobs", "yannay", 12);
		DBUser savi = new DBUser("Savious", "sahibalata", "spmama", 14);
		ArrayList<DBUser> users = new ArrayList<>();
		users.add(eyal);
		users.add(itay);
		users.add(shmulik);
		users.add(savi);
		users.add(yannay);
		for (int i = 0; i < users.size(); i++)
		{
			try
			{
				userDB.addUser(users.get(i));
			} catch (
				DatabaseAlreadyExistsException
				| DatabaseUnkownFailureException
				| DatabaseInvalidParamsException e)
			{
				e.printStackTrace();
				fail();
			}
		}
		
		List<DBUser> res = null;
		
		try
		{
			res = userDB.getUsers(3, 1);
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(1, res.size());
		assertEquals(users.get(3), res.get(0));
		
		try
		{
			res = userDB.getUsers(0, 5);
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(5, res.size());
		for (int i = 0; i < users.size(); i++)
		{
			assertEquals(users.get(i), res.get(i));
		}
		
		try
		{
			res = userDB.getUsers(2, 3);
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(3, res.size());
		assertEquals(users.get(2), res.get(0));
		assertEquals(users.get(3), res.get(1));
		assertEquals(users.get(4), res.get(2));
	}
	
	
	@Test
	public void isUserExistBadParamsTest()
	{
		boolean res;
		try
		{
			res = userDB.isUserExists(null);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{
			return;
		}
		fail();
	}
	
	
	@Test
	public void isUserExistNonExistingTest()
	{
		DBUser shmulik = new DBUser("Shmulik", "123", "shmulik", 1);
		try
		{
			userDB.addUser(shmulik);
		} catch (
			DatabaseAlreadyExistsException
			| DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e1)
		{
			e1.printStackTrace();
			fail();
		}
		
		boolean res = false;
		try
		{
			res = userDB.isUserExists("avi");
			userDB.addUser(new DBUser("avi", "1", "av", 1));
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException
			| DatabaseAlreadyExistsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(false, res);
		
		try
		{
			res = userDB.isUserExists("shmulik2");
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(false, res);
	}
	
	
	@Test
	public void isUserExistExistingTest()
	{
		DBUser shmulik = new DBUser("Shmulik", "123", "shmulik", 1);
		DBUser eyal = new DBUser("Eyal", "1", "eyal", 2);
		
		try
		{
			userDB.addUser(shmulik);
			userDB.addUser(eyal);
		} catch (
			DatabaseAlreadyExistsException
			| DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e1)
		{
			e1.printStackTrace();
			fail();
		}
		
		boolean res = false;
		try
		{
			res = userDB.isUserExists("ShMuliK");
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(true, res);
		
		try
		{
			res = userDB.isUserExists("Eyal");
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(true, res);
	}
	
	
	@Test
	public void validateUserBadParamsTest()
	{
		String username = null;
		String password = null;
		
		try
		{
			userDB.validateUser(username, password);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{}
		
		username = "1";
		try
		{
			userDB.validateUser(username, password);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{}
		
		username = null;
		password = "1";
		try
		{
			userDB.validateUser(username, password);
		} catch (DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (DatabaseInvalidParamsException e)
		{}
	}
	
	
	@Test
	public void validateUserTest()
	{
		DBUser badUser = new DBUser("Shmulik", "1", "shmulik", 1);
		boolean res = false;
		try
		{
			res = userDB.validateUser("Shmulik2", "1");
			userDB.addUser(badUser);
		} catch (
			DatabaseAlreadyExistsException
			| DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(false, res);
		
		try
		{
			res = userDB.validateUser("Shmulik", "2");
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(false, res);
		
		try
		{
			res = userDB.validateUser("Shmulik", "1");
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(true, res);
		
		try
		{
			res = userDB.validateUser("SHmUlik", "1");
		} catch (
			DatabaseUnkownFailureException
			| DatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(true, res);
	}
	
	
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
		} catch (final DatabaseCreationException e)
		{
			fail("Fuck");
		}
	}
	
	
	@org.junit.After
	public void After()
	{
		String dropQuery = "DROP TABLE `servicebook_db`.`users`;";
		try (
			Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement())
		{
			stmt.execute(dropQuery);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	BasicDataSource ds;
	
	UsersDatabaseImpl userDB;
}
