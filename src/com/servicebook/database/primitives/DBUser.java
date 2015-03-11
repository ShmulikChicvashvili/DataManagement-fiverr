package com.servicebook.database.primitives;

public class DBUser {
	private String username;
	private String password;
	private String name;
	private int balance;

	public DBUser(String username, String password, String name, int balance) {
		super();
		this.username = username;
		this.password = password;
		this.name = name;
		this.balance = balance;
	}
}
