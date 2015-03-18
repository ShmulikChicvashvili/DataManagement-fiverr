/**
 * 
 */
package com.servicebook.database.exceptions.users;

import com.servicebook.database.exceptions.AbstractDatabaseException;

/**
 * @author Shmulik
 *
 */
public class TableCreationException extends AbstractDatabaseException
{

	/**
	 * @param e The exception to set
	 */
	public TableCreationException(Exception e)
	{
		super(e);
	}	
	
}
