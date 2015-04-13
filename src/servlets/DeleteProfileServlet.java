
package servlets;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.servicebook.database.MultiTableDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;




/**
 * Servlet implementation class DeleteProfileServlet
 */
@WebServlet("/DeleteProfile")
public class DeleteProfileServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteProfileServlet()
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
		MultiTableDatabase multiTableDB =
			(MultiTableDatabase) getServletContext().getAttribute(
				"multiTableDB");
		try
		{
			multiTableDB.deleteUser((String) request
				.getSession(false)
				.getAttribute("username"));
		} catch (DatabaseUnkownFailureException e)
		{
			// TODO: figure out what to do
			return;
		} catch (InvalidParameterException e)
		{
			// TODO: figure out what to do
			return;
		}
		HttpSession session = request.getSession(false);
		if (session != null)
		{
			session.invalidate();
		}
		response.sendRedirect("/HW5/login.html");
	}
	
}
