package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import nu.borjessons.clubhouse.impl.data.Schedule;

public record ScheduleRecord(LocalDate periodStart,
                             LocalDate periodEnd,
                             List<TrainingTimeRecord> trainingTimes,
                             String teamId,
                             String teamName) {
  public ScheduleRecord {
    Objects.requireNonNull(periodStart, "periodStart must not be null");
    Objects.requireNonNull(periodEnd, "periodEnd must not be null");
    Objects.requireNonNull(trainingTimes, "trainingTimes must not be null");
    Objects.requireNonNull(teamId, "teamId must not be null");
    Objects.requireNonNull(teamName, "teamName must not be null");
  }

  public ScheduleRecord(Schedule schedule) {
    this(schedule.getPeriodStart(),
        schedule.getPeriodEnd(),
        schedule.getTrainingTimes()
            .stream()
            .map(TrainingTimeRecord::new)
            .collect(Collectors.toList()),
        schedule.getTeam().getTeamId(), schedule.getTeam().getName());
  }
}
