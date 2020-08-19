package nu.borjessons.clubhouse.service;

import java.util.Optional;

import javax.persistence.NoResultException;

public abstract class ClubhouseAbstractService {
	
	protected <T> T getOptional(Optional<T> option, Class<T> className, String identifier) {
		if (option.isPresent()) return option.get();
		throw new NoResultException(String.format("That entity %s with id %s could not be found", className.getSimpleName(), identifier));
	}

	protected <T> T getOptional(Optional<T> option, Class<T> className, Long identifier) {
		if (option.isPresent()) return option.get();
		throw new NoResultException(String.format("That entity %s with id %d could not be found", className.getSimpleName(), identifier));
	}

}
