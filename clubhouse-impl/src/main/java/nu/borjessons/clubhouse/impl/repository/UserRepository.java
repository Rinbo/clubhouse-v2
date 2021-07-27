package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  @Query(value = "SELECT * FROM users WHERE user_id = ?1 and managed_account", nativeQuery = true)
  Optional<User> findManagedUserById(String userId);

  Optional<User> findByUserId(String userId);
}
