/**
 * 
 */
package com.servicebook.database.exceptions.friendships;

import com.servicebook.database.exceptions.AbstractDatabaseException;

/**
 * @author Shmulik
 *
 */
public class TableCreationException extends AbstractDatabaseException
{

	/**
	 * @param e
	 */
	public TableCreationException(Exception e)
	{
		super(e);
	}	
	
}
