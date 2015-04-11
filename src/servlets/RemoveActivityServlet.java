
package servlets;


import java.io.IOException;
import java.io.PrintWriter;

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
 * Servlet implementation class RemoveActivityServlet
 */
@WebServlet("/RemoveActivity")
public class RemoveActivityServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RemoveActivityServlet()
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
		String idString = request.getParameter("id");
		ServletContext sc = getServletContext();
		MultiTableDatabase multiTableDB =
			(MultiTableDatabase) sc.getAttribute("multiTableDB");
		PrintWriter output = response.getWriter();
		int id = new Integer(idString).intValue();
		try
		{
			multiTableDB.deleteActivity(id);
		} catch (DatabaseUnkownFailureException e)
		{
			output.write("Unknown failure occured");
			return;
		} catch (InvalidParameterException e)
		{
			output.write("Invalid parameters");
			return;
		} 
		finally {
			output.close();
		}
		response.getWriter().write("Removed successfuly");
		output.close();
	}
	
}
