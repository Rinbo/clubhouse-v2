package nu.borjessons.clubhouse.impl.security;

import static nu.borjessons.clubhouse.impl.security.SecurityUtil.CLUB_REGISTRATION_URL;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.FAMILY_REGISTRATION_URL;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.H2_CONSOLE;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.PUBLIC_CLUB_URLS;
import static nu.borjessons.clubhouse.impl.security.SecurityUtil.USER_REGISTRATION_URL;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.security.provider.TopLevelAuthProvider;
import nu.borjessons.clubhouse.impl.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
  private final ClubUserRepository clubUserRepository;
  private final JWTUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final TopLevelAuthProvider topLevelAuthProvider;
  private final UserService userService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(topLevelAuthProvider)
        .userDetailsService(userService)
        .passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable();
    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, USER_REGISTRATION_URL, CLUB_REGISTRATION_URL, FAMILY_REGISTRATION_URL).permitAll()
        .antMatchers(HttpMethod.GET, PUBLIC_CLUB_URLS).permitAll()
        .antMatchers(H2_CONSOLE).permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilterAt(new AuthenticationFilter(authenticationManager(), jwtUtil, userService), BasicAuthenticationFilter.class)
        .addFilterAfter(new AuthorizationFilter(jwtUtil, userService), BasicAuthenticationFilter.class)
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

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
    configuration.addAllowedOriginPattern("*");
    configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
    configuration.setAllowedHeaders(List.of("Cache-Control", "Content-Type", "Authorization"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(true);

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
