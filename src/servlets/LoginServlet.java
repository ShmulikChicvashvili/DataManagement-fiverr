
package servlets;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.servicebook.database.UsersDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.users.InvalidParamsException;




/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet()
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
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		ServletContext sc = getServletContext();
		UsersDatabase userDB = (UsersDatabase) sc.getAttribute("userDB");
		boolean res = false;
		PrintWriter output = response.getWriter();
		try
		{
			res = userDB.validateUser(username, password);
		} catch (DatabaseUnkownFailureException e)
		{
			output.write("Unknown error occured");
			response.addHeader("NEED_REDIRECT", "false");
		} catch (InvalidParamsException e)
		{
			output.write("Invalid parameters");
			response.addHeader("NEED_REDIRECT", "false");
		}
		if (res)
		{
			HttpSession session = request.getSession(true);
			session.setAttribute("username", username);
			Cookie usernameCookie = new Cookie("username", username);
			Cookie passwordCookie = new Cookie("password", password);
			usernameCookie.setMaxAge(60 * 60 * 24 * 7);
			passwordCookie.setMaxAge(60 * 60 * 24 * 7);
			response.addCookie(usernameCookie);
			response.addCookie(passwordCookie);
			output.write("success");
			response.addHeader("NEED_REDIRECT", "true");
		} else
		{
			output.write("Username or password are incorrect");
			response.addHeader("NEED_REDIRECT", "false");
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
