package nu.borjessons.clubhouse.impl.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;
import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.repository.TrainingEventRepository;
import nu.borjessons.clubhouse.impl.service.TrainingEventService;
import nu.borjessons.clubhouse.impl.util.AppUtils;

@Service
@RequiredArgsConstructor
public class TrainingEventServiceImpl implements TrainingEventService {
  private final ClubUserRepository clubUserRepository;
  private final TeamRepository teamRepository;
  private final TrainingEventRepository trainingEventRepository;

  @Override
  @Transactional
  public TrainingEventRecord create(String clubId, String teamId, TrainingEventRequestModel trainingEventRequestModel) {
    Team team = teamRepository.findByTeamId(teamId).orElseThrow(AppUtils.createNotFoundExceptionSupplier("Team not found: " + teamId));

    // TODO verify leader has has role leader
    List<ClubUser> presentLeaders = clubUserRepository.findByClubIdAndUserIds(clubId, trainingEventRequestModel.presentLeaders());
    List<ClubUser> presentMembers = clubUserRepository.findByClubIdAndUserIds(clubId, trainingEventRequestModel.presentMembers());
    TrainingEvent trainingEvent = createTrainingEvent(trainingEventRequestModel, presentLeaders, presentMembers, team);
    return new TrainingEventRecord(trainingEventRepository.save(trainingEvent));
  }

  @Override
  @Transactional
  public void delete(long trainingEventId) {
    trainingEventRepository.deleteById(trainingEventId);
  }

  @Override
  @Transactional
  public TrainingEventRecord get(long trainingEventId) {
    return new TrainingEventRecord(trainingEventRepository.findById(trainingEventId)
        .orElseThrow(AppUtils.createNotFoundExceptionSupplier("TrainingEvent not found: " + trainingEventId)));
  }

  @Override
  @Transactional
  public List<TrainingEventRecord> get(String teamId, PageRequest pageRequest) {
    Team team = teamRepository.findByTeamId(teamId).orElseThrow(AppUtils.createNotFoundExceptionSupplier("Team not found: " + teamId));
    return trainingEventRepository.findByTeam(team, pageRequest).stream().map(TrainingEventRecord::new).toList();
  }

  @Override
  public TrainingEventRecord update(String clubId, long trainingEventId, TrainingEventRequestModel trainingEventRequestModel) {
    // TODO verify leader has has role leader
    List<ClubUser> presentLeaders = clubUserRepository.findByClubIdAndUserIds(clubId, trainingEventRequestModel.presentLeaders());
    List<ClubUser> presentMembers = clubUserRepository.findByClubIdAndUserIds(clubId, trainingEventRequestModel.presentMembers());

    TrainingEvent trainingEvent = trainingEventRepository.findById(trainingEventId)
        .orElseThrow(AppUtils.createNotFoundExceptionSupplier("TrainingEvent not found: " + trainingEventId));

    updateTrainingEvent(trainingEvent, trainingEventRequestModel, presentLeaders, presentMembers);

    return new TrainingEventRecord(trainingEventRepository.save(trainingEvent));
  }

  private TrainingEvent createTrainingEvent(TrainingEventRequestModel trainingEventRequestModel, List<ClubUser> presentLeaders, List<ClubUser> presentMembers,
      Team team) {
    TrainingEvent trainingEvent = new TrainingEvent();
    trainingEvent.setDuration(trainingEventRequestModel.duration());
    trainingEvent.setDateTime(trainingEventRequestModel.dateTime());
    trainingEvent.setNotes(trainingEventRequestModel.notes());
    trainingEvent.setPresentLeaders(presentLeaders);
    trainingEvent.setPresentMembers(presentMembers);
    trainingEvent.setTeam(team);
    return trainingEvent;
  }

  private void updateTrainingEvent(TrainingEvent trainingEvent, TrainingEventRequestModel trainingEventRequestModel, List<ClubUser> presentLeaders,
      List<ClubUser> presentMembers) {
    trainingEvent.setDateTime(trainingEventRequestModel.dateTime());
    trainingEvent.setDuration(trainingEventRequestModel.duration());
    trainingEvent.setNotes(trainingEventRequestModel.notes());
    trainingEvent.setPresentLeaders(presentLeaders);
    trainingEvent.setPresentMembers(presentMembers);
  }
}
