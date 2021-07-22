package nu.borjessons.clubhouse.integration.tests.util;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;

public class TeamUtil {
  public static TeamRequestModel createRequestModel(List<String> leaderIds, String name) {
    TeamRequestModel teamRequestModel = new TeamRequestModel();
    teamRequestModel.setName(name);
    teamRequestModel.setMinAge(5);
    teamRequestModel.setMaxAge(12);
    teamRequestModel.setLeaderIds(leaderIds);
    return teamRequestModel;
  }

  public static TeamDTO createTeam(String clubId, TeamRequestModel teamRequestModel, String token) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams").buildAndExpand(clubId).toUriString();
    final ResponseEntity<String> response = RestUtil.postRequest(uri, token, teamRequestModel, String.class);
    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDTO.class);

  }

  private TeamUtil() {
    throw new IllegalStateException("Utility class");
  }
}
