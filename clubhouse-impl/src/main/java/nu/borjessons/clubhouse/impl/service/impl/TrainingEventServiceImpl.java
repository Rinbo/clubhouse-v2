package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.repository.TrainingEventRepository;
import nu.borjessons.clubhouse.impl.repository.TrainingTimeRepository;
import nu.borjessons.clubhouse.impl.service.TrainingEventService;
import nu.borjessons.clubhouse.impl.service.UpcomingTrainingEventProducer;
import nu.borjessons.clubhouse.impl.util.AppUtils;

@Service
@RequiredArgsConstructor
public class TrainingEventServiceImpl implements TrainingEventService {
  private static TrainingEvent createTrainingEvent(TrainingEventRequestModel trainingEventRequestModel, List<ClubUser> presentLeaders,
      List<ClubUser> presentMembers, Team team) {
    TrainingEvent trainingEvent = new TrainingEvent();
    trainingEvent.setDuration(trainingEventRequestModel.duration());
    trainingEvent.setLocalDateTime(trainingEventRequestModel.localDateTime());
    trainingEvent.setLocation(trainingEventRequestModel.location());
    trainingEvent.setNotes(trainingEventRequestModel.notes());
    trainingEvent.setPresentLeaders(presentLeaders);
    trainingEvent.setPresentMembers(presentMembers);
    trainingEvent.setTeam(team);
    return trainingEvent;
  }

  private static void updateTrainingEvent(TrainingEvent trainingEvent, TrainingEventRequestModel trainingEventRequestModel, List<ClubUser> presentLeaders,
      List<ClubUser> presentMembers) {
    trainingEvent.setLocalDateTime(trainingEventRequestModel.localDateTime());
    trainingEvent.setDuration(trainingEventRequestModel.duration());
    trainingEvent.setLocation(trainingEventRequestModel.location());
    trainingEvent.setNotes(trainingEventRequestModel.notes());
    trainingEvent.setPresentLeaders(presentLeaders);
    trainingEvent.setPresentMembers(presentMembers);
  }

  private static List<ClubUser> verifyIsLeader(List<ClubUser> clubUsers) {
    List<ClubUser> leaders = new ArrayList<>();

    for (ClubUser clubUser : clubUsers) {
      List<Role> roles = clubUser.getRoles().stream().map(RoleEntity::getName).toList();
      if (roles.contains(Role.LEADER)) leaders.add(clubUser);
    }

    return leaders;
  }

  private final ClubUserRepository clubUserRepository;
  private final TeamRepository teamRepository;
  private final TrainingEventRepository trainingEventRepository;
  private final TrainingTimeRepository trainingTimeRepository;
  private final UpcomingTrainingEventProducer upcomingTrainingEventProducer;

  @Override
  @Transactional
  public TrainingEventRecord create(String clubId, String teamId, TrainingEventRequestModel trainingEventRequestModel) {
    Team team = teamRepository.findByTeamId(teamId).orElseThrow(AppUtils.createNotFoundExceptionSupplier("Team not found: " + teamId));
    List<ClubUser> presentLeaders = verifyIsLeader(clubUserRepository.findByClubIdAndUserIds(clubId, trainingEventRequestModel.presentLeaders()));
    List<ClubUser> presentMembers = clubUserRepository.findByClubIdAndUserIds(clubId, trainingEventRequestModel.presentMembers());
    TrainingEvent trainingEvent = createTrainingEvent(trainingEventRequestModel, presentLeaders, presentMembers, team);
    setAndUpdateTrainingTime(trainingEvent, trainingEventRequestModel.trainingTimeId());

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
  public List<TrainingEventRecord> getUpcomingTrainingEvents(long userId, String clubId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId)
        .orElseThrow(AppUtils.createNotFoundExceptionSupplier("User not found: " + userId));

    List<Team> teams = clubUser.getManagedTeams();
    List<TrainingEvent> trainingEvents = trainingEventRepository.findByTeamIn(teams, PageRequest.of(0, 20, Sort.by("localDateTime").descending()));
    return upcomingTrainingEventProducer.getUpcomingTrainingEvents(trainingEvents).stream().map(TrainingEventRecord::new).toList();
  }

  @Override
  @Transactional
  public TrainingEventRecord update(String clubId, long trainingEventId, TrainingEventRequestModel trainingEventRequestModel) {
    List<ClubUser> presentLeaders = verifyIsLeader(clubUserRepository.findByClubIdAndUserIds(clubId, trainingEventRequestModel.presentLeaders()));
    List<ClubUser> presentMembers = clubUserRepository.findByClubIdAndUserIds(clubId, trainingEventRequestModel.presentMembers());

    TrainingEvent trainingEvent = trainingEventRepository.findById(trainingEventId)
        .orElseThrow(AppUtils.createNotFoundExceptionSupplier("TrainingEvent not found: " + trainingEventId));

    updateTrainingEvent(trainingEvent, trainingEventRequestModel, presentLeaders, presentMembers);

    return new TrainingEventRecord(trainingEventRepository.save(trainingEvent));
  }

  private void setAndUpdateTrainingTime(TrainingEvent trainingEvent, String trainingTimeId) {
    if (trainingTimeId == null) return;
    trainingTimeRepository.findByTrainingTimeId(trainingTimeId).ifPresent(trainingTime -> {
      trainingEvent.setTrainingTime(trainingTime);
      trainingTime.setLastActivated(LocalDateTime.now());
      trainingTimeRepository.save(trainingTime);
    });
  }
}
