package nu.borjessons.clubhouse.impl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.key.UserId;

@Repository
public interface ClubUserRepository extends JpaRepository<ClubUser, Long> {
  @Query("select count(c) from ClubUser c where c.club.id = ?1")
  int countByClubId(String clubId);

  @Query(nativeQuery = true, value = "SELECT * FROM club_user WHERE user_id IN (SELECT id from users where user_id = ?1)")
  List<ClubUser> findAllByUserId(String userId);

  @Query(nativeQuery = true, value = "SELECT * FROM club_user WHERE club_id IN (SELECT id from club where club_id = ?1)")
  List<ClubUser> findByClubId(String clubId);

  @Query(nativeQuery = true, value = "SELECT * FROM club_user WHERE user_id IN (SELECT id from users where user_id=?2) AND club_id IN (SELECT id from club where club_id = ?1)")
  Optional<ClubUser> findByClubIdAndUserId(String clubId, String userId);

  @Query(nativeQuery = true, value = "SELECT * FROM club_user WHERE user_id = ?2 AND club_id IN (SELECT id from club where club_id = ?1)")
  Optional<ClubUser> findByClubIdAndUserId(String clubId, long userId);

  @Query("select cu from ClubUser cu where cu.club.clubId = ?1 and cu.user.id in (select u.id from User u where u.userId in (?2))")
  List<ClubUser> findByClubIdAndUserIds(String clubId, List<UserId> userIds);

  @Query(nativeQuery = true, value = "SELECT * FROM club_user WHERE user_id IN (SELECT id from users where email=?2) AND club_id IN (SELECT id from club where club_id = ?1)")
  Optional<ClubUser> findByClubIdAndUsername(String clubId, String username);

  @Modifying
  @Query(value = "UPDATE announcement SET author_id = NULL WHERE author_id = ?1", nativeQuery = true)
  void removeClubUserAnnouncementReferences(long id);
}
