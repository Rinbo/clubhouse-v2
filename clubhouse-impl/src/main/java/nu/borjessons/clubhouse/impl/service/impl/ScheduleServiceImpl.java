package nu.borjessons.clubhouse.impl.service.impl;

import java.util.function.BiFunction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Schedule;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.dto.ScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ScheduleRequest;
import nu.borjessons.clubhouse.impl.repository.ScheduleRepository;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.service.ScheduleService;

@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService {
  private final ScheduleRepository scheduleRepository;
  private final TeamRepository teamRepository;
  private final BiFunction<ScheduleRequest, Team, Schedule> scheduleInputConverter;

  @Transactional
  @Override
  public ScheduleRecord getSchedule(String teamId) {
    Schedule schedule = scheduleRepository.findByTeamId(teamId).orElseThrow();
    return new ScheduleRecord(schedule);
  }

  @Override
  public ScheduleRecord updateSchedule(String teamId, ScheduleRecord scheduleRecord) {
    return null;
  }

  @Transactional
  @Override
  public ScheduleRecord createSchedule(String teamId, ScheduleRequest scheduleRequest) {
    Team team = teamRepository.findByTeamId(teamId).orElseThrow();
    return new ScheduleRecord(scheduleRepository.save(scheduleInputConverter.apply(scheduleRequest, team)));
  }
}
