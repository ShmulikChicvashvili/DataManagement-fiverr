
package servlets;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.MultiTableDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.friendships.ReflexiveFriendshipException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;




/**
 * Servlet implementation class AddFriendServlet
 */
@WebServlet("/AddFriend")
public class AddFriendServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddFriendServlet()
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
		String username1 = request.getParameter("first_username");
		String username2 = request.getParameter("second_username");
		
		ServletContext sc = getServletContext();
		MultiTableDatabase multiTableDB =
			(MultiTableDatabase) sc.getAttribute("multiTableDB");
		PrintWriter output = response.getWriter();
		try
		{
			multiTableDB.addFriendship(username1, username2);
		} catch (ElementAlreadyExistsException e)
		{
			output.write("Already friends");
			return;
		} catch (DatabaseUnkownFailureException e)
		{
			output.write("Unknown error occured");
			return;
		} catch (InvalidParameterException e)
		{
			output.write("Unknown error occured");
			return;
		} catch (ReflexiveFriendshipException e)
		{
			output.write("You can't add yourself");
			return;
		}
		output.write("Friend Added");
		return;
	}
	
}
