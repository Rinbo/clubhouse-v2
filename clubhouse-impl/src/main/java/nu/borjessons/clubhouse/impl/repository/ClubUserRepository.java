package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.ClubUser;

@Repository
public interface ClubUserRepository extends JpaRepository<ClubUser, Long> {
  @Query(nativeQuery = true, value = "SELECT * FROM club_user WHERE user_id = ?1 AND club_id IN (SELECT id from club where club_id = ?2)")
  Optional<ClubUser> findByUserIdAndClubStringId(long userId, String clubId);
}
