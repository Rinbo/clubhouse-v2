package nu.borjessons.clubhouse.impl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
  Optional<Team> findByTeamId(String teamId);

  @Query(value = "SELECT * FROM team WHERE club_id IN (SELECT id FROM club WHERE club_id=?1)", nativeQuery = true)
  List<Team> findByClubId(String clubId);
}
