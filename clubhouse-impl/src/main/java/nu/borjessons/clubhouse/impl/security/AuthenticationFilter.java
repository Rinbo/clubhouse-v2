package nu.borjessons.clubhouse.impl.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.controller.model.request.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
  public void successfulAuthentication(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {
    UserDetails userDetails = (User) auth.getPrincipal();
    userService.updateUserLoginTime(userDetails.getUsername());
    String token = SecurityConstants.TOKEN_PREFIX + jwtUtil.generateToken(userDetails);
    res.addHeader(SecurityConstants.AUTHORIZATION, token);
  }
}
