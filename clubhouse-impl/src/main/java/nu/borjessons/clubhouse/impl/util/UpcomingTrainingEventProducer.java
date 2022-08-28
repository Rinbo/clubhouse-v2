package nu.borjessons.clubhouse.impl.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.UpcomingTrainingEvent;

@Component
public class UpcomingTrainingEventProducer {
  private final Duration threshold;

  public UpcomingTrainingEventProducer(@Value("${upcoming-training-time-threshold:PT3H}") Duration threshold) {
    this.threshold = threshold;
  }

  public List<UpcomingTrainingEvent> createUpcomingTrainingEvents(List<Team> teams) {
    return teams.stream().flatMap(this::findWithinThreshold).toList();
  }

  private UpcomingTrainingEvent createUpcomingTrainingEvent(Team team, TrainingTime trainingTime) {
    LocalTime startTime = trainingTime.getStartTime();
    return new UpcomingTrainingEvent(team.getName(), team.getTeamId(), LocalDateTime.of(
        LocalDate.now(), startTime), Duration.between(startTime, trainingTime.getEndTime()),
        trainingTime.getLocation());
  }

  private Stream<UpcomingTrainingEvent> findWithinThreshold(Team team) {
    return team.getTrainingTimes()
        .stream()
        .filter(this::isTrainingTimeWithinThreshold)
        .map(trainingTime -> createUpcomingTrainingEvent(team, trainingTime));
  }

  private boolean isTrainingTimeWithinThreshold(TrainingTime trainingTime) {
    Instant now = Instant.now();
    return (now.isAfter(Instant.from(trainingTime.getStartTime().minus(threshold))) && now.isBefore(Instant.from(trainingTime.getStartTime().plus(threshold))));
  }
}
