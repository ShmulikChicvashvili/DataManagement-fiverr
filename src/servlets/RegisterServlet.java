
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
import com.servicebook.database.exceptions.users.ElementAlreadyExistsException;
import com.servicebook.database.exceptions.users.InvalidParamsException;
import com.servicebook.database.primitives.DBUser;




/**
 * 
 */

/**
 * @author Shmulik
 *
 */
@WebServlet("/servlet/Register")
public class RegisterServlet extends HttpServlet
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	/* (non-Javadoc) @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http
	 * .HttpServletRequest, javax.servlet.http.HttpServletResponse) */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException,
		IOException
	{
		doPost(req, resp);
	}
	
	
	/* (non-Javadoc) @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http
	 * .HttpServletRequest, javax.servlet.http.HttpServletResponse) */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException,
		IOException
	{
		String username = req.getParameter("username");
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		
		System.out.println(username + "\n" + name + "\n" + password);
		
		ServletContext sc = getServletContext();
		UsersDatabase userDB = (UsersDatabase) sc.getAttribute("userDB");
		PrintWriter output = resp.getWriter();
		try
		{
			userDB.addUser(new DBUser(username, password, name, 0));
		} catch (ElementAlreadyExistsException e)
		{
			output.write("Username already exists!");
			resp.addHeader("NEED_REDIRECT", "false");
			return;
		} catch (DatabaseUnkownFailureException e)
		{
			output.write("Unknown error occured");
			resp.addHeader("NEED_REDIRECT", "false");
			return;
		} catch (InvalidParamsException e)
		{
			output.write("Invalid parameters");
			resp.addHeader("NEED_REDIRECT", "false");
			return;
		}
		
		// Setting session and cookies for feature reference
		HttpSession session = req.getSession(true);
		session.setAttribute("username", username);
		Cookie usernameCookie = new Cookie("username", username);
		Cookie passwordCookie = new Cookie("password", password);
		usernameCookie.setMaxAge(60 * 60 * 24 * 7);
		passwordCookie.setMaxAge(60 * 60 * 24 * 7);
		resp.addCookie(usernameCookie);
		resp.addCookie(passwordCookie);
		// Finished handling session and cookies
		resp.addHeader("NEED_REDIRECT", "true");
	}
	
}
