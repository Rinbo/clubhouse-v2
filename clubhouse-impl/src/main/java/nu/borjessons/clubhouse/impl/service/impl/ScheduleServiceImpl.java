package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.ClubScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.TeamScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
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

  private final ClubRepository clubRepository;

  @Override
  public Collection<ClubScheduleRecord> getClubSchedule(String clubId, LocalDate startDate, LocalDate endDate) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    Set<Team> teams = club.getTeams();
    List<LocalDate> dateRange = getDateRange(startDate, endDate);

    List<ClubScheduleRecord> clubScheduleRecords = new ArrayList<>();
    String clubName = club.getName();

    for (LocalDate localDate : dateRange) {
      List<TeamScheduleRecord> teamScheduleRecords = new ArrayList<>();
      populateTeamSchedules(teams, localDate, teamScheduleRecords);
      clubScheduleRecords.add(new ClubScheduleRecord(clubId, clubName, localDate, teamScheduleRecords));
    }
    return clubScheduleRecords;
  }
}
