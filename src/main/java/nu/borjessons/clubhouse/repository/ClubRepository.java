package nu.borjessons.clubhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.data.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

}
