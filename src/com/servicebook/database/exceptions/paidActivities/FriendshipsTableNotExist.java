/**
 *
 */

package com.servicebook.database.exceptions.paidActivities;


import com.servicebook.database.exceptions.AbstractDatabaseException;




/**
 * @author Eyal
 *
 */
public class FriendshipsTableNotExist extends AbstractDatabaseException
{
	public FriendshipsTableNotExist(Exception e)
	{
		super(e);
	}
}
