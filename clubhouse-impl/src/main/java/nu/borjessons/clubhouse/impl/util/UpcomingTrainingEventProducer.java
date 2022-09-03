package nu.borjessons.clubhouse.impl.util;

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
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.UpcomingTrainingEvent;

@Component
public class UpcomingTrainingEventProducer {
  private final Clock clock;
  private final Duration threshold;

  public UpcomingTrainingEventProducer(@Value("${upcoming-training-time-threshold:PT3H}") Duration threshold, Clock clock) {
    this.clock = clock;
    this.threshold = threshold;
  }

  public List<UpcomingTrainingEvent> createUpcomingTrainingEvents(List<Team> teams) {
    return teams.stream().flatMap(this::findWithinThreshold).toList();
  }

  private UpcomingTrainingEvent createUpcomingTrainingEvent(Team team, TrainingTime trainingTime) {
    LocalTime startTime = trainingTime.getStartTime();
    return new UpcomingTrainingEvent(
        team.getName(),
        team.getTeamId(),
        LocalDateTime.of(LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC), startTime),
        Duration.between(startTime, trainingTime.getEndTime()),
        trainingTime.getLocation(),
        trainingTime.getTrainingTimeId());
  }

  private Stream<UpcomingTrainingEvent> findWithinThreshold(Team team) {
    return team.getTrainingTimes()
        .stream()
        .filter(this::isNotRecentlyActivated)
        .filter(this::isTrainingTimeWithinThreshold)
        .map(trainingTime -> createUpcomingTrainingEvent(team, trainingTime));
  }

  private boolean isNotRecentlyActivated(TrainingTime trainingTime) {
    LocalDateTime lastActivated = trainingTime.getLastActivated();
    if (lastActivated == null) return true;
    return clock.instant().minus(threshold).isAfter(lastActivated.toInstant(ZoneOffset.UTC));
  }

  private boolean isTrainingTimeWithinThreshold(TrainingTime trainingTime) {
    LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    LocalDateTime from = LocalDateTime.of(now.toLocalDate(), trainingTime.getStartTime()).minus(threshold);
    LocalDateTime to = LocalDateTime.of(now.toLocalDate(), trainingTime.getEndTime()).plus(threshold);
    return (now.isAfter(from) && now.isBefore(to));
  }
}