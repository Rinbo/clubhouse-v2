package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record ScheduleRecord(LocalDate localDate, List<ClubScheduleRecord> clubScheduleRecords) {
  public ScheduleRecord {
    Objects.requireNonNull(localDate, "localDate must not be null");
    Objects.requireNonNull(clubScheduleRecords, "clubScheduleRecords must not be null");
  }
}
