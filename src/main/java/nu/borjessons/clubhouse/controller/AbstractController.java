package nu.borjessons.clubhouse.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class AbstractController {
	
	protected <T> T getOrThrow(Optional<T> option) {
		if (option.isPresent()) return option.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "That entity could not be found");
	}
}
