/**
 * 
 */
package com.servicebook.database.exceptions;


/**
 * @author Shmulik
 *
 */
public class DatabaseUnkownFailureException
	extends
		AbstractDatabaseException
{

	/**
	 * @param e
	 */
	public DatabaseUnkownFailureException(Exception e)
	{
		super(e);
	}	
	
}
