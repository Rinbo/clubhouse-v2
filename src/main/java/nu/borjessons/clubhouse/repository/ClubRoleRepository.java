package nu.borjessons.clubhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.data.ClubRole;

@Repository	
public interface ClubRoleRepository extends JpaRepository<ClubRole, Long> {

}
