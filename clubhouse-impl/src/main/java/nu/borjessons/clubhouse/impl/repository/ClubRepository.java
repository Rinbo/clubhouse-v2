package nu.borjessons.clubhouse.impl.repository;

import nu.borjessons.clubhouse.impl.data.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
  Optional<Club> findByClubId(String clubId);

  Club findByName(String name);
}
