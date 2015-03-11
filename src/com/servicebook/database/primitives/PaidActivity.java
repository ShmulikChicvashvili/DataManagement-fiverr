package com.servicebook.database.primitives;

public abstract class PaidActivity {
	protected long id;
	protected String title;
	protected String username;
	protected int capacity;
	protected int distance;

	public PaidActivity(long id, String title, String username, int capacity,
			int distance) {
		super();
		this.id = id;
		this.title = title;
		this.username = username;
		this.capacity = capacity;
		this.distance = distance;
	}
}
