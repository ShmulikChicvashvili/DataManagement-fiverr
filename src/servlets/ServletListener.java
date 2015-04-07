
package servlets;


import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.UsersDatabase;
import com.servicebook.database.exceptions.users.TableCreationException;
import com.servicebook.database.implementation.FriendshipsDatabaseImpl;
import com.servicebook.database.implementation.PaidActivitiesDatabaseImpl;
import com.servicebook.database.implementation.UsersDatabaseImpl;




/**
 * 
 */

/**
 * @author Shmulik
 *
 */
public class ServletListener implements ServletContextListener
{
	
	/* (non-Javadoc) @see
	 * javax.servlet.ServletContextListener#contextDestroyed(javax
	 * .servlet.ServletContextEvent) */
	@Override
	public void contextDestroyed(ServletContextEvent e)
	{
		// should be doing something?
		
	}
	
	
	/* (non-Javadoc) @see
	 * javax.servlet.ServletContextListener#contextInitialized
	 * (javax.servlet.ServletContextEvent) */
	@Override
	public void contextInitialized(ServletContextEvent e)
	{
		ServletContext sc = e.getServletContext();
		String url = sc.getInitParameter("url");
		String driverClassName = sc.getInitParameter("driverClassName");
		String username = sc.getInitParameter("username");
		String password = sc.getInitParameter("password");
		String schema = sc.getInitParameter("schema");
		String usersTable = sc.getInitParameter("usersTable");
		String friendshipsTable = sc.getInitParameter("friendshipsTable");
		String activitiesTable1 = sc.getInitParameter("activitiesTable1");
		String activitiesTable2 = sc.getInitParameter("activitiesTable2");
		String friendshipUserColumn =
			sc.getInitParameter("friendshipUserColumn");
		String friendshipsFriendColumn =
			sc.getInitParameter("friendshipsFriendColumn");
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDefaultAutoCommit(false);
		ds
			.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		ds.setDriverClassName(driverClassName);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setUrl(url);
		UsersDatabase userDB = null;
		try
		{
			userDB = new UsersDatabaseImpl(usersTable, schema, ds);
		} catch (TableCreationException e1)
		{
			e1.printStackTrace();
			return;
		}
		sc.setAttribute("userDB", userDB);
		
		FriendshipsDatabase friendshipsDB = null;
		try
		{
			friendshipsDB =
				new FriendshipsDatabaseImpl(
					friendshipsTable,
					usersTable,
					schema,
					ds);
		} catch (com.servicebook.database.exceptions.friendships.TableCreationException e1)
		{
			e1.printStackTrace();
			return;
		}
		sc.setAttribute("friendshipDB", friendshipsDB);
		
		PaidActivitiesDatabase activitiesDB = null;
		try
		{
			activitiesDB =
				new PaidActivitiesDatabaseImpl(
					activitiesTable1,
					activitiesTable2,
					schema,
					ds,
					friendshipsTable,
					friendshipUserColumn,
					friendshipsFriendColumn);
		} catch (com.servicebook.database.exceptions.paidActivities.TableCreationException e1)
		{
			e1.printStackTrace();
			return;
		}
		sc.setAttribute("activitiesDB", activitiesDB);
	}
}
