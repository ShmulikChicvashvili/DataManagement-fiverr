
package servlets;


import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.servicebook.database.MultiTableDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;




/**
 * Servlet implementation class RemoveAllActivitiesServlet
 */
@WebServlet("/RemoveAllActivities")
public class RemoveAllActivitiesServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RemoveAllActivitiesServlet()
	{}
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		String username = request.getParameter("username");
		ServletContext sc = getServletContext();
		MultiTableDatabase multiTableDB =
			(MultiTableDatabase) sc.getAttribute("multiTableDB");
		try
		{
			multiTableDB.deleteUserActivities(username);
		} catch (DatabaseUnkownFailureException | InvalidParameterException e)
		{
			return;
		}
	}
	
}
