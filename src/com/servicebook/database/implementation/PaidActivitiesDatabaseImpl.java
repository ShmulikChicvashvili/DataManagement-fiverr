package com.servicebook.database.implementation;

import java.sql.Connection;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;

public class PaidActivitiesDatabaseImpl extends AbstractMySqlDatabase implements
		PaidActivitiesDatabase {

	public PaidActivitiesDatabaseImpl(String table, String schema,
			BasicDataSource datasource) {
		super(schema, datasource);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addPaidService(DBPaidService service) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPaidTask(DBPaidTask task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deletePaidActivity(long id, Connection conn) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DBPaidService> getServicesOfferedToUser(String username,
			int start, int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBPaidTask> getTasksOfferedToUser(String username, int start,
			int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBPaidService> getServicesOfferedByUser(String username,
			int start, int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBPaidTask> getTasksOfferedByUser(String username, int start,
			int amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerToActivity(String username, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterFromActivity(String username, long id) {
		// TODO Auto-generated method stub

	}

}
