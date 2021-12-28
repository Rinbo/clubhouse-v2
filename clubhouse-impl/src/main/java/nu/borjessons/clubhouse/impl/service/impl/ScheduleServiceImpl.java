package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.ClubScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.TeamScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.ScheduleService;

@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
  private static List<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
    List<LocalDate> localDates = new ArrayList<>();
    LocalDate localDate = startDate;

    while (localDate.isBefore(endDate.plus(1, ChronoUnit.DAYS))) {
      localDates.add(localDate);
      localDate = localDate.plus(1, ChronoUnit.DAYS);
    }

    return localDates;
  }

  private static void populateTeamSchedules(Set<Team> teams, LocalDate localDate, List<TeamScheduleRecord> teamScheduleRecords) {
    for (Team team : teams) {
      for (TrainingTime trainingTime : team.getTrainingTimes()) {
        if (trainingTime.getDayOfWeek() == localDate.getDayOfWeek()) {
          teamScheduleRecords.add(new TeamScheduleRecord(team.getTeamId(), team.getName(), new TrainingTimeRecord(trainingTime)));
        }
      }
    }
  }

  private static List<ClubScheduleRecord> getClubScheduleRecords(Club club, Set<Team> teams, List<LocalDate> dateRange) {
    List<ClubScheduleRecord> clubScheduleRecords = new ArrayList<>();

    for (LocalDate localDate : dateRange) {
      List<TeamScheduleRecord> teamScheduleRecords = new ArrayList<>();
      populateTeamSchedules(teams, localDate, teamScheduleRecords);
      clubScheduleRecords.add(new ClubScheduleRecord(club.getClubId(), club.getName(), localDate, teamScheduleRecords));
    }
    return clubScheduleRecords;
  }

  private final ClubRepository clubRepository;
  private final UserRepository userRepository;

  @Override
  public Collection<ClubScheduleRecord> getClubSchedule(String clubId, LocalDate startDate, LocalDate endDate) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    Set<Team> teams = club.getTeams();
    List<LocalDate> dateRange = getDateRange(startDate, endDate);

    return getClubScheduleRecords(club, teams, dateRange);
  }

  @Override
  public Collection<ClubScheduleRecord> getUserClubSchedule(String userId, String clubId, LocalDate startDate, LocalDate endDate) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    User user = userRepository.findByUserId(userId).orElseThrow();
    List<ClubUser> userAndChildren = user.getChildren().stream().map(child -> child.getClubUser(clubId)).map(Optional::get).collect(Collectors.toList());
    userAndChildren.add(user.getClubUser(clubId).orElseThrow());
    Set<Team> teams = userAndChildren.stream().map(ClubUser::getTeams).flatMap(List::stream).collect(Collectors.toSet());
    return getClubScheduleRecords(club, teams, getDateRange(startDate, endDate));
  }

  @Override
  public Collection<ClubScheduleRecord> getLeaderClubSchedule(String userId, String clubId, LocalDate startDate, LocalDate endDate) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    User user = userRepository.findByUserId(userId).orElseThrow();
    Set<Team> teams = new HashSet<>(user.getClubUser(clubId).orElseThrow().getManagedTeams());
    return getClubScheduleRecords(club, teams, getDateRange(startDate, endDate));
  }
}
