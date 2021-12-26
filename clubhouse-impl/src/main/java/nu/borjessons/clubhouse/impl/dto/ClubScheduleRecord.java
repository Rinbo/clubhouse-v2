package nu.borjessons.clubhouse.impl.dto;

import java.util.List;
import java.util.Objects;

public record ClubScheduleRecord(String clubName, List<TeamScheduleRecord> teamScheduleRecords) {
  public ClubScheduleRecord {
    Objects.requireNonNull(clubName, "clubName must not be null");
    Objects.requireNonNull(teamScheduleRecords, "teamScheduleRecords must not be null");
  }
}
