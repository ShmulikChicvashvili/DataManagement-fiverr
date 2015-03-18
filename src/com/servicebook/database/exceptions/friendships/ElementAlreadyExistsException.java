/**
 * 
 */
package com.servicebook.database.exceptions.friendships;

import com.servicebook.database.exceptions.AbstractDatabaseException;

/**
 * @author Shmulik
 *
 */
public class ElementAlreadyExistsException extends AbstractDatabaseException
{

	/**
	 * @param e
	 */
	public ElementAlreadyExistsException(Exception e)
	{
		super(e);
	}	
	
}
