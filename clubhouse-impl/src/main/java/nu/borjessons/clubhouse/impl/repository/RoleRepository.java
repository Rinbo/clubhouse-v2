package nu.borjessons.clubhouse.impl.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

  @Query(nativeQuery = true, value = "SELECT * FROM role WHERE name in (?1)")
  Set<RoleEntity> findByRoleNames(Set<String> myRoles);
}
