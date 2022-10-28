package nu.borjessons.clubhouse.impl.security;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.security.filter.AuthenticationFilter;
import nu.borjessons.clubhouse.impl.security.filter.ClubsAuthorizationFilter;
import nu.borjessons.clubhouse.impl.security.filter.TopLevelAuthorizationFilter;
import nu.borjessons.clubhouse.impl.security.provider.ClubTokenAuthenticationProvider;
import nu.borjessons.clubhouse.impl.security.provider.TopLevelAuthenticationProvider;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;
import nu.borjessons.clubhouse.impl.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private final ClubTokenAuthenticationProvider clubTokenAuthenticationProvider;
  private final JWTUtil jwtUtil;
  private final ObjectMapper objectMapper;
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
    http.cors(c -> c.configurationSource(r -> getCorsConfiguration()));

    http.authorizeRequests()
        .antMatchers(SecurityUtil.getPublicUrls()).permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilterAt(new AuthenticationFilter(authenticationManager(), jwtUtil, objectMapper, tokenStore, userService), BasicAuthenticationFilter.class)
        .addFilterAfter(new TopLevelAuthorizationFilter(authenticationManager()), BasicAuthenticationFilter.class)
        .addFilterAfter(new ClubsAuthorizationFilter(authenticationManager()), BasicAuthenticationFilter.class)
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

  private CorsConfiguration getCorsConfiguration() {
    CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
    configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowCredentials(true);
    return configuration;
  }
}
