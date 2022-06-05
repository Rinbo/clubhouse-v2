package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;

import org.springframework.data.domain.PageRequest;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;

public interface TeamPostService {
  TeamPostRecord createPost(User user, String clubId, String teamId, TeamPostRequest teamPostRequest);

  Collection<TeamPostRecord> getPosts(String teamId, PageRequest of);

  TeamPostRecord toggleSticky(TeamPostId teamPostId);

  TeamPostRecord updatePost(User principal, String clubId, String teamId, TeamPostId teamPostId, TeamPostRequest teamPostRequest);
}
