package nu.borjessons.clubhouse.impl.service;

import nu.borjessons.clubhouse.impl.dto.ScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ScheduleRequest;

public interface ScheduleService {
  ScheduleRecord getSchedule(String teamId);

  ScheduleRecord updateSchedule(String teamId, ScheduleRecord scheduleRecord);

  ScheduleRecord createSchedule(String teamId, ScheduleRequest scheduleRequest);
}
