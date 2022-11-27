package nu.borjessons.clubhouse.impl.security.resource.authorization;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.AppUserDetails;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.TeamPost;
import nu.borjessons.clubhouse.impl.data.TeamPostComment;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.TeamPostCommentRepository;
import nu.borjessons.clubhouse.impl.repository.TeamPostRepository;
import nu.borjessons.clubhouse.impl.security.SecurityContextFacade;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;
import nu.borjessons.clubhouse.impl.util.AppUtils;

@Component
@RequiredArgsConstructor
public class TeamPostResourceAuthorization {
  private final ClubUserRepository clubUserRepository;
  private final SecurityContextFacade securityContextFacade;
  private final TeamPostCommentRepository teamPostCommentRepository;
  private final TeamPostRepository teamPostRepository;

  public TeamPost getAuthorizedTeamPost(String clubId, TeamPostId teamPostId) {
    if (securityContextFacade.getAuthorities().stream().anyMatch(SecurityUtil.ADMIN_LEADER_ROLES::contains)) {
      return teamPostRepository.findByTeamPostId(teamPostId).orElseThrow(AppUtils.createNotFoundExceptionSupplier("team post not found: " + teamPostId));
    }

    return getTeamPost(getClubUser(securityContextFacade.getAuthenticationPrincipal(), clubId), teamPostId);
  }

  public TeamPostComment getAuthorizedTeamPostComment(String clubId, long teamPostCommentId) {
    if (securityContextFacade.getAuthorities().stream().anyMatch(SecurityUtil.ADMIN_LEADER_ROLES::contains)) {
      return teamPostCommentRepository.findById(teamPostCommentId)
          .orElseThrow(AppUtils.createNotFoundExceptionSupplier("comment not found: " + teamPostCommentId));
    }
    return getTeamPostComment(getClubUser(securityContextFacade.getAuthenticationPrincipal(), clubId), teamPostCommentId);
  }

  public TeamPost getSelfAuthorizedTeamPost(String clubId, TeamPostId teamPostId) {
    return getTeamPost(getClubUser(securityContextFacade.getAuthenticationPrincipal(), clubId), teamPostId);
  }

  public TeamPostComment getSelfAuthorizedTeamPostComment(String clubId, long teamPostCommentId) {
    return getTeamPostComment(getClubUser(securityContextFacade.getAuthenticationPrincipal(), clubId), teamPostCommentId);
  }

  private ClubUser getClubUser(AppUserDetails principal, String clubId) {
    return clubUserRepository.findByClubIdAndUserId(clubId, principal.getId())
        .orElseThrow(AppUtils.createAccessDeniedSupplier());
  }

  private TeamPost getTeamPost(ClubUser clubUser, TeamPostId teamPostId) {
    return teamPostRepository.findByTeamPostIdAndClubUser(teamPostId, clubUser).orElseThrow(AppUtils.createAccessDeniedSupplier());
  }

  private TeamPostComment getTeamPostComment(ClubUser clubUser, long teamPostCommentId) {
    return teamPostCommentRepository.findByIdAndClubUser(teamPostCommentId, clubUser)
        .orElseThrow(AppUtils.createNotFoundExceptionSupplier("teamPostCommentId not found: " + teamPostCommentId));
  }
}
