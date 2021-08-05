package nu.borjessons.clubhouse.impl.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.rest.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;
  private final TokenStore tokenStore;
  private final UserService userService;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
    try {
      UserLoginRequestModel credentials = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);
      String username = credentials.getUsername().toLowerCase().trim();
      String password = credentials.getPassword();
      return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Attempt to authenticate failed. Unable to read input stream from request object");
    }
  }

  @Override
  public void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {
    User user = (User) auth.getPrincipal();
    String username = user.getUsername();
    userService.updateUserLoginTime(username);
    String token = jwtUtil.doGenerateToken(username);
    Cookie cookie = new Cookie(SecurityUtil.JWT_TOKEN_KEY, token);
    cookie.setMaxAge(604800);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(req.isSecure());
    res.addCookie(cookie);
    tokenStore.put(username, token);
  }
}
