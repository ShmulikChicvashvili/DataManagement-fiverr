package com.servicebook.database.primitives;

public class DBPaidTask extends DBPaidActivity {

	public DBPaidTask(String title, String username, int capacity,
			int distance, int numRegistered) {
		super(title, username, (short) capacity, (short) distance,
				(short) numRegistered);
		// TODO Auto-generated constructor stub
	}

	public DBPaidTask(int id, String title, String username, short capacity,
			short distance, short numRegistered) {
		super(id, title, username, capacity, distance, numRegistered);
		// TODO Auto-generated constructor stub
	}

}
