package nu.borjessons.clubhouse.impl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.TeamPost;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;

@Repository
public interface TeamPostRepository extends PagingAndSortingRepository<TeamPost, Long> {
  //@Query(value = "SELECT * FROM team_post WHERE team_id IN (SELECT id FROM team where team_id = ?1)", nativeQuery = true)
  @Query("select tp from TeamPost tp where tp.team.id in (select t.id from Team t where t.teamId = ?1)")
  List<TeamPost> getTeamPostByTeamId(String teamId, Pageable pageable);

  Optional<TeamPost> findByTeamPostId(TeamPostId teamPostId);

  @Query("select t from TeamPost t where t.teamPostId = ?1 and t.clubUser = ?2")
  Optional<TeamPost> findByTeamPostIdAndClubUser(TeamPostId teamPostId, ClubUser clubUser);
}
