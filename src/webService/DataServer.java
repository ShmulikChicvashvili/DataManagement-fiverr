/**
 *
 */

package webService;


import javax.servlet.ServletContext;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;




/**
 * @author Eyal
 *
 */
public class DataServer extends Application
{
	@Override
	public Restlet createInboundRoot()
	{
		// think of this as a dispatcher
		final Router router = new Router();

		final ServletContext ctx =
			(ServletContext) getContext().getAttributes().get(
				"org.restlet.ext.servlet.ServletContext");
		setContext(getContext());
		router.attach("/u/{username}", graphSearch.class);
		
		return router;
	}
}
