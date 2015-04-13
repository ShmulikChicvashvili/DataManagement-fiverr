
package filters;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.servicebook.database.UsersDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.users.InvalidParamsException;




/**
 * Servlet Filter implementation class AuthenticationFilter
 */
@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter
{
	private ServletContext context;
	
	
	
	/**
	 * Default constructor.
	 */
	public AuthenticationFilter()
	{}
	
	
	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy()
	{
		//
	}
	
	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(
		ServletRequest request,
		ServletResponse response,
		FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		if (uri.endsWith("login.html")
			|| uri.endsWith("registration.html")
			|| uri.endsWith("Register")
			|| uri.endsWith("Login")
			|| uri.endsWith(".css")
			|| uri.endsWith(".js"))
		{
			chain.doFilter(req, res);
		} else if (authenticateSession(req))
		{
			chain.doFilter(req, res);
		} else if (authenticateCookies(req))
		{
			createSession(req);
			chain.doFilter(req, res);
		} else
		{
			res.sendRedirect("/HW5/login.html");
		}
	}
	
	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException
	{
		context = fConfig.getServletContext();
	}
	
	
	private static boolean authenticateSession(HttpServletRequest request)
	{
		boolean $ = false;
		
		HttpSession session = request.getSession(false);
		$ = session != null;
		
		return $;
	}
	
	
	private boolean authenticateCookies(HttpServletRequest request)
	{
		boolean $ = false;
		
		String username = null;
		String password = null;
		Cookie[] cookies = request.getCookies();
		if (cookies == null) { return $; }
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals("username"))
			{
				username = cookie.getValue();
			}
			if (cookie.getName().equals("password"))
			{
				password = cookie.getValue();
			}
		}
		if (username == null || password == null) { return $; }
		UsersDatabase userDB = (UsersDatabase) context.getAttribute("userDB");
		try
		{
			$ = userDB.validateUser(username, password);
		} catch (DatabaseUnkownFailureException e)
		{
			$ = false;
		} catch (InvalidParamsException e)
		{
			$ = false;
		}
		
		return $;
	}
	
	
	private static String getUsernameFromCookie(HttpServletRequest request)
	{
		String $ = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals("username"))
			{
				$ = cookie.getValue();
			}
		}
		return $;
	}
	
	
	private static void createSession(HttpServletRequest request)
	{
		HttpSession session = request.getSession(true);
		session.setAttribute("username", getUsernameFromCookie(request));
	}
	
}
