package nu.borjessons.clubhouse.impl.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.security.TokenStore;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;
import nu.borjessons.clubhouse.impl.service.UserService;

@Slf4j
@Component
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;
  private final ObjectMapper objectMapper;
  private final TokenStore tokenStore;
  private final UserService userService;

  public AuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationManager authenticationManager1, JWTUtil jwtUtil,
      ObjectMapper objectMapper, TokenStore tokenStore, UserService userService) {
    super(authenticationManager);
    this.authenticationManager = authenticationManager1;
    this.jwtUtil = jwtUtil;
    this.objectMapper = objectMapper;
    this.tokenStore = tokenStore;
    this.userService = userService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
    if (!req.getMethod().equals("POST")) {
      throw new AuthenticationServiceException("Authentication method not supported: " + req.getMethod());
    }

    try {
      UserLoginRequestModel credentials = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);
      String username = credentials.getUsername().toLowerCase().trim();
      String password = credentials.getPassword();
      return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (IOException e) {
      throw new BadCredentialsException("Attempt to authenticate failed. Unable to read input stream from request object");
    }
  }

  @Override
  public void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
    User user = (User) auth.getPrincipal();
    String username = user.getUsername();
    UserDto userDto = userService.updateUserLoginTime(username);
    String token = jwtUtil.doGenerateToken(username);

    ResponseCookie responseCookie = ResponseCookie
        .from(SecurityUtil.JWT_TOKEN_KEY, token)
        .httpOnly(true)
        .path("/")
        .maxAge(604800)
        .sameSite("None")
        .secure(true)
        .build();

    res.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    res.getWriter().write(objectMapper.writeValueAsString(userDto));
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    tokenStore.put(username, token);
  }
}
