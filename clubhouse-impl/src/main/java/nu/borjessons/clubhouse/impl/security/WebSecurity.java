package nu.borjessons.clubhouse.impl.security;

import static nu.borjessons.clubhouse.impl.security.SecurityUtil.CLUB_REGISTRATION_URL;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.FAMILY_REGISTRATION_URL;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.H2_CONSOLE;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.PUBLIC_CLUB_URLS;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.USER_REGISTRATION_URL;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
  private final ClubUserRepository clubUserRepository;
  private final CustomCorsConfiguration customCorsConfiguration;
  private final JWTUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.cors(c -> c.configurationSource(customCorsConfiguration));

    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, USER_REGISTRATION_URL, CLUB_REGISTRATION_URL, FAMILY_REGISTRATION_URL).permitAll()
        .antMatchers(HttpMethod.GET, PUBLIC_CLUB_URLS).permitAll()
        .antMatchers(H2_CONSOLE).permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilterAt(new AuthenticationFilter(authenticationManager(), jwtUtil, userService), BasicAuthenticationFilter.class)
        .addFilterAfter(new TopLevelAuthorizationFilter(jwtUtil, userService), BasicAuthenticationFilter.class)
        .addFilterAfter(new ClubsAuthorizationFilter(clubUserRepository, jwtUtil, userService), BasicAuthenticationFilter.class)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.headers()
        .frameOptions()
        .disable();

    http.logout()
        .deleteCookies(SecurityUtil.JWT_TOKEN_KEY)
        .logoutUrl("/logout")
        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK));
  }
}
