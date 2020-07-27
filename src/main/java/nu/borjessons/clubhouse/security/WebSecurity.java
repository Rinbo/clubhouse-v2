package nu.borjessons.clubhouse.security;

import static nu.borjessons.clubhouse.security.SecurityConstants.CLUB_REGISTRATION_URL;
import static nu.borjessons.clubhouse.security.SecurityConstants.H2_CONSOLE;
import static nu.borjessons.clubhouse.security.SecurityConstants.USER_REGISTRATION_URL;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserService userService;
	private final JWTUtil jwtUtil;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
		
		http.authorizeRequests()
	        .antMatchers(HttpMethod.POST, USER_REGISTRATION_URL, CLUB_REGISTRATION_URL)
	        .permitAll()
	        .antMatchers(H2_CONSOLE)
	        .permitAll()
	        .anyRequest().authenticated()
	        .and()
	        .addFilter(new AuthenticationFilter(authenticationManager(), jwtUtil))
	        .addFilter(new AuthorizationFilter(authenticationManager(), userService, jwtUtil))
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.headers().frameOptions().disable();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("Cache-Control", "Content-Type"));
		configuration.setAllowCredentials(true);
		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}

}
