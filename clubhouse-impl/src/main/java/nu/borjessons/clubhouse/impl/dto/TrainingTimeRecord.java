package nu.borjessons.clubhouse.impl.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

import nu.borjessons.clubhouse.impl.data.TrainingTime;

public record TrainingTimeRecord(String trainingTimeId, DayOfWeek dayOfWeek, String location, LocalTime startTime, LocalTime endTime) {
  public static int localTimeComparator(TrainingTimeRecord first, TrainingTimeRecord second) {
    int weekdayCompare = Integer.compare(first.dayOfWeek.getValue(), second.dayOfWeek.getValue());
    if (weekdayCompare != 0) return weekdayCompare;
    return first.startTime().compareTo(second.startTime());
  }

  public TrainingTimeRecord {
    Objects.requireNonNull(trainingTimeId, "String trainingTimeId must not be null");
    Objects.requireNonNull(dayOfWeek, "dayOfWeek must not be null");
    Objects.requireNonNull(location, "location must not be null");
    Objects.requireNonNull(startTime, "startTime must not be null");
    Objects.requireNonNull(endTime, "endTime must not be null");
  }

  public TrainingTimeRecord(TrainingTime trainingTime) {
    this(trainingTime.getTrainingTimeId(), trainingTime.getDayOfWeek(), trainingTime.getLocation(), trainingTime.getStartTime(), trainingTime.getEndTime());
  }
}
