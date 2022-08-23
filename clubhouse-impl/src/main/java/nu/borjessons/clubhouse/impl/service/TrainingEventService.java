package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;

public interface TrainingEventService {
  TrainingEventRecord create(String clubId, String teamId, TrainingEventRequestModel trainingEventRequestModel);

  TrainingEventRecord delete(long trainingEventId);

  TrainingEventRecord get(long trainingEventId);

  List<TrainingEventRecord> getAllForTeam(String teamId);

  TrainingEventRecord update(String clubId, long trainingEventId, TrainingEventRequestModel trainingEventRequestModel);
}
