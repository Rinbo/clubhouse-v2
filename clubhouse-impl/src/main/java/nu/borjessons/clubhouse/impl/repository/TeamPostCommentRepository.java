package nu.borjessons.clubhouse.impl.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.TeamPostComment;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;

public interface TeamPostCommentRepository extends PagingAndSortingRepository<TeamPostComment, Long> {
  @Query("select t from TeamPostComment t where t.id = ?1 and t.clubUser = ?2")
  Optional<TeamPostComment> findByIdAndClubUser(long id, ClubUser clubUser);

  @Query("select t from TeamPostComment t where t.teamPost.id in (select tp.id from TeamPost tp where tp.teamPostId = ?1)")
  Collection<TeamPostComment> findByTeamPostId(TeamPostId teamPostId, PageRequest pageRequest);

  @Query(value = "SELECT COUNT(*) FROM team_post_comment WHERE team_post_id IN (SELECT id FROM team_post WHERE team_post.team_post_id = ?1)", nativeQuery = true)
  int countByTeamPostId(TeamPostId teamPostId);
}
