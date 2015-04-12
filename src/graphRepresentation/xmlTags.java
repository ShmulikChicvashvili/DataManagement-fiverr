package graphRepresentation;

public class xmlTags {
	public static String ROOT_TAG = "database";
	public static String CURR_USER_TAG = "currUser";
	public static String USERS_TAG = "users";
	public static String USER_TAG = "user";
	public static String USERNAME_TAG = "username";
	public static String NAME_TAG = "name";
	public static String BALANCE_TAG = "balance";
	public static String FRIENDSHIPS_TAG = "friendships";
	public static String FRIENDSHIP_TAG = "friendship";
	public static String IS_OFFERING_ATT = "isOffering";

	public enum IsOfferingOpts {
		Y {
			public String toXML() {
				return "y";
			}
		},
		N {
			public String toXML() {
				return "y";
			}
		};

		public abstract String toXML();
	}
}
