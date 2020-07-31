package nu.borjessons.clubhouse.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class AbstractController {
	
	protected <T> T getOrThrow(Optional<T> option) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "That entity could not be found");
	}
	
	protected <T> T getOrThrow(Optional<T> option, String identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity could not be found %s", identifier));
	}
	
	protected <T> T getOrThrow(Optional<T> option, Long identifier) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("That entity could not be found %d", identifier));
	}
}
