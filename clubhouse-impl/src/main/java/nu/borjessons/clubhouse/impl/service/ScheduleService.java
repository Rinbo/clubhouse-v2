package nu.borjessons.clubhouse.impl.service;

import java.time.LocalDate;
import java.util.Collection;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ClubScheduleRecord;

public interface ScheduleService {
  Collection<ClubScheduleRecord> getClubSchedule(String clubId, LocalDate startDate, LocalDate endDate);

  Collection<ClubScheduleRecord> getUserClubSchedule(UserId userId, String clubId, LocalDate startDate, LocalDate endDate);

  Collection<ClubScheduleRecord> getLeaderClubSchedule(UserId userId, String clubId, LocalDate startDate, LocalDate endDate);
}
