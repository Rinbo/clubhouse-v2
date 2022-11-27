package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
import nu.borjessons.clubhouse.impl.data.AppUserDetails;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostCommentRecord;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostCommentRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;
import nu.borjessons.clubhouse.impl.security.resource.authorization.TeamPostResourceAuthorization;
import nu.borjessons.clubhouse.impl.service.TeamPostService;

/**
 * Read access for all members of club
 * Write access to own post
 * Toggle sticky only for {@code ADMINS} and {@code LEADER}
 */
@RestController
@RequestMapping("/clubs/{clubId}/teams/{teamId}/posts")
@RequiredArgsConstructor
public class TeamPostController {
  private final TeamPostResourceAuthorization teamPostResourceAuthorization;
  private final TeamPostService teamPostService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping
  public TeamPostRecord createPost(@AuthenticationPrincipal AppUserDetails principal, @PathVariable String clubId, @PathVariable String teamId,
      @RequestBody TeamPostRequest teamPostRequest) {

    return teamPostService.createPost(principal.getId(), clubId, teamId, teamPostRequest);
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/{teamPostId}/comments")
  public TeamPostRecord createTeamPostComment(@AuthenticationPrincipal AppUserDetails principal, @PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId, @RequestBody TeamPostCommentRequest teamPostCommentRequest) {
    return teamPostService.createComment(principal.getId(), clubId, teamPostId, teamPostCommentRequest);
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/{teamPostId}")
  public ResponseEntity<String> deletePost(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    teamPostService.deletePost(teamPostResourceAuthorization.getAuthorizedTeamPost(clubId, teamPostId));
    return ResponseEntity.ok("Post successfully deleted");
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/{teamPostId}/comments/{teamPostCommentId}")
  public ResponseEntity<String> deleteTeamPostComment(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId,
      @PathVariable long teamPostCommentId) {
    teamPostService.deleteTeamPostComment(teamPostResourceAuthorization.getAuthorizedTeamPostComment(clubId, teamPostCommentId));

    return ResponseEntity.ok("Comment successfully deleted");
  }

  @GetMapping("/{teamPostId}/comments/size")
  public ResponseEntity<Integer> getCommentSize(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    return ResponseEntity.ok(teamPostService.getCommentSize(teamPostId));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{teamPostId}")
  public TeamPostRecord getPost(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    return teamPostService.getPost(teamPostId);
  }

  @GetMapping("/size")
  public ResponseEntity<Integer> getPostSize(@PathVariable String clubId, @PathVariable String teamId) {
    return ResponseEntity.ok(teamPostService.getSize(teamId));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping
  public Collection<TeamPostRecord> getPosts(@PathVariable String clubId, @PathVariable String teamId, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return teamPostService.getPosts(teamId, PageRequest.of(page, size, Sort.by("sticky").descending().and(Sort.by("updatedAt").descending())));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{teamPostId}/comments")
  public Collection<TeamPostCommentRecord> getTeamPostComments(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return teamPostService.getTeamPostComments(teamPostId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
  }

  @PreAuthorize("hasRole('USER') or hasRole('LEADER')")
  @PutMapping("/{teamPostId}/toggle-sticky")
  public TeamPostRecord toggleSticky(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    return teamPostService.toggleSticky(teamPostId);
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/{teamPostId}")
  public TeamPostRecord updatePost(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId,
      @RequestBody TeamPostRequest teamPostRequest) {
    return teamPostService.updatePost(teamPostResourceAuthorization.getSelfAuthorizedTeamPost(clubId, teamPostId), teamPostRequest);
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/{teamPostId}/comments/{teamPostCommentId}")
  public TeamPostRecord updateTeamPostComment(@PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId, @PathVariable long teamPostCommentId, @RequestBody TeamPostCommentRequest teamPostCommentRequest) {
    return teamPostService.updateComment(teamPostResourceAuthorization.getSelfAuthorizedTeamPostComment(clubId, teamPostCommentId), teamPostCommentRequest);
  }
}
