package nu.borjessons.clubhouse.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import nu.borjessons.clubhouse.data.User;

public abstract class ClubhouseAbstractService {
	
	protected <T> T getOrThrow(Optional<T> option) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "That entity could not be found");
	}
	
	protected <T> T getOrThrow(Optional<T> option, String identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity could not be found %s", identifier));
	}
	
	protected <T> T getOrThrow(Optional<T> option, String className, String identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity %s with id %s could not be found", className, identifier));
	}
	
	protected <T> T getOrThrow(Optional<T> option, Long identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity could not be found %d", identifier));
	}
	
	protected <T> T getOrThrow(Optional<T> option, String className, Long identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity %s with id %d could not be found", className, identifier));
	}
	
	protected User getOrThrowUNFE(Optional<User> maybeUser, String email) {
		if (maybeUser.isPresent()) return maybeUser.get();
		throw new UsernameNotFoundException(String.format("User %s could not be found", email));
	}
}
