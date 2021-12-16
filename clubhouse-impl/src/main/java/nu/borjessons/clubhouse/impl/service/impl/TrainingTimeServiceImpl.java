package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.repository.TrainingTimeRepository;
import nu.borjessons.clubhouse.impl.service.TrainingTimeService;

@RequiredArgsConstructor
@Service
public class TrainingTimeServiceImpl implements TrainingTimeService {
  private static void updateExistingTrainingTime(TrainingTime existing, TrainingTime trainingTime) {
    existing.setDayOfWeek(trainingTime.getDayOfWeek());
    existing.setLocation(trainingTime.getLocation());
    existing.setStartTime(trainingTime.getStartTime());
    existing.setEndTime(trainingTime.getEndTime());
  }

  private final TrainingTimeRepository trainingTimeRepository;
  private final TeamRepository teamRepository;

  @Override
  public Collection<TrainingTimeRecord> getTrainingTimes(String teamId) {
    Team team = teamRepository.findByTeamId(teamId).orElseThrow();
    return team.getTrainingTimes().stream().map(TrainingTimeRecord::new).toList();
  }

  @Override
  public TrainingTimeRecord createTrainingTime(String teamId, TrainingTime trainingTime) {
    Team team = teamRepository.findByTeamId(teamId).orElseThrow();
    team.addTrainingTime(trainingTime);
    return new TrainingTimeRecord(trainingTimeRepository.save(trainingTime));
  }

  @Override
  public TrainingTimeRecord updateTrainingTime(String teamId, TrainingTime trainingTime) {
    TrainingTime existing = trainingTimeRepository.findByTrainingTimeId().orElseThrow();
    updateExistingTrainingTime(existing, trainingTime);
    return new TrainingTimeRecord(trainingTimeRepository.save(existing));
  }
}
