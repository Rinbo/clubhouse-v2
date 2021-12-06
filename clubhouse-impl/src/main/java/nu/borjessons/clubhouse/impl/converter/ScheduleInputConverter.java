package nu.borjessons.clubhouse.impl.converter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import nu.borjessons.clubhouse.impl.data.Schedule;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.rest.ScheduleRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingTimeRequest;

@Component
public final class ScheduleInputConverter implements BiFunction<ScheduleRequest, Team, Schedule> {
  private static List<TrainingTime> getTrainingTimes(List<TrainingTimeRequest> trainingTimes) {
    return trainingTimes.stream().map(ScheduleInputConverter::createTrainingTime).toList();
  }

  private static TrainingTime createTrainingTime(TrainingTimeRequest trainingTimeRequest) {
    TrainingTime trainingTime = new TrainingTime();
    trainingTime.setDuration(trainingTimeRequest.getDuration());
    trainingTime.setDayOfWeek(DayOfWeek.of(trainingTimeRequest.getDayOfWeek()));
    trainingTime.setLocation(trainingTimeRequest.getLocation());
    return trainingTime;
  }

  @Override
  public Schedule apply(ScheduleRequest scheduleRequest, Team team) {
    Objects.requireNonNull(scheduleRequest, "scheduleRequest must not be null");
    Objects.requireNonNull(team, "team must not be null");

    List<TrainingTime> trainingTimes = getTrainingTimes(scheduleRequest.getTrainingTimes());

    Schedule schedule = new Schedule();
    schedule.addTeam(team);
    schedule.setPeriodStart(scheduleRequest.getPeriodStart());
    schedule.setPeriodEnd(scheduleRequest.getPeriodEnd());
    trainingTimes.forEach(schedule::addTrainingTime);

    return schedule;
  }
}
