package nu.borjessons.clubhouse.integration.tests.util;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;

public class RegistrationTestHelper {

  public static UserDTO registerChild(String clubId, String childName, String parentId, String token) throws JsonProcessingException {
    final String uri = IntegrationTestHelper
        .getUriBuilder("/clubs/{clubId}")
        .path("/register-children")
        .queryParam("parentId", parentId)
        .buildAndExpand(clubId).toUriString();

    CreateChildRequestModel createChildRequestModel = createChildModel(childName);
    ResponseEntity<String> response = IntegrationTestHelper.postRequest(uri, token, List.of(createChildRequestModel), String.class);
    return IntegrationTestHelper.deserializeJsonBody(response.getBody(), UserDTO.class);
  }

  private static CreateChildRequestModel createChildModel(String childName) {
    CreateChildRequestModel createChildRequestModel = new CreateChildRequestModel();
    createChildRequestModel.setFirstName(childName);
    createChildRequestModel.setLastName(childName + "son");
    createChildRequestModel.setDateOfBirth("2020-01-01");
    return createChildRequestModel;
  }
}
