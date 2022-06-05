package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public Collection<TeamPostRecord> getPosts(@PathVariable String clubId, @PathVariable String teamId, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return teamPostService.getPosts(teamId, PageRequest.of(page, size, Sort.by("sticky").descending().and(Sort.by("updatedAt").descending())));
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
      @PathVariable TeamPostId teamPostId, @RequestBody TeamPostRequest teamPostRequest) {
    // TODO make resource auth to make sure this is the user that created it
    return teamPostService.updatePost(principal, clubId, teamId, teamPostId, teamPostRequest);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
  @PutMapping("/{teamPostId}/toggle-sticky")
  public TeamPostRecord toggleSticky(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    return teamPostService.toggleSticky(teamPostId);
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/{teamPostId}")
  public void delete(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    // TODO resourceChecking Must be admin or post belong to user
  }

}
