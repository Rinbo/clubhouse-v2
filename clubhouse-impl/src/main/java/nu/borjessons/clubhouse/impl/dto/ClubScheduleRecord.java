package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record ClubScheduleRecord(String clubId, String clubName, LocalDate localDate, List<TeamScheduleRecord> teamScheduleRecords) {
  public ClubScheduleRecord {
    Objects.requireNonNull(clubId, "clubId must not be null");
    Objects.requireNonNull(clubName, "clubName must not be null");
    Objects.requireNonNull(localDate, "localDate must not be null");
    Objects.requireNonNull(teamScheduleRecords, "teamScheduleRecords must not be null");
  }
}
