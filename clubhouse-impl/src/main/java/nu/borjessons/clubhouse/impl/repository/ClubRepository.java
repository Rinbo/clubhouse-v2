package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
  Optional<Club> findByClubId(String clubId);

  Optional<Club> findByPath(String path);
}
