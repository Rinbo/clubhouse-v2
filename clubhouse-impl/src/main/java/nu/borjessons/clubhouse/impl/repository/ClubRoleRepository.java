package nu.borjessons.clubhouse.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRoleRepository extends JpaRepository<ClubRole, Long> {
}
