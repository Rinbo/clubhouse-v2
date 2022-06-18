package nu.borjessons.clubhouse.impl.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import nu.borjessons.clubhouse.impl.data.TeamPostComment;

public interface TeamPostCommentRepository extends PagingAndSortingRepository<TeamPostComment, Long> {
  
}
