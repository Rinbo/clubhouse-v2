package nu.borjessons.clubhouse.service.impl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.repository.ClubRepository;
import nu.borjessons.clubhouse.service.ClubService;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {
	
	private final ClubRepository clubRepository;

	@Override
	public Club getClubById(long clubId) {
		Optional<Club> maybeClub = clubRepository.findById(clubId);
		return getOrThrow(maybeClub, clubId);
	}

	private Club getOrThrow(Optional<Club> maybeClub, long clubId) {
		if (maybeClub.isPresent()) return maybeClub.get();
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Club with id %d does not exist", clubId));
	}

}
