package nu.borjessons.clubhouse.impl.security;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.security.filter.AuthenticationFilter;
import nu.borjessons.clubhouse.impl.security.filter.ClubsAuthorizationFilter;
import nu.borjessons.clubhouse.impl.security.filter.TopLevelAuthorizationFilter;
import nu.borjessons.clubhouse.impl.security.provider.ClubTokenAuthenticationProvider;
import nu.borjessons.clubhouse.impl.security.provider.TopLevelAuthenticationProvider;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
  @Bean
  public AuthenticationManager createAuthenticationManager(HttpSecurity http, ClubTokenAuthenticationProvider clubTokenAuthenticationProvider,
      TopLevelAuthenticationProvider topLevelAuthenticationProvider) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

    return authenticationManagerBuilder
        .authenticationProvider(clubTokenAuthenticationProvider)
        .authenticationProvider(topLevelAuthenticationProvider).build();
  }

  // TODO - Tomorrow: Add fake endpoints and try out different ADMIN/USER combinations
  // Then fix UserDetails and TokenStore
  @Bean
  public SecurityFilterChain createFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager,
      AuthenticationFilter authenticationFilter) throws Exception {
    httpSecurity.csrf().disable();
    httpSecurity.cors(c -> c.configurationSource(r -> getCorsConfiguration()));

    httpSecurity.authorizeRequests()
        .antMatchers(SecurityUtil.getPublicUrls()).permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilterAfter(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(new TopLevelAuthorizationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(new ClubsAuthorizationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    httpSecurity.logout()
        .logoutSuccessHandler((request, response, authentication) -> {
          response.setStatus(HttpServletResponse.SC_OK);
          response.setHeader(HttpHeaders.SET_COOKIE, SecurityUtil.getLogoutCookie().toString());
        });

    return httpSecurity.build();
  }

  private CorsConfiguration getCorsConfiguration() {
    CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
    configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:5173"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowCredentials(true);
    return configuration;
  }
}
