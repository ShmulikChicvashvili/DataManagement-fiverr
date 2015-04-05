package com.servicebook.database.implementation;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.servicebook.database.AbstractMySqlDatabase;
import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.MultiTableDatabase;
import com.servicebook.database.PaidActivitiesDatabase;
import com.servicebook.database.UsersDatabase;

public class MultiTableDatabaseImpl extends AbstractMySqlDatabase implements
		MultiTableDatabase {

	public MultiTableDatabaseImpl(String schema, BasicDataSource datasource,
			FriendshipsDatabase friendsDB, PaidActivitiesDatabase activitiesDB,
			UsersDatabase usersDB) {
		super(schema, datasource);
		this.friendsDB = friendsDB;
		this.activitiesDB = activitiesDB;
		this.usersDB = usersDB;
	}

	@Override
	public void registerToActivity(int id, String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterFromActivity(int id, String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteUser(String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteActivity(int id) {
		// TODO Auto-generated method stub

	}

	private FriendshipsDatabase friendsDB;

	private PaidActivitiesDatabase activitiesDB;

	private UsersDatabase usersDB;

}
