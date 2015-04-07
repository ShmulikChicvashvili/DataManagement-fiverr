
package servlets;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.SendResult;
import javax.websocket.Session;




/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LogoutServlet()
	{}
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = request.getSession(false);
		if (session != null)
		{
			session.invalidate();
		}
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			response.sendRedirect("/HW5/login.html");
			return;
		}
		for(Cookie c : cookies) {
			if(c.getName().equals("username") || c.getName().equals("password")) {
				c.setMaxAge(0);
				response.addCookie(c);
			}
		}
		response.sendRedirect("/HW5/login.html");
		return;
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
	
}
