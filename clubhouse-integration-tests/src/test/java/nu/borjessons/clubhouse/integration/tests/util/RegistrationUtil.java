package nu.borjessons.clubhouse.integration.tests.util;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.security.SecurityConstants;

public class RegistrationUtil {
  public static UserDTO registerChild(String clubId, String childName, String parentId, String token) throws JsonProcessingException {
    final String uri = RestUtil
        .getUriBuilder("/clubs/{clubId}")
        .path("/register-children")
        .queryParam("parentId", parentId)
        .buildAndExpand(clubId).toUriString();

    CreateChildRequestModel createChildRequestModel = createChildModel(childName);
    ResponseEntity<String> response = RestUtil.postRequest(uri, token, List.of(createChildRequestModel), String.class);
    return RestUtil.deserializeJsonBody(response.getBody(), UserDTO.class);
  }

  public static UserDTO registerClub(CreateClubModel createClubModel) throws JsonProcessingException {
    UriComponentsBuilder builder = RestUtil.getUriBuilder(SecurityConstants.CLUB_REGISTRATION_URL);
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<CreateClubModel> httpEntity = new HttpEntity<>(createClubModel);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), UserDTO.class);
  }

  private static CreateChildRequestModel createChildModel(String childName) {
    CreateChildRequestModel createChildRequestModel = new CreateChildRequestModel();
    createChildRequestModel.setFirstName(childName);
    createChildRequestModel.setLastName(childName + "son");
    createChildRequestModel.setDateOfBirth("2020-01-01");
    return createChildRequestModel;
  }
}
