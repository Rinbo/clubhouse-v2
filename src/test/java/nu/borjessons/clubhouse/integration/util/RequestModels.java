package nu.borjessons.clubhouse.integration.util;

import java.util.HashMap;
import java.util.Map;

public class RequestModels {
	
	private RequestModels() {
		throw new AssertionError();
	}
	
	public static int userCounter = 1;
	
	public static final String GENERIC_PASSWORD = "1234567890";
	
	public static final String CLUB1 = "Judo BK";
	
	public static final String ADMIN_USER_USERNAME = "admin@outlook.com";
	public static final String[] ADMIN_USER_NAME = new String[] {"Admin", "Adminsson" };
	public static final String NORMAL_USER_USERNAME = "user@outlook.com";
	public static final String[] NORMAL_USER1_NAME = new String[] {"User", "Usersson" };
	public static final String[] NORMAL_USER1_CHILDS_NAME = new String[] {"Child", "Childsson" };

	public static Map<String, Object> clubRegistrationRequest(String ClubName, String owner, String[] name) {
		Map<String, Object> organizationRequestModel = new HashMap<>();
		organizationRequestModel.put("name", ClubName);
		organizationRequestModel.put("type", "SPORT");

		Map<String, Object> userRequestModel = new HashMap<>();

		userRequestModel.put("email", owner);
		userRequestModel.put("firstName", name[0]);
		userRequestModel.put("lastName", name[1]);
		userRequestModel.put("password", GENERIC_PASSWORD);
		userRequestModel.put("clubId", "dummy");

		organizationRequestModel.put("owner", userRequestModel);

		userCounter++;
		
		return organizationRequestModel;
	}

	public static Map<String, Object> userRegistrationRequest(String clubId, String user, String[] name) {
		Map<String, Object> userRequestModel = new HashMap<>();

		userRequestModel.put("email", NORMAL_USER_USERNAME);
		userRequestModel.put("firstName", name[0]);
		userRequestModel.put("lastName", name[1]);
		userRequestModel.put("password", GENERIC_PASSWORD);
		userRequestModel.put("clubId", clubId);
		
		userCounter++;

		return userRequestModel;
	}

	public static Map<String, Object> childRegistrationRequest(String[] name) {
		Map<String, Object> userRequestModel = new HashMap<>();

		userRequestModel.put("firstName", name[0]);
		userRequestModel.put("lastName", name[1]);
		
		userCounter++;

		return userRequestModel;
	}

	public static Map<String, Object> loginRequest(String username) {
		Map<String, Object> userRequestModel = new HashMap<>();
		userRequestModel.put("username", username);
		userRequestModel.put("password", GENERIC_PASSWORD);

		return userRequestModel;
	}

}
