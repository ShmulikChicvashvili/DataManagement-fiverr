
package com.servicebook.database.primitives;


public class DBUser
{
	public DBUser(String username, String password, String name, int balance)
	{
		super();
		this.username = username;
		this.password = password;
		this.name = name;
		this.balance = balance;
	}
	
	
	/**
	 * @return the balance
	 */
	public int getBalance()
	{
		return balance;
	}
	
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}
	
	
	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}
	
	
	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(int balance)
	{
		this.balance = balance;
	}
	
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	
	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	
	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	
	/* (non-Javadoc) @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof DBUser)) { return false; }
		DBUser other = (DBUser) obj;
		return (this.username.equals(other.username)
			&& this.password.equals(other.password)
			&& this.name.equals(other.name) && this.balance == other.balance);
	}
	
	
	/* (non-Javadoc) @see java.lang.Object#toString() */
	@Override
	public String toString()
	{
		return this.username
			+ ", "
			+ this.password
			+ ", "
			+ this.name
			+ ", "
			+ new Integer(this.balance).toString();
	}
	
	
	
	private String username;
	
	private String password;
	
	private String name;
	
	private int balance;
	
}
