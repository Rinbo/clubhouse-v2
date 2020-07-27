package nu.borjessons.clubhouse.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import nu.borjessons.clubhouse.data.User;

public interface UserService extends UserDetailsService {

	User getUserByEmail(String username);

}

