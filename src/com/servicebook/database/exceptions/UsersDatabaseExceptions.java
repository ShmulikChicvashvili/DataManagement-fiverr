/**
 *
 */

package com.servicebook.database.exceptions;


import java.io.StringWriter;




/**
 * @author Shmulik
 *
 *         This class will hold all the possible exceptions for the
 *         UsersDatabaseImpl class
 */
public class UsersDatabaseExceptions
{
	/**
	 * @author Shmulik
	 *
	 *         Exception in case the table's creation statement fails for some
	 *         reason
	 */
	public static class UsersDatabaseCreationException extends Exception
	{
		public UsersDatabaseCreationException()
		{
			trace = new StringWriter();
		}


		public StringWriter getTrace()
		{
			return trace;
		}


		@Override
		public void printStackTrace()
		{
			System.err.println(trace.toString());
		}



		private final StringWriter trace;
		
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
	}



	/**
	 * @author Shmulik
	 *
	 *         Exception in case of invalid parameters
	 */
	public static class UsersDatabaseInvalidParamsException extends Exception
	{
		public UsersDatabaseInvalidParamsException()
		{
			trace = new StringWriter();
		}
		
		
		public StringWriter getTrace()
		{
			return trace;
		}


		@Override
		public void printStackTrace()
		{
			System.err.println(trace.toString());
		}



		private final StringWriter trace;
		
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
	}



	/**
	 * @author Shmulik
	 *
	 *         Exception in case of an unknown failure in the database
	 */
	public static class UsersDatabaseUnkownFailureException extends Exception
	{
		public UsersDatabaseUnkownFailureException()
		{
			trace = new StringWriter();
		}


		public StringWriter getTrace()
		{
			return trace;
		}


		@Override
		public void printStackTrace()
		{
			System.err.println(trace.toString());
		}



		private final StringWriter trace;
		
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
	}



	/**
	 * @author Shmulik
	 *
	 *         Exception in case that the user already exists in the database
	 */
	public static class UsersDatabaseUserAlreadyExistsException
		extends
			Exception
	{
		public UsersDatabaseUserAlreadyExistsException()
		{
			trace = new StringWriter();
		}
		
		
		public StringWriter getTrace()
		{
			return trace;
		}


		@Override
		public void printStackTrace()
		{
			System.err.println(trace.toString());
		}



		private final StringWriter trace;
		
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
	}

}
