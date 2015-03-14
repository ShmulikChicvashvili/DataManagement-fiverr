/**
 *
 */

package test;


import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import com.servicebook.database.exceptions.UsersDatabaseExceptions.UsersDatabaseCreationException;
import com.servicebook.database.exceptions.UsersDatabaseExceptions.UsersDatabaseInvalidParamsException;
import com.servicebook.database.exceptions.UsersDatabaseExceptions.UsersDatabaseUnkownFailureException;
import com.servicebook.database.exceptions.UsersDatabaseExceptions.UsersDatabaseUserAlreadyExistsException;
import com.servicebook.database.implementation.UsersDatabaseImpl;
import com.servicebook.database.primitives.DBUser;




/**
 * @author Shmulik
 *
 */
public class UsersDatabaseTest
{
	@Test
	public void AddBadUserTest1()
	{
		final DBUser badUser = null;
		try
		{
			userDB.addUser(badUser);
		} catch (final UsersDatabaseUserAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final UsersDatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final UsersDatabaseInvalidParamsException e)
		{}
	}
	
	
	@Test
	public void AddBadUserTest2()
	{
		
		DBUser badUser = new DBUser(null, null, null, 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final UsersDatabaseUserAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final UsersDatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final UsersDatabaseInvalidParamsException e)
		{}

		badUser = new DBUser(null, "password", "name", -1);
		try
		{
			userDB.addUser(badUser);
		} catch (final UsersDatabaseUserAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final UsersDatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final UsersDatabaseInvalidParamsException e)
		{}
		
		badUser = new DBUser("username", "password", null, 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final UsersDatabaseUserAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final UsersDatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final UsersDatabaseInvalidParamsException e)
		{}
		
		badUser = new DBUser("username", null, "name", 0);
		try
		{
			userDB.addUser(badUser);
		} catch (final UsersDatabaseUserAlreadyExistsException e)
		{
			fail("In case of invalid params we shouldnt get to already exists exception");
		} catch (final UsersDatabaseUnkownFailureException e)
		{
			fail("In case of invalid params we shouldnt get here");
		} catch (final UsersDatabaseInvalidParamsException e)
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
			UsersDatabaseUserAlreadyExistsException
			| UsersDatabaseUnkownFailureException
			| UsersDatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}

		goodUser = new DBUser("Eyal", "123", "eyal", 0);
		try
		{
			userDB.addUser(goodUser);
		} catch (
			UsersDatabaseUserAlreadyExistsException
			| UsersDatabaseUnkownFailureException
			| UsersDatabaseInvalidParamsException e)
		{
			fail();
		}
	}
	
	@Test
	public void AddSameUserTest() {
		DBUser goodUser = new DBUser("Shmulik", "123", "shmulik", 0);
		try
		{
			userDB.addUser(goodUser);
		} catch (
			UsersDatabaseUserAlreadyExistsException
			| UsersDatabaseUnkownFailureException
			| UsersDatabaseInvalidParamsException e)
		{
			e.printStackTrace();
			fail();
		}
		
		goodUser = new DBUser("Shmulik", "1", "s", 1);
		try
		{
			userDB.addUser(goodUser);
		} catch (UsersDatabaseUserAlreadyExistsException e)
		{
			// Success
		} catch (UsersDatabaseUnkownFailureException e)
		{
			fail("Shouldnt get to UsersDatabaseUnkownFailureException exception");
		} catch (UsersDatabaseInvalidParamsException e)
		{
			fail("Shouldnt get to UsersDatabaseInvalidParamsException");
		}
	}


	@Before
	public void Before()
	{
		ds = new BasicDataSource();
		ds.setDefaultAutoCommit(false);
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername("root");
		ds.setPassword("root");
		ds.setUrl("jdbc:mysql://localhost/");

		try
		{
			userDB = new UsersDatabaseImpl("users", "servicebook_db", ds);
		} catch (final UsersDatabaseCreationException e)
		{
			fail("Fuck");
		}
	}
	
	@org.junit.After
	public void After() {
		String dropQuery = "DROP TABLE `servicebook_db`.`users`;";
		try(Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement()) {
			stmt.execute(dropQuery);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}


	BasicDataSource ds;
	UsersDatabaseImpl userDB;
}
