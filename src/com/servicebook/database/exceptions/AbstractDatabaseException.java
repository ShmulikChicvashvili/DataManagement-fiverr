/**
 * 
 */
package com.servicebook.database.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Shmulik
 *
 */
public abstract class AbstractDatabaseException extends Exception
{	
	public AbstractDatabaseException(Exception e)
	{
		trace = new StringWriter();
		e.printStackTrace(new PrintWriter(trace));
	}
	
	@Override
	public void printStackTrace()
	{
		System.err.println(trace.toString());
	}
	
	/* (non-Javadoc) @see java.lang.Throwable#printStackTrace(java.io.PrintWriter) */
	@Override
	public void printStackTrace(PrintWriter s)
	{
		s.write(trace.toString());
	}

	StringWriter trace;


}
