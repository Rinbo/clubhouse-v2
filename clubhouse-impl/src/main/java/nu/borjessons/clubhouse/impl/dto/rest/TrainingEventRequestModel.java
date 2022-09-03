package nu.borjessons.clubhouse.impl.dto.rest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.util.Validate;

public record TrainingEventRequestModel(
    LocalDateTime localDateTime,
    Duration duration,
    String notes,
    List<UserId> presentLeaders,
    List<UserId> presentMembers,
    String trainingTimeId) {

  public TrainingEventRequestModel {
    Validate.notNull(localDateTime, "localDateTime");
    Validate.notNull(duration, "duration");
  }
}
