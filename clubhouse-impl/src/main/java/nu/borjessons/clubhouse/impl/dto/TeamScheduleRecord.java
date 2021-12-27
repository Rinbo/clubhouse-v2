package nu.borjessons.clubhouse.impl.dto;

import java.util.Objects;

public record TeamScheduleRecord(String teamId, String teamName, TrainingTimeRecord trainingTimeRecord) {
  public TeamScheduleRecord {
    Objects.requireNonNull(teamId, "teamId must not be null");
    Objects.requireNonNull(teamName, "teamName must not be null");
    Objects.requireNonNull(trainingTimeRecord, "trainingTimeRecord must not be null");
  }
}
