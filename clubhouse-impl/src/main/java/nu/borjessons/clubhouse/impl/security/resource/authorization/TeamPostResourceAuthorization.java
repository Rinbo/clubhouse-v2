package nu.borjessons.clubhouse.impl.security.resource.authorization;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.TeamPost;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.TeamPostRepository;
import nu.borjessons.clubhouse.impl.security.SecurityContextFacade;
import nu.borjessons.clubhouse.impl.util.ClubhouseUtils;

@Component
@RequiredArgsConstructor
public class TeamPostResourceAuthorization {
  private static final List<GrantedAuthority> ADMIN_ROLES = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_LEADER"));

  private static Supplier<ResponseStatusException> createAccessDeniedSupplier() {
    return () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
  }

  private final ClubUserRepository clubUserRepository;
  private final TeamPostRepository teamPostRepository;
  private final SecurityContextFacade securityContextFacade;

  public TeamPost getAuthorizedTeamPost(String clubId, TeamPostId teamPostId) {
    if (securityContextFacade.getAuthorities().stream().anyMatch(ADMIN_ROLES::contains)) {
      return teamPostRepository.findByTeamPostId(teamPostId).orElseThrow(ClubhouseUtils.createNotFoundExceptionSupplier("team post not found: " + teamPostId));
    }

    return getTeamPost(getClubUser(securityContextFacade.getAuthenticationPrincipal(), clubId), teamPostId);
  }

  public TeamPost getSelfAuthorizedTeamPost(String clubId, TeamPostId teamPostId) {
    return getTeamPost(getClubUser(securityContextFacade.getAuthenticationPrincipal(), clubId), teamPostId);
  }

  private TeamPost getTeamPost(ClubUser clubUser, TeamPostId teamPostId) {
    return teamPostRepository.findByTeamPostIdAndClubUser(teamPostId, clubUser).orElseThrow(createAccessDeniedSupplier());
  }

  private ClubUser getClubUser(User principal, String clubId) {
    return clubUserRepository.findByClubIdAndUserId(clubId, principal.getId())
        .orElseThrow(createAccessDeniedSupplier());
  }
}
