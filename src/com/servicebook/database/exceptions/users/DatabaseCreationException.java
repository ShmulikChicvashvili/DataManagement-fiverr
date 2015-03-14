/**
 * 
 */
package com.servicebook.database.exceptions.users;

import com.servicebook.database.exceptions.AbstractDatabaseException;

/**
 * @author Shmulik
 *
 */
public class DatabaseCreationException extends AbstractDatabaseException
{

	/**
	 * @param e The exception to set
	 */
	public DatabaseCreationException(Exception e)
	{
		super(e);
	}	
	
}
