package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import nu.borjessons.clubhouse.impl.dto.TeamPostCommentRecord;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostCommentRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;
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
  private static final List<GrantedAuthority> ADMIN_ROLES = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_LEADER"));

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
  public TeamPostRecord getPost(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    return teamPostService.getPost(teamPostId);
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/{teamPostId}")
  public TeamPostRecord updatePost(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId, @RequestBody TeamPostRequest teamPostRequest) {
    return teamPostService.updatePost(principal, clubId, teamId, teamPostId, teamPostRequest);
  }

  @PreAuthorize("hasRole('USER') or hasRole('LEADER')")
  @PutMapping("/{teamPostId}/toggle-sticky")
  public TeamPostRecord toggleSticky(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId) {
    return teamPostService.toggleSticky(teamPostId);
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/{teamPostId}")
  public ResponseEntity<String> delete(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId) {
    if (principal.getAuthorities().stream().anyMatch(ADMIN_ROLES::contains)) {
      teamPostService.deletePost(teamPostId);
    } else {
      teamPostService.deletePost(principal, clubId, teamPostId);
    }

    return ResponseEntity.ok("Post successfully deleted");
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/{teamPostId}/comment")
  public TeamPostRecord createTeamPostComment(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId, @RequestBody TeamPostCommentRequest teamPostCommentRequest) {
    return teamPostService.createComment(principal, clubId, teamPostId, teamPostCommentRequest);
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/{teamPostId}/comments/{teamPostCommentId}")
  public TeamPostRecord updateTeamPostComment(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId, @PathVariable long teamPostCommentId, @RequestBody TeamPostCommentRequest teamPostCommentRequest) {
    return teamPostService.updateComment(principal, clubId, teamPostCommentId, teamPostCommentRequest);
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/{teamPostId}/comments/{teamPostCommentId}")
  public ResponseEntity<String> deleteTeamPostComment(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @PathVariable TeamPostId teamPostId, @PathVariable long teamPostCommentId) {
    if (principal.getAuthorities().stream().anyMatch(ADMIN_ROLES::contains)) {
      teamPostService.deleteTeamPostComment(teamPostCommentId);
    } else {
      teamPostService.deleteTeamPostComment(principal, clubId, teamPostCommentId);
    }

    return ResponseEntity.ok("Comment successfully deleted");
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{teamPostId}/comments")
  public Collection<TeamPostCommentRecord> getTeamPostComments(@PathVariable String clubId, @PathVariable String teamId, @PathVariable TeamPostId teamPostId,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return teamPostService.getTeamPostComments(teamPostId, PageRequest.of(page, size));
  }
}
