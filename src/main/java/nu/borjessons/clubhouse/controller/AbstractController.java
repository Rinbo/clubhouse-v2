package nu.borjessons.clubhouse.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import nu.borjessons.clubhouse.data.User;

public abstract class AbstractController {
	
	protected User getPrincipal() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	protected <T> T getOptional(Optional<T> option) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "That entity could not be found");
	}
	
	protected <T> T getOptional(Optional<T> option, String identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity could not be found %s", identifier));
	}
	
	protected <T> T getOptional(Optional<T> option, Class<T> className, String identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity %s with id %s could not be found", className.getSimpleName(), identifier));
	}
	
	protected <T> T getOptional(Optional<T> option, Long identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity could not be found %d", identifier));
	}
	
	protected <T> T getOptional(Optional<T> option, Class<T> className, Long identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity %s with id %d could not be found", className.getSimpleName(), identifier));
	}
}
