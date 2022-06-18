package nu.borjessons.clubhouse.integration.tests.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.TeamDto;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;

public class TeamUtil {
  public static TeamRequestModel createRequestModel(List<String> leaderIds, String name) {
    TeamRequestModel teamRequestModel = new TeamRequestModel();
    teamRequestModel.setName(name);
    teamRequestModel.setMinAge(5);
    teamRequestModel.setMaxAge(20);
    teamRequestModel.setLeaderIds(leaderIds);
    return teamRequestModel;
  }

  public static TeamDto createTeam(String clubId, TeamRequestModel teamRequestModel, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams").buildAndExpand(clubId).toUriString();
    ResponseEntity<String> response = RestUtil.postRequest(uri, token, teamRequestModel, String.class);
    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDto.class);

  }

  public static List<Team> getAllTeams(TeamRepository teamRepository) {
    return teamRepository.findAll();
  }

  public static List<TeamDto> getClubTeams(String clubId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams").buildAndExpand(clubId).toUriString();
    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    TeamDto[] teamDtos = RestUtil.deserializeJsonBody(response.getBody(), TeamDto[].class);
    return Arrays.stream(teamDtos).collect(Collectors.toList());
  }

  public static List<TeamDto> getMyTeams(String clubId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/my-teams").buildAndExpand(clubId).toUriString();
    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    TeamDto[] teamDtos = RestUtil.deserializeJsonBody(response.getBody(), TeamDto[].class);
    return Arrays.stream(teamDtos).collect(Collectors.toList());
  }

  public static TeamDto getTeamById(String clubId, String teamId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}")
        .buildAndExpand(clubId, teamId)
        .toUriString();

    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDto.class);
  }

  public static TeamDto joinTeam(String clubId, String teamId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/join")
        .buildAndExpand(clubId, teamId)
        .toUriString();

    ResponseEntity<String> response = RestUtil.putRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDto.class);
  }

  public static void leaveTeam(String clubId, String teamId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/leave")
        .buildAndExpand(clubId, teamId)
        .toUriString();

    ResponseEntity<String> response = RestUtil.putRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static TeamDto removeLeaderFromTeam(String clubId, UserId leaderId, String teamId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/remove-leader")
        .queryParam("leaderId", leaderId)
        .buildAndExpand(clubId, teamId)
        .toUriString();
    ResponseEntity<String> response = RestUtil.putRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDto.class);
  }

  public static TeamDto updateTeam(String clubId, TeamRequestModel teamRequestModel, String teamId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}")
        .buildAndExpand(clubId, teamId)
        .toUriString();
    ResponseEntity<String> response = RestUtil.putRequest(uri, token, teamRequestModel, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDto.class);
  }

  public static TeamDto updateTeamMembers(String clubId, String teamId, List<String> memberList, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/members")
        .buildAndExpand(clubId, teamId)
        .toUriString();
    ResponseEntity<String> response = RestUtil.putRequest(uri, token, memberList, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), TeamDto.class);
  }

  private TeamUtil() {
    throw new IllegalStateException("Utility class");
  }
}
