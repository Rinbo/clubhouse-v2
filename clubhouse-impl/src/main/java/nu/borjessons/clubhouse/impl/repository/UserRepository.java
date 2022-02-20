package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  @Query(value = "SELECT * FROM users WHERE user_id = ?1 and managed_account", nativeQuery = true)
  Optional<User> findManagedUserById(UserId userId);

  Optional<User> findByUserId(UserId userId);

  @Modifying
  @Query(value = "UPDATE announcement SET author_id = NULL WHERE author_id IN (SELECT id FROM club_user WHERE user_id = ?1)", nativeQuery = true)
  void removeUserAnnouncementReferences(long id);
}
