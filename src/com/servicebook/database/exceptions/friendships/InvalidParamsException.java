/**
 * 
 */
package com.servicebook.database.exceptions.friendships;

import com.servicebook.database.exceptions.AbstractDatabaseException;

/**
 * @author Shmulik
 *
 */
public class InvalidParamsException extends AbstractDatabaseException
{

	/**
	 * @param e
	 */
	public InvalidParamsException(Exception e)
	{
		super(e);
	}	
	
}
