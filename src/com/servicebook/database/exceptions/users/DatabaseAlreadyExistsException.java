/**
 * 
 */
package com.servicebook.database.exceptions.users;

import com.servicebook.database.exceptions.AbstractDatabaseException;

/**
 * @author Shmulik
 *
 */
public class DatabaseAlreadyExistsException
	extends
		AbstractDatabaseException
{

	/**
	 * @param e
	 */
	public DatabaseAlreadyExistsException(Exception e)
	{
		super(e);
	}	
	
}
