package servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.ContentType;
import org.w3c.dom.Document;

import com.mysql.fabric.Response;
import com.servicebook.database.MultiTableDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.multiTable.InvalidParameterException;

/**
 * Servlet implementation class DbXML
 */
@WebServlet("/DbXML")
public class DbXML extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DbXML() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ServletContext sc = getServletContext();
		MultiTableDatabase multiDB = (MultiTableDatabase) sc
				.getAttribute("multiTableDB");

		String username = (String) request.getParameter("username");
		Document doc = null;
		try {
			doc = multiDB.toXML(username);
		} catch (DatabaseUnkownFailureException | InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
