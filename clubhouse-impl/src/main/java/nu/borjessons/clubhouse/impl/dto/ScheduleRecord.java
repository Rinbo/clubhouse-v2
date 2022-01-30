package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDate;
import java.util.List;

import nu.borjessons.clubhouse.impl.util.Validate;

public record ScheduleRecord(LocalDate localDate, List<ClubScheduleRecord> clubScheduleRecords) {
  public ScheduleRecord {
    Validate.notNull(localDate, "localDate");
    Validate.notNull(clubScheduleRecords, "clubScheduleRecords");
  }
}
