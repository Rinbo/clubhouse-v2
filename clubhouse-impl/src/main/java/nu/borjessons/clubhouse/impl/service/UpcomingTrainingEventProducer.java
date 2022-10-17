package nu.borjessons.clubhouse.impl.service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.UpcomingTrainingEvent;
import nu.borjessons.clubhouse.impl.util.Validate;

@Component
public class UpcomingTrainingEventProducer {
  private static String getTrainingTimeId(TrainingTime trainingTime) {
    if (trainingTime == null) return null;
    return trainingTime.getTrainingTimeId();
  }

  private final Clock clock;
  private final Duration threshold;

  public UpcomingTrainingEventProducer(@Value("${upcoming-training-time-threshold:PT5H}") Duration threshold, Clock clock) {
    Validate.notNull(threshold, "threshold");
    Validate.notNull(clock, "clock");

    this.clock = clock;
    this.threshold = threshold;
  }

  public List<UpcomingTrainingEvent> getUpcomingTraining(List<Team> teams) {
    return teams.stream().flatMap(this::findTrainingWithinThreshold).toList();
  }

  public List<UpcomingTrainingEvent> getUpcomingTrainingEvents(List<TrainingEvent> trainingEvents) {
    return trainingEvents.stream().filter(this::isTrainingEventWithinThreshold).map(this::createUpcomingTrainingEvent).toList();
  }

  private UpcomingTrainingEvent createUpcomingTrainingEvent(Team team, TrainingTime trainingTime) {
    LocalTime startTime = trainingTime.getStartTime();
    return new UpcomingTrainingEvent(
        team.getName(),
        team.getTeamId(),
        LocalDateTime.of(LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC), startTime),
        Duration.between(startTime, trainingTime.getEndTime()),
        trainingTime.getLocation(),
        getTrainingTimeId(trainingTime));
  }

  private UpcomingTrainingEvent createUpcomingTrainingEvent(TrainingEvent trainingEvent) {
    Team team = trainingEvent.getTeam();
    return new UpcomingTrainingEvent(
        team.getName(),
        team.getTeamId(),
        trainingEvent.getLocalDateTime(),
        trainingEvent.getDuration(),
        trainingEvent.getLocation(),
        getTrainingTimeId(trainingEvent.getTrainingTime()));
  }

  private Stream<UpcomingTrainingEvent> findTrainingWithinThreshold(Team team) {
    return team.getTrainingTimes()
        .stream()
        .filter(this::isDayOfWeek)
        .filter(this::isNotRecentlyActivated)
        .filter(this::isTrainingTimeWithinThreshold)
        .map(trainingTime -> createUpcomingTrainingEvent(team, trainingTime));
  }

  private boolean isDayOfWeek(TrainingTime trainingTime) {
    return LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC).getDayOfWeek() == trainingTime.getDayOfWeek();
  }

  private boolean isNotRecentlyActivated(TrainingTime trainingTime) {
    LocalDateTime lastActivated = trainingTime.getLastActivated();
    if (lastActivated == null) return true;
    return clock.instant().minus(threshold).isAfter(lastActivated.toInstant(ZoneOffset.UTC));
  }

  private boolean isTrainingEventWithinThreshold(TrainingEvent trainingEvent) {
    LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    LocalTime startTime = trainingEvent.getLocalDateTime().toLocalTime();
    LocalDateTime from = LocalDateTime.of(now.toLocalDate(), startTime).minus(threshold);
    LocalDateTime to = LocalDateTime.of(now.toLocalDate(), startTime).plus(threshold);
    return now.isAfter(from) && now.isBefore(to);
  }

  private boolean isTrainingTimeWithinThreshold(TrainingTime trainingTime) {
    LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    LocalDateTime from = LocalDateTime.of(now.toLocalDate(), trainingTime.getStartTime()).minus(threshold);
    LocalDateTime to = LocalDateTime.of(now.toLocalDate(), trainingTime.getEndTime()).plus(threshold);
    return (now.isAfter(from) && now.isBefore(to));
  }
}