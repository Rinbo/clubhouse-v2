package nu.borjessons.clubhouse.impl.service;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;

public interface TeamPostService {
  TeamPostRecord createPost(User user, String clubId, String Team, TeamPostRequest teamPostRequest);
}
