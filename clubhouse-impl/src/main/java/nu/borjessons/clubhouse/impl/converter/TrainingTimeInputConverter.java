package nu.borjessons.clubhouse.impl.converter;

import java.util.Objects;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingTimeRequest;

@Component
public final class TrainingTimeInputConverter implements Function<TrainingTimeRequest, TrainingTime> {
  @Override
  public TrainingTime apply(TrainingTimeRequest trainingTimeRequest) {
    Objects.requireNonNull(trainingTimeRequest, "trainingTimeRequest must not be null");

    TrainingTime trainingTime = new TrainingTime();
    trainingTime.setDayOfWeek(trainingTimeRequest.getDayOfWeek());
    trainingTime.setLocation(trainingTimeRequest.getLocation());
    trainingTime.setStartTime(trainingTimeRequest.getStartTime());
    trainingTime.setEndTime(trainingTimeRequest.getEndTime());
    return trainingTime;
  }
}
