
package filters;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;




/**
 * Servlet Filter implementation class InactivateUserFilter
 */
@WebFilter("/InactivateUserFilter")
public class InactivateUserFilter implements Filter
{
	
	/**
	 * Default constructor.
	 */
	public InactivateUserFilter()
	{
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
	}
	
	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(
		ServletRequest req,
		ServletResponse res,
		FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
		{
			response.sendRedirect("/HW5/login.html");
			return;
		}
		for (Cookie c : cookies)
		{
			if (c.getName().equals("username")
				|| c.getName().equals("password"))
			{
				c.setMaxAge(0);
				response.addCookie(c);
			}
		}
		chain.doFilter(request, response);
	}
	
	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException
	{
		// TODO Auto-generated method stub
	}
	
}
