package nu.borjessons.clubhouse.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.dto.ClubDTO;

@RequestMapping("/clubs")
@RequiredArgsConstructor
@RestController
public class ClubController extends AbstractController {
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/principal/active")
	public ClubDTO getActiveClub() {
		return new ClubDTO(getPrincipal().getActiveClub());
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/principal")
	public Set<ClubDTO> getClubs() {
		Set<Club> clubs = getPrincipal().getClubs();
		return clubs.stream().map(ClubDTO::new).collect(Collectors.toSet());
	}

}
