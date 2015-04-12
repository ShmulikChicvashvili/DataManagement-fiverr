
package servlets;


import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.servicebook.database.UsersDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.primitives.DBUser;




/**
 * Servlet implementation class SearchUserServlet
 */
@WebServlet("/SearchUser")
public class SearchUserServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchUserServlet()
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
		String user = request.getParameter("user");
		ServletContext sc = getServletContext();
		UsersDatabase userDB = (UsersDatabase) sc.getAttribute("userDB");
		DBUser res = null;
		try
		{
			res = userDB.getUser(user);
		} catch (DatabaseUnkownFailureException e)
		{
			response.getWriter().write("Unknown failure occured");
			return;
		} catch (InvalidParamsException e)
		{
			response.getWriter().write("Invalid parameters");
			return;
		}
		if (res == null)
		{
			response.getWriter().write("No such user");
			;
		} else
		{
			response.getWriter().println(
				"Username: " + res.getUsername() + "<br>");
			response.getWriter().println("Name: " + res.getName() + "<br>");
			response.getWriter().println("Balance: " + res.getBalance());
		}
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
		doGet(request, response);
	}
	
}
