package nu.borjessons.clubhouse.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.service.UserService;

public class AuthorizationFilter extends BasicAuthenticationFilter {
	
	private UserService userService;
	private JWTUtil jwtUtil;

	public AuthorizationFilter(AuthenticationManager authManager, UserService userService, JWTUtil jwtUtil) {
		super(authManager);
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		String token = req.getHeader(SecurityConstants.AUTHORIZATION);
		
		if(token == null) {
            chain.doFilter(req, res);
            return;
        }
		
		UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}
	
	 private UsernamePasswordAuthenticationToken getAuthentication(String token) {
	        if(token != null) {

	        	if (!jwtUtil.validateToken(token)) 	return null;
	        	
	        	Claims claims = jwtUtil.getAllClaimsFromToken(token);
				String email = claims.getSubject();

				if (email == null) return null;

				User user = userService.getUserByEmail(email);
				
				return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

	        }
	        return null;
	    }

}
