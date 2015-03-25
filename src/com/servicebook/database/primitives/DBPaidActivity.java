package com.servicebook.database.primitives;

public abstract class DBPaidActivity {
	protected int id;
	protected String title;
	protected String username;
	protected short capacity;
	protected short distance;
	protected short numRegistered;

	public DBPaidActivity(String title, String username, short capacity,
			short distance, short numRegistered) {
		super();
		id = -1;
		this.title = title;
		this.username = username;
		this.capacity = capacity;
		this.distance = distance;
		this.numRegistered = numRegistered;
	}

	public DBPaidActivity(int id, String title, String username,
			short capacity, short distance, short numRegistered) {
		super();
		this.id = id;
		this.title = title;
		this.username = username;
		this.capacity = capacity;
		this.distance = distance;
		this.numRegistered = numRegistered;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public short getCapacity() {
		return capacity;
	}

	public void setCapacity(short capacity) {
		this.capacity = capacity;
	}

	public short getDistance() {
		return distance;
	}

	public void setDistance(short distance) {
		this.distance = distance;
	}

	public short getNumRegistered() {
		return numRegistered;
	}

	public void setNumRegistered(short numRegistered) {
		this.numRegistered = numRegistered;
	}
}
