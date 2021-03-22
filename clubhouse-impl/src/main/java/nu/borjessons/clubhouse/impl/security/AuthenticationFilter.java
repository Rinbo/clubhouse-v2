package nu.borjessons.clubhouse.impl.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.controller.model.request.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;
  private final UserService userService;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
    try {
      UserLoginRequestModel credentials = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);
      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(credentials.getUsername().toLowerCase().trim(), credentials.getPassword()));
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Attempt to authenticate failed. Unable to read input stream from request object");
    }
  }

  @Override
  public void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {
    User user = (User) auth.getPrincipal();
    setClub(user, req.getHeader("clubId"));
    userService.updateUserLoginTime(user.getUsername());
    String token = SecurityConstants.TOKEN_PREFIX + jwtUtil.doGenerateToken(user.getUsername());
    res.addHeader(SecurityConstants.AUTHORIZATION, token);
  }

  private void setClub(User user, String clubId) {
    if (clubId == null || user.getActiveClubId().equals("clubId")) return;
    Club club = user
        .getClubs()
        .stream()
        .filter(c -> c.getClubId().equals(clubId))
        .findFirst()
        .orElseThrow(() -> new AccessDeniedException("You do not have access to this club"));
    userService.switchClub(user, club);
  }
}
