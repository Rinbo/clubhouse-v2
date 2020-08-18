package nu.borjessons.clubhouse.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.repository.ClubRepository;
import nu.borjessons.clubhouse.service.ClubService;
import nu.borjessons.clubhouse.service.ClubhouseAbstractService;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl extends ClubhouseAbstractService implements ClubService {
	
	private final ClubRepository clubRepository;

	@Override
	public Club getClubByClubId(String clubId) {
		return getOptional(clubRepository.findByClubId(clubId), Club.class.getSimpleName(), clubId);
	}
}
