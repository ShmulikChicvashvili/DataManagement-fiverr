/**
 *
 */

package webService;


import java.util.List;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.servicebook.database.FriendshipsDatabase;
import com.servicebook.database.exceptions.DatabaseUnkownFailureException;
import com.servicebook.database.exceptions.friendships.InvalidParamsException;
import com.servicebook.database.primitives.DBUser;




/**
 * @author Eyal
 *
 */
public class graphSearch extends ServerResource
{
	@Get
	public Representation getFriendsMaxDist()
	{
		JsonRepresentation response = new JsonRepresentation(JSONObject.NULL);
		final String username = getAttribute("username");
		final Integer maxDist = toInteger(getQueryValue("maxDist"));
		if (username == null || maxDist == null)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
			getResponse().setEntity("Missing parameters", MediaType.ALL);
			return null;
		}
		// System.out.println(getApplication().toString());
		// System.out.println(getApplication().getContext());
		// System.out.println(getApplication().getContext().getAttributes());
		final ServletContext sc =
			(ServletContext) getApplication()
				.getContext()
				.getAttributes()
				.get("org.restlet.ext.servlet.ServletContext");
		final FriendshipsDatabase friendsDB =
			(FriendshipsDatabase) sc.getAttribute("friendshipDB");

		List<DBUser> users = null;
		try
		{

			users = friendsDB.getFriendsMaxDist(username, maxDist);
		} catch (DatabaseUnkownFailureException | InvalidParamsException e)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
			getResponse().setEntity("maxDist must be 1 or 2", MediaType.ALL);
			return null;
		}

		final JSONArray arr = new JSONArray();
		for (final DBUser u : users)
		{
			final JSONObject o = new JSONObject();
			o.put("username", u.getUsername());
			o.put("name", u.getName());
			o.put("balance", u.getBalance());
			arr.put(o);
		}

		response = new JsonRepresentation(arr);
		return response;
	}
}
