package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.UpcomingTrainingEvent;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;

public interface TrainingEventService {
  TrainingEventRecord create(String clubId, String teamId, TrainingEventRequestModel trainingEventRequestModel);

  void delete(long trainingEventId);

  TrainingEventRecord get(long trainingEventId);

  List<TrainingEventRecord> get(String teamId, PageRequest pageRequest);

  List<UpcomingTrainingEvent> getUpcomingTrainingEvents(long userId, String clubId);

  TrainingEventRecord update(String clubId, long trainingEventId, TrainingEventRequestModel trainingEventRequestModel);
}
