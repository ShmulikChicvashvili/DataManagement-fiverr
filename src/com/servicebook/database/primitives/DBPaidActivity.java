package com.servicebook.database.primitives;

public abstract class DBPaidActivity {
	protected long id;
	protected String title;
	protected String username;
	protected int capacity;
	protected int distance;
	protected int numRegistered;

	public DBPaidActivity(long id, String title, String username, int capacity,
			int distance, int numRegistered) {
		super();
		this.id = id;
		this.title = title;
		this.username = username;
		this.capacity = capacity;
		this.distance = distance;
		this.numRegistered = numRegistered;
	}
}
