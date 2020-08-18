package nu.borjessons.clubhouse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.data.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

	Optional<Team> findByTeamId(String teamId);
}
