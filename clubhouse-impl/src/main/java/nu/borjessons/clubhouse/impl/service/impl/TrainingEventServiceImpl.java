package nu.borjessons.clubhouse.impl.service.impl;

import java.util.List;

import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;
import nu.borjessons.clubhouse.impl.service.TrainingEventService;

public class TrainingEventServiceImpl implements TrainingEventService {
  @Override
  public TrainingEventRecord create(String teamId, TrainingEventRequestModel trainingEventRequestModel) {
    return null;
  }

  @Override
  public TrainingEventRecord delete(long trainingEventId) {
    return null;
  }

  @Override
  public TrainingEventRecord get(long trainingEventId) {
    return null;
  }

  @Override
  public List<TrainingEventRecord> getAllForTeam(String teamId) {
    return null;
  }

  @Override
  public TrainingEventRecord update(String teamId, long trainingEventId, TrainingEventRequestModel trainingEventRequestModel) {
    return null;
  }
}
