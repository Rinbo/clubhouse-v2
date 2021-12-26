package nu.borjessons.clubhouse.impl.dto;

import java.util.Objects;

public record TeamScheduleRecord(String teamName, TrainingTimeRecord trainingTimeRecord) {
  public TeamScheduleRecord {
    Objects.requireNonNull(teamName, "teamName must not be null");
    Objects.requireNonNull(trainingTimeRecord, "trainingTimeRecord must not be null");
  }
}
