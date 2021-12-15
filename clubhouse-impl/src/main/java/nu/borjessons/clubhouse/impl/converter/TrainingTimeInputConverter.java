package nu.borjessons.clubhouse.impl.converter;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingTimeRequest;

@Component
public final class TrainingTimeInputConverter implements BiFunction<TrainingTimeRequest, Team, TrainingTime> {
  private static TrainingTime createTrainingTime(TrainingTimeRequest trainingTimeRequest) {
    TrainingTime trainingTime = new TrainingTime();
    trainingTime.setDayOfWeek(DayOfWeek.of(trainingTimeRequest.getDayOfWeek()));
    trainingTime.setLocation(trainingTimeRequest.getLocation());
    trainingTime.setStartTime(trainingTimeRequest.getStartTime());
    trainingTime.setEndTime(trainingTimeRequest.getEndTime());
    return trainingTime;
  }

  @Override
  public TrainingTime apply(TrainingTimeRequest trainingTimeRequest, Team team) {
    Objects.requireNonNull(trainingTimeRequest, "trainingTimeRequest must not be null");
    Objects.requireNonNull(team, "team must not be null");

    return null;
  }
}
