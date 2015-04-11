
package servlets;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.servicebook.database.MultiTableDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;
import com.servicebook.database.primitives.DBPaidService;
import com.servicebook.database.primitives.DBPaidTask;




/**
 * Servlet implementation class OfferActivityServlet
 */
@WebServlet("/OfferActivity")
public class OfferActivityServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OfferActivityServlet()
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
		String type = request.getParameter("type");
		String title = request.getParameter("title");
		String capacity = request.getParameter("capacity");
		
		MultiTableDatabase multiTableDB =
			(MultiTableDatabase) getServletContext().getAttribute(
				"multiTableDB");
		PrintWriter output = response.getWriter();
		if (type.equals("Task"))
		{
			try
			{
				multiTableDB.addPaidTask(new DBPaidTask(
					title,
					username,
					new Integer(capacity),
					1,
					0));
			} catch (NumberFormatException e)
			{
				output.write("Invalid Arguments");
				return;
			} catch (DatabaseUnkownFailureException e)
			{
				output.write("Unknown Error Occured");
				return;
			} catch (InvalidParameterException e)
			{
				output.write("Invalid Arguments");
				return;
			} finally
			{
				output.close();
			}
			response.getWriter().write("Offered task successfuly");
			output.close();
		} else
		{
			try
			{
				multiTableDB.addPaidService(new DBPaidService(
					title,
					username,
					new Integer(capacity),
					1,
					0));
			} catch (NumberFormatException e)
			{
				output.write("Invalid Arguments");
				return;
			} catch (DatabaseUnkownFailureException e)
			{
				output.write("Unknown Error Occured");
				return;
			} catch (InvalidParameterException e)
			{
				output.write("Invalid Arguments");
				return;
			} finally
			{
				output.close();
			}
			response.getWriter().write("Offered task successfuly");
			output.close();
		}
	}
}
