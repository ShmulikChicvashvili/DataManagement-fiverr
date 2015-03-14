package test;

import static org.junit.Assert.*;
import jdk.nashorn.internal.ir.ForNode;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.Test;

import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.implementation.PaidActivitiesDatabaseImpl;

public class PaidActivityDatabaseTest {

	@Test
	public void test() throws ClassNotFoundException {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername("root");
		ds.setPassword("root");
		ds.setUrl("jdbc:mysql://localhost/");

		PaidActivitiesDatabase paidDB = new PaidActivitiesDatabaseImpl(
				"activities", "activities_registrations", "servicebook_db", ds);

		System.out.println("Done test!");
	}
}
