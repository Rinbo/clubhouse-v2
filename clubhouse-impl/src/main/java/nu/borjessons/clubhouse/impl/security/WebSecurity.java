package nu.borjessons.clubhouse.impl.security;

import static nu.borjessons.clubhouse.impl.security.SecurityUtil.H2_CONSOLE;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.PUBLIC_CLUB_URLS;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.REGISTRATION_URLS;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.VALIDATE_TOKEN_URL;

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
import nu.borjessons.clubhouse.impl.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
  private final ClubTokenAuthenticationProvider clubTokenAuthenticationProvider;
  private final CustomCorsConfiguration customCorsConfiguration;
  private final JWTUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final TokenStore tokenStore;
  private final TopLevelAuthenticationProvider topLevelAuthenticationProvider;
  private final UserService userService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .authenticationProvider(clubTokenAuthenticationProvider)
        .authenticationProvider(topLevelAuthenticationProvider)
        .userDetailsService(userService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.cors(c -> c.configurationSource(customCorsConfiguration));

    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, REGISTRATION_URLS).permitAll()
        .antMatchers(HttpMethod.GET, PUBLIC_CLUB_URLS, VALIDATE_TOKEN_URL).permitAll()
        .antMatchers(H2_CONSOLE).permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilterAt(new AuthenticationFilter(authenticationManager(), jwtUtil, tokenStore, userService), BasicAuthenticationFilter.class)
        .addFilterAfter(new TopLevelAuthorizationFilter(authenticationManager()), BasicAuthenticationFilter.class)
        .addFilterAfter(new ClubsAuthorizationFilter(authenticationManager()),
            BasicAuthenticationFilter.class)
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
