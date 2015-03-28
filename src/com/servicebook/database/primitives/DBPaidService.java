
package com.servicebook.database.primitives;


public class DBPaidService extends DBPaidActivity
{
	
	public DBPaidService(
		int id,
		String title,
		String username,
		short capacity,
		short distance,
		short numRegistered)
	{
		super(id, title, username, capacity, distance, numRegistered);
	}
	
	
	public DBPaidService(
		String title,
		String username,
		int capacity,
		int distance,
		int numRegistered)
	{
		super(
			title,
			username,
			(short) capacity,
			(short) distance,
			(short) numRegistered);
	}
	
}
