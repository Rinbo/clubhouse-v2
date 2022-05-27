package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;
import nu.borjessons.clubhouse.impl.service.TeamPostService;

@RestController
@RequestMapping("/clubs/{clubId}/teams/{teamId}/posts")
@RequiredArgsConstructor
public class TeamPostController {

  private final TeamPostService teamPostService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping
  public TeamPostRecord createPost(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @RequestBody TeamPostRequest teamPostRequest) {

    return teamPostService.createPost(principal, clubId, teamId, teamPostRequest);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping
  public Collection<TeamPostRecord> getPosts(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId) {
    // TODO resourceChecking Either ADMIN or has access to Team
    return null;
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{teamPostId}")
  public TeamPostRecord getPost(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId) {
    // TODO resourceChecking Either ADMIN or has access to Team
    return null;
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/{teamPostId}")
  public TeamPostRecord updatePost(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId) {
    // TODO resourceChecking Must belong to user and user be member of team
    return null;
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/{teamPostId}")
  public void delete(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    // TODO resourceChecking Must be admin or post belong to user
  }

}
