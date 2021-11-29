package nu.borjessons.clubhouse.impl.service;

import nu.borjessons.clubhouse.impl.dto.ScheduleRecord;

public interface ScheduleService {
  ScheduleRecord getSchedule(String teamId);

  ScheduleRecord updateSchedule(String teamId, ScheduleRecord scheduleRecord);
}
