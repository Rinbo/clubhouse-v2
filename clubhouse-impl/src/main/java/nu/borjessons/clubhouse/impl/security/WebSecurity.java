package nu.borjessons.clubhouse.impl.security;

import static nu.borjessons.clubhouse.impl.security.SecurityConstants.CLUB_REGISTRATION_URL;
import static nu.borjessons.clubhouse.impl.security.SecurityConstants.FAMILY_REGISTRATION_URL;
import static nu.borjessons.clubhouse.impl.security.SecurityConstants.H2_CONSOLE;
import static nu.borjessons.clubhouse.impl.security.SecurityConstants.PUBLIC_CLUB_URLS;
import static nu.borjessons.clubhouse.impl.security.SecurityConstants.USER_REGISTRATION_URL;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

  private final JWTUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final ClubUserRepository clubUserRepository;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable();

    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, USER_REGISTRATION_URL, CLUB_REGISTRATION_URL, FAMILY_REGISTRATION_URL)
        .permitAll()
        .antMatchers(HttpMethod.GET, PUBLIC_CLUB_URLS)
        .permitAll()
        .antMatchers(H2_CONSOLE)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilter(new AuthenticationFilter(authenticationManager(), jwtUtil, userService))
        .addFilter(new AuthorizationFilter(authenticationManager(), clubUserRepository, userService, jwtUtil))
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.headers().frameOptions().disable();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
    configuration.addAllowedOriginPattern("*");
    configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
    configuration.setAllowedHeaders(List.of("Cache-Control", "Content-Type", "Authorization", "ClubId"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(true);

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
