
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
	
	
	
	private String username;
	
	private String password;
	
	private String name;
	
	private int balance;

}
