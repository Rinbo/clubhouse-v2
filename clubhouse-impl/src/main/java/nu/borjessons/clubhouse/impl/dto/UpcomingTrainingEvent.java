package nu.borjessons.clubhouse.impl.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import nu.borjessons.clubhouse.impl.util.Validate;

public record UpcomingTrainingEvent(String teamName, String teamId, LocalDateTime localDateTime, Duration duration, String location, String trainingTimeId) {
  public UpcomingTrainingEvent {
    Validate.notEmpty(teamName, "teamName");
    Validate.notEmpty(teamId, "teamId");
    Validate.notNull(localDateTime, "localDateTime");
    Validate.notNull(duration, "duration");
  }
}
