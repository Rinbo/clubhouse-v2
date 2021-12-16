package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;

import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;

public interface TrainingTimeService {
  Collection<TrainingTimeRecord> getTrainingTimes(String teamId);

  TrainingTimeRecord createTrainingTime(String teamId, TrainingTime trainingTime);

  TrainingTimeRecord updateTrainingTime(String trainingTimeId, TrainingTime trainingTime);

  void deleteTrainingTime(String trainingTimeId);
}
