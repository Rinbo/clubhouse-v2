package nu.borjessons.clubhouse.impl.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.TeamPost;

@Repository
public interface TeamPostRepository extends PagingAndSortingRepository<TeamPost, Long> {
}
