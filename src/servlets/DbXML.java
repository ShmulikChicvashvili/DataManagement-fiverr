package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
		String username = request.getParameter("username");
		String typeStr = request.getParameter("type");
		if (username == null || typeStr == null) {
			// TODO error
			return;
		}
		int type = 0;
		try {
			type = new Integer(typeStr);
		} catch (NumberFormatException e) {
			// TODO type is not a number
			return;
		}

		String xslPath = null;
		switch (type) {
		case 1:
			xslPath = "/users.xsl";
			break;
		case 2:
			xslPath = "/friends.xsl";
			break;
		case 3:
			xslPath = "/offeringUsers.xsl";
			break;
		default:
			// TODO return invalid parameter
			return;
		}

		ServletContext sc = getServletContext();
		MultiTableDatabase multiDB = (MultiTableDatabase) sc
				.getAttribute("multiTableDB");

		Document doc = null;
		try {
			doc = multiDB.toXML(username);
		} catch (DatabaseUnkownFailureException | InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Transformer transformer = null;
		try (PrintWriter out = response.getWriter()) {
			transformer = TransformerFactory.newInstance().newTransformer(
					new javax.xml.transform.stream.StreamSource(getClass()
							.getResourceAsStream(xslPath)));
			transformer.transform(new DOMSource(doc), new StreamResult(out));
			response.setContentType("application/xml");

			// just for debugging
			DOMResult domResult = new DOMResult();
			transformer.transform(new DOMSource(doc), domResult);
			StreamResult result = new StreamResult(System.out);
			System.out.println("dbXML after using XSL: " + xslPath);
			transformer.transform(new DOMSource(domResult.getNode()), result);
			System.out.println("");
		} catch (TransformerConfigurationException
				| TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
