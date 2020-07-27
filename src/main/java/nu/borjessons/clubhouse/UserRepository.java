package nu.borjessons.clubhouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.data.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByEmail(String email);

}
