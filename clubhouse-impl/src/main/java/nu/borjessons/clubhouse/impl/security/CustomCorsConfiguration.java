package nu.borjessons.clubhouse.impl.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Component
public class CustomCorsConfiguration implements CorsConfigurationSource {
  @Override
  public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
    CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
    configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowCredentials(true);
    return configuration;
  }
}
