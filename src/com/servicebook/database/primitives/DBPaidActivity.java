package com.servicebook.database.primitives;

public abstract class DBPaidActivity {
	protected int id;

	protected String title;

	protected String username;

	protected short capacity;

	protected short distance;

	protected short numRegistered;

	/* (non-Javadoc) @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return "DBPaidActivity [id=" + id + ", title=" + title + ", username="
				+ username + ", capacity=" + capacity + ", distance="
				+ distance + ", numRegistered=" + numRegistered + "]";
	}

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

	/* (non-Javadoc) @see java.lang.Object#hashCode() */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
		result = prime * result + distance;
		result = prime * result + id;
		result = prime * result + numRegistered;
		result = prime * result + (title == null ? 0 : title.hashCode());
		result = prime * result + (username == null ? 0 : username.hashCode());
		return result;
	}

	/* (non-Javadoc) @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DBPaidActivity other = (DBPaidActivity) obj;
		if (capacity != other.capacity) {
			return false;
		}
		if (distance != other.distance) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (numRegistered != other.numRegistered) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
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

	public boolean isFull() {
		return capacity <= numRegistered;
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
