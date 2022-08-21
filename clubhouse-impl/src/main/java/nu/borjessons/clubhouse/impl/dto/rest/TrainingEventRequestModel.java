package nu.borjessons.clubhouse.impl.dto.rest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.util.Validate;

public record TrainingEventRequestModel(
    LocalDateTime dateTime,
    Duration duration,
    String notes,
    List<UserId> presentLeaders,
    List<UserId> presentMembers) {

  public TrainingEventRequestModel {
    Validate.notNull(dateTime, "localDateTime");
    Validate.notNull(duration, "duration");
  }
}
