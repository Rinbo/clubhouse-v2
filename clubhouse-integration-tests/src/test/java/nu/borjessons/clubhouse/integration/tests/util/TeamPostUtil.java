package nu.borjessons.clubhouse.integration.tests.util;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostCommentRecord;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostCommentRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;

public class TeamPostUtil {
  public static final String BODY = "My awesome body";
  public static final String TITLE = "My cool title";

  public static TeamPostRecord create(String token, String clubId, String teamId) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts").buildAndExpand(clubId, teamId).toUriString();
    ResponseEntity<String> responseEntity = RestUtil.postRequest(uri, token, createTeamPostRequest(), String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    return RestUtil.deserializeJsonBody(responseEntity.getBody(), TeamPostRecord.class);
  }

  public static TeamPostRecord createComment(String token, String clubId, String teamId, TeamPostId teamPostId) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts/{teamPostId}/comments").buildAndExpand(clubId, teamId, teamPostId).toUriString();
    ResponseEntity<String> responseEntity = RestUtil.postRequest(uri, token, new TeamPostCommentRequest("a comment"), String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    return RestUtil.deserializeJsonBody(responseEntity.getBody(), TeamPostRecord.class);
  }

  public static void delete(String token, String clubId, String teamId, TeamPostId teamPostId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts/{teamPostId}").buildAndExpand(clubId, teamId, teamPostId).toUriString();
    ResponseEntity<String> responseEntity = RestUtil.deleteRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals("Post successfully deleted", responseEntity.getBody());
  }

  public static void deleteComment(String token, String clubId, String teamId, TeamPostId teamPostId, long teamPostCommentId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts/{teamPostId}/comments/{teamPostCommentId}")
        .buildAndExpand(clubId, teamId, teamPostId, teamPostCommentId)
        .toUriString();
    ResponseEntity<String> responseEntity = RestUtil.deleteRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals("Comment successfully deleted", responseEntity.getBody());
  }

  public static TeamPostRecord get(String token, String clubId, String teamId, TeamPostId teamPostId) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts/{teamPostId}").buildAndExpand(clubId, teamId, teamPostId).toUriString();
    ResponseEntity<String> responseEntity = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    return RestUtil.deserializeJsonBody(responseEntity.getBody(), TeamPostRecord.class);
  }

  public static List<TeamPostRecord> getAll(String token, String clubId, String teamId) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts").buildAndExpand(clubId, teamId).toUriString();
    ResponseEntity<String> responseEntity = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    TeamPostRecord[] teamPostRecords = RestUtil.deserializeJsonBody(responseEntity.getBody(), TeamPostRecord[].class);
    return Arrays.stream(teamPostRecords).toList();
  }

  public static List<TeamPostCommentRecord> getComments(String token, String clubId, String teamId, TeamPostId teamPostId, int page, int size)
      throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts/{teamPostId}/comments")
        .queryParam("page", page)
        .queryParam("size", size)
        .buildAndExpand(clubId, teamId, teamPostId).toUriString();
    ResponseEntity<String> responseEntity = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    TeamPostCommentRecord[] teamPostCommentRecords = RestUtil.deserializeJsonBody(responseEntity.getBody(), TeamPostCommentRecord[].class);
    return Arrays.stream(teamPostCommentRecords).toList();
  }

  public static TeamPostRecord update(String token, String clubId, String teamId, TeamPostId teamPostId) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts/{teamPostId}").buildAndExpand(clubId, teamId, teamPostId).toUriString();
    TeamPostRequest teamPostRequest = new TeamPostRequest("Updated title", "Updated body");
    ResponseEntity<String> responseEntity = RestUtil.putRequest(uri, token, teamPostRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    return RestUtil.deserializeJsonBody(responseEntity.getBody(), TeamPostRecord.class);
  }

  public static TeamPostRecord updateComment(String token, String clubId, String teamId, TeamPostId teamPostId, long teamPostCommentId)
      throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/posts/{teamPostId}/comments/{teamPostCommentId}")
        .buildAndExpand(clubId, teamId, teamPostId, teamPostCommentId)
        .toUriString();
    ResponseEntity<String> responseEntity = RestUtil.putRequest(uri, token, new TeamPostCommentRequest("updated Comment"), String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    return RestUtil.deserializeJsonBody(responseEntity.getBody(), TeamPostRecord.class);
  }

  private static TeamPostRequest createTeamPostRequest() {
    return new TeamPostRequest(TITLE, BODY);
  }
}
