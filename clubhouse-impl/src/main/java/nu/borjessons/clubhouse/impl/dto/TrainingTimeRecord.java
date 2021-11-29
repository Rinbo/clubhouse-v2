package nu.borjessons.clubhouse.impl.dto;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Objects;

import nu.borjessons.clubhouse.impl.data.TrainingTime;

public record TrainingTimeRecord(Duration duration, DayOfWeek dayOfWeek, String location) {
  public TrainingTimeRecord {
    Objects.requireNonNull(duration, "duration must not be null");
    Objects.requireNonNull(dayOfWeek, "dayOfWeek must not be null");
    Objects.requireNonNull(location, "location must not be null");
  }

  public TrainingTimeRecord(TrainingTime trainingTime) {
    this(trainingTime.getDuration(), trainingTime.getDayOfWeek(), trainingTime.getLocation());
  }
}
