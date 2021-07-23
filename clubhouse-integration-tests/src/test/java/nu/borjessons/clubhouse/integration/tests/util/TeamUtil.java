package nu.borjessons.clubhouse.integration.tests.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    teamRequestModel.setMaxAge(20);
    teamRequestModel.setLeaderIds(leaderIds);
    return teamRequestModel;
  }

  public static TeamDTO createTeam(String clubId, TeamRequestModel teamRequestModel, String token) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams").buildAndExpand(clubId).toUriString();
    final ResponseEntity<String> response = RestUtil.postRequest(uri, token, teamRequestModel, String.class);
    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDTO.class);

  }

  public static List<TeamDTO> getMyTeams(String clubId, String token) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/my-teams").buildAndExpand(clubId).toUriString();
    final ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    final TeamDTO[] teamDTOS = RestUtil.deserializeJsonBody(response.getBody(), TeamDTO[].class);
    return Arrays.stream(teamDTOS).collect(Collectors.toList());
  }

  public static List<TeamDTO> getClubTeams(String clubId, String token) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams").buildAndExpand(clubId).toUriString();
    final ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    final TeamDTO[] teamDTOS = RestUtil.deserializeJsonBody(response.getBody(), TeamDTO[].class);
    return Arrays.stream(teamDTOS).collect(Collectors.toList());
  }

  public static TeamDTO getTeamById(String clubId, String teamId, String token) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}")
        .buildAndExpand(clubId, teamId)
        .toUriString();

    final ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDTO.class);
  }

  private TeamUtil() {
    throw new IllegalStateException("Utility class");
  }
}
