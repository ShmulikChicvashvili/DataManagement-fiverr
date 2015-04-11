/**
 *
 */

package test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.users.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.exceptions.users.TableCreationException;
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
			final String data = new Integer(i).toString();
			try
			{
				userDB.addUser(new DBUser(data, data, data, i));
			} catch (
				ElementAlreadyExistsException
				| DatabaseUnkownFailureException
				| InvalidParamsException e)
			{
				e.printStackTrace();
				fail();
			}
		}
	}


	@Test
	public void AddUserCaseSensitiveTest()
	{
		final DBUser goodUser = new DBUser("Shmulik", "123", "shmulik", 1);
		final DBUser goodUserCaseUnSensitive =
			new DBUser("shmuliK", "123", "shmulik", 2);

		try
		{
			userDB.addUser(goodUser);
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}

		try
		{
			userDB.addUser(goodUserCaseUnSensitive);
		} catch (final ElementAlreadyExistsException e)
		{
			return;
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
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
		} catch (final ElementAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final InvalidParamsException e)
		{}
	}


	@Test
	public void AddBadUserTest2()
	{

		DBUser badUser = new DBUser(null, null, null, 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final ElementAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final InvalidParamsException e)
		{}

		badUser = new DBUser(null, "password", "name", -1);
		try
		{
			userDB.addUser(badUser);
		} catch (final ElementAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final InvalidParamsException e)
		{}

		badUser = new DBUser("username", "password", null, 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final ElementAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final InvalidParamsException e)
		{}

		badUser = new DBUser("username", null, "name", 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final ElementAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final InvalidParamsException e)
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
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}

		goodUser = new DBUser("Eyal", "123", "eyal", 0);
		try
		{
			userDB.addUser(goodUser);
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e)
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
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}

		goodUser = new DBUser("Shmulik", "1", "s", 1);
		try
		{
			userDB.addUser(goodUser);
		} catch (final ElementAlreadyExistsException e)
		{
			// Success
		} catch (final DatabaseUnkownFailureException e)
		{
			fail("Shouldnt get to UsersDatabaseUnkownFailureException exception");
		} catch (final InvalidParamsException e)
		{
			fail("Shouldnt get to UsersDatabaseInvalidParamsException");
		}
	}


	@Test
	public void GetBadUserTest()
	{
		final String badUsername = null;
		try
		{
			userDB.getUser(badUsername);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
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
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
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
			| InvalidParamsException
			| ElementAlreadyExistsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(null, res);

		getUsername = "Shmulik2";
		try
		{
			userDB.getUser(getUsername);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(null, res);
	}


	@Test
	public void GetGoodUserTest()
	{
		final String username1 = "Shmulik";
		final String username2 = "Eyal";
		final DBUser goodUser1 =
			new DBUser(username1, "1", "shmulik chicvashvili", 1);
		final DBUser goodUser2 = new DBUser(username2, "2", "eyal gaylord", 2);
		try
		{
			userDB.addUser(goodUser1);
			userDB.addUser(goodUser2);
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}

		DBUser res = null;
		try
		{
			res = userDB.getUser(username1);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(goodUser1, res);

		try
		{
			res = userDB.getUser(username2);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(goodUser2, res);

		try
		{
			res = userDB.getUser("sHmuLik");
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
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
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{
			// Success
		}

		start = 0;
		amount = -1;
		try
		{
			userDB.getUsers(start, amount);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{
			// Success
		}
	}


	@Test
	public void GetOfLimitUsersTest()
	{
		final ArrayList<DBUser> users = new ArrayList<>();
		users.add(new DBUser("1", "1", "1", 1));
		users.add(new DBUser("2", "2", "2", 2));
		users.add(new DBUser("3", "3", "3", 3));
		for (int i = 0; i < users.size(); i++)
		{
			try
			{
				userDB.addUser(users.get(i));
			} catch (
				ElementAlreadyExistsException
				| DatabaseUnkownFailureException
				| InvalidParamsException e)
			{
				e.printStackTrace();
				fail();
			}
		}

		List<DBUser> res = null;
		try
		{
			res = userDB.getUsers(0, 100);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
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
		final DBUser shmulik = new DBUser("Shmulik", "123", "shmulik", 1);
		final DBUser eyal = new DBUser("Eyal", "1", "eyal", 2);
		final DBUser itay = new DBUser("Itay", "imanidiot", "itay", -11);
		final DBUser yannay = new DBUser("Yannay", "boobs", "yannay", 12);
		final DBUser savi = new DBUser("Savious", "sahibalata", "spmama", 14);
		final ArrayList<DBUser> users = new ArrayList<>();
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
				ElementAlreadyExistsException
				| DatabaseUnkownFailureException
				| InvalidParamsException e)
			{
				e.printStackTrace();
				fail();
			}
		}

		List<DBUser> res = null;

		try
		{
			res = userDB.getUsers(3, 1);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(1, res.size());
		assertEquals(users.get(3), res.get(0));

		try
		{
			res = userDB.getUsers(0, 5);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
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
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
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
	public void isUserExistBadParamsTest() throws SQLException
	{
		conn = ds.getConnection();
		try
		{
			userDB.isUserExists(null, conn);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{
			conn.close();
			return;
		}
		fail();
	}


	@Test
	public void isUserExistNonExistingTest() throws SQLException
	{
		conn = ds.getConnection();
		final DBUser shmulik = new DBUser("Shmulik", "123", "shmulik", 1);
		try
		{
			userDB.addUser(shmulik);
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e1)
		{
			e1.printStackTrace();
			fail();
		}

		boolean res = false;
		try
		{
			res = userDB.isUserExists("avi", conn);
			userDB.addUser(new DBUser("avi", "1", "av", 1));
		} catch (
			DatabaseUnkownFailureException
			| InvalidParamsException
			| ElementAlreadyExistsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(false, res);

		try
		{
			res = userDB.isUserExists("shmulik2", conn);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(false, res);

		conn.close();
	}


	@Test
	public void isUserExistExistingTest() throws SQLException
	{
		conn = ds.getConnection();

		final DBUser shmulik = new DBUser("Shmulik", "123", "shmulik", 1);
		final DBUser eyal = new DBUser("Eyal", "1", "eyal", 2);

		try
		{
			userDB.addUser(shmulik);
			userDB.addUser(eyal);
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e1)
		{
			e1.printStackTrace();
			fail();
		}

		boolean res = false;
		try
		{
			res = userDB.isUserExists("ShMuliK", conn);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(true, res);

		try
		{
			res = userDB.isUserExists("Eyal", conn);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(true, res);

		conn.close();
	}


	@Test
	public void validateUserBadParamsTest()
	{
		String username = null;
		String password = null;

		try
		{
			userDB.validateUser(username, password);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{}

		username = "1";
		try
		{
			userDB.validateUser(username, password);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{}

		username = null;
		password = "1";
		try
		{
			userDB.validateUser(username, password);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{}
	}


	@Test
	public void validateUserTest()
	{
		final DBUser badUser = new DBUser("Shmulik", "1", "shmulik", 1);
		boolean res = false;
		try
		{
			res = userDB.validateUser("Shmulik2", "1");
			userDB.addUser(badUser);
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(false, res);

		try
		{
			res = userDB.validateUser("Shmulik", "2");
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(false, res);

		try
		{
			res = userDB.validateUser("Shmulik", "1");
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(true, res);

		try
		{
			res = userDB.validateUser("SHmUlik", "1");
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(true, res);
	}


	@Test
	public void updateBalanceInvalidParamsTest()
	{
		try
		{
			userDB.updateBalance(null, "shmulik", 1);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{
			// success
		}

		Connection conn = null;
		try
		{
			conn = ds.getConnection();
		} catch (final SQLException e)
		{
			e.printStackTrace();
			fail();
		}
		if (conn == null)
		{
			fail();
		}
		try
		{
			userDB.updateBalance(conn, null, 0);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{
			// success
		}

		try
		{
			conn.close();
		} catch (final SQLException e)
		{
			e.printStackTrace();
			fail();
		}
		try
		{
			userDB.updateBalance(conn, "", 0);
		} catch (final DatabaseUnkownFailureException e)
		{
			e.printStackTrace();
			fail();
		} catch (final InvalidParamsException e)
		{
			return;
		}
		fail();
	}


	@Test
	public void updateBalanceTest() throws SQLException
	{
		try
		{
			userDB.addUser(new DBUser("Shmulik", "1", "shmulik", 13));
			userDB.addUser(new DBUser("Eyal", "1", "eyal", -3));
		} catch (
			ElementAlreadyExistsException
			| DatabaseUnkownFailureException
			| InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}

		Connection conn = null;
		Connection conn2 = null;
		try
		{
			conn = ds.getConnection();
			conn2 = ds.getConnection();
		} catch (final SQLException e)
		{
			e.printStackTrace();
			fail();
		}
		if (conn == null || conn2 == null)
		{
			fail();
		}

		try
		{
			userDB.updateBalance(conn, "shmulik", 5);
			conn.commit();
			assertEquals(18, userDB.getUser("shmulik").getBalance());
			userDB.updateBalance(conn2, "shmulik", -5);
			conn2.commit();
			assertEquals(13, userDB.getUser("shmulik").getBalance());
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		} catch (final SQLException e)
		{
			e.printStackTrace();
			fail();
		} finally
		{
			conn.close();
			conn2.close();
		}
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

		final String dropQuery =
			"DROP TABLE IF EXISTS `servicebook_db`.`users`;";
		try (
			Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement())
		{
			stmt.execute(dropQuery);
		} catch (final SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			userDB = new UsersDatabaseImpl("users", "servicebook_db", ds);
		} catch (final TableCreationException e)
		{
			fail("Fuck");
		}
	}


	@org.junit.After
	public void After()
	{
		if (conn != null)
		{
			try
			{
				conn.close();
			} catch (final SQLException e)
			{
				e.printStackTrace();
			}
		}
		final String dropQuery = "DROP TABLE `servicebook_db`.`users`;";
		try (
			Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement())
		{
			stmt.execute(dropQuery);
		} catch (final SQLException e)
		{
			e.printStackTrace();
		}
	}



	BasicDataSource ds;

	UsersDatabaseImpl userDB;

	private Connection conn;
}
