package nu.borjessons.clubhouse.integration.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestModels {
	
	private RequestModels() {
		throw new AssertionError();
	}
	
	public static int userCounter = 1;
	
	public static final String GENERIC_PASSWORD = "1234567890";
	public static final String GENERIC_DATE_OF_BIRTH = "1982-02-16";
	
	public static final String CLUB_1 = "Judo BK";
	public static final String CLUB_2 = "Borjessons BK";
	
	public static final String ADMIN_USER_USERNAME = "admin@outlook.com";
	public static final String[] ADMIN_USER_NAME = new String[] {"Admin", "Adminsson" };
	public static final String NORMAL_USER_USERNAME = "user@outlook.com";
	public static final String[] NORMAL_USER1_NAME = new String[] {"User", "Usersson" };
	public static final String[] NORMAL_USER1_CHILDS_NAME = new String[] {"Child", "Childsson" };
	public static final String CHILD_1_NAME = "Sixten Childsson";
	public static final String CHILD_2_NAME = "Albin Childsson";

	public static Map<String, Object> clubRegistrationRequest(String ClubName, String owner, String[] name) {
		Map<String, Object> organizationRequestModel = new HashMap<>();
		organizationRequestModel.put("name", ClubName);
		organizationRequestModel.put("type", "SPORT");

		Map<String, Object> userRequestModel = userRegistrationRequest("dummy", owner, name);

		organizationRequestModel.put("owner", userRequestModel);
		
		return organizationRequestModel;
	}

	public static Map<String, Object> userWithChildrenRegistrationRequest(String clubId, String user, String[] name,
			List<String> childrenNames) {
		Map<String, Object> userRequestModel = userRegistrationRequest(clubId, user, name);
		
		List<Map<String, Object>> children = childrenNames.stream().map(child -> childRegistrationRequest(child)).collect(Collectors.toList());
		
		userRequestModel.put("children", children);
		
		return userRequestModel;
	}
	
	private static Map<String, Object> userRegistrationRequest(String clubId, String user, String[] name) {
		Map<String, Object> userRequestModel = new HashMap<>();

		userRequestModel.put("email", user);
		userRequestModel.put("firstName", name[0]);
		userRequestModel.put("lastName", name[1]);
		userRequestModel.put("password", GENERIC_PASSWORD);
		userRequestModel.put("dateOfBirth", GENERIC_DATE_OF_BIRTH);
		userRequestModel.put("clubId", clubId);
		
		userCounter++;

		return userRequestModel;
	}

	public static Map<String, Object> childRegistrationRequest(String name) {
		Map<String, Object> childRequestModel = new HashMap<>();
		
		String[] nameArr = name.split(" ");

		childRequestModel.put("firstName", nameArr[0]);
		childRequestModel.put("lastName", nameArr[1]);
		childRequestModel.put("dateOfBirth", GENERIC_DATE_OF_BIRTH);
		
		userCounter++;

		return childRequestModel;
	}

	public static Map<String, Object> loginRequest(String username) {
		Map<String, Object> userRequestModel = new HashMap<>();
		userRequestModel.put("username", username);
		userRequestModel.put("password", GENERIC_PASSWORD);

		return userRequestModel;
	}
	
	public static Map<String, Object> loginRequest(String username, String password) {
		Map<String, Object> userRequestModel = new HashMap<>();
		userRequestModel.put("username", username);
		userRequestModel.put("password", password);

		return userRequestModel;
	}

}
