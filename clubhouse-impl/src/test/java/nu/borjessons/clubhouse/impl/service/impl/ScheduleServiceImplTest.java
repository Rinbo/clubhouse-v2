package nu.borjessons.clubhouse.impl.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.ClubScheduleRecord;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;

class ScheduleServiceImplTest {
  private static Club createClub() {
    Club club = new Club();
    club.setName("Robins Club");
    club.setPath("robins-club");
    club.setType(Club.Type.SPORT);

    club.addTeam(createTeams("Team 1", 1));
    club.addTeam(createTeams("Team 2", 4));

    return club;
  }

  private static TrainingTime createTrainingTime(DayOfWeek dayOfWeek, String location) {
    TrainingTime trainingTime = new TrainingTime();
    trainingTime.setStartTime(LocalTime.of(16, 30));
    trainingTime.setEndTime(LocalTime.of(18, 0));
    trainingTime.setDayOfWeek(dayOfWeek);
    trainingTime.setLocation(location);
    return trainingTime;
  }

  private static Team createTeams(String name, int day) {
    Team team = new Team();
    team.setName(name);
    team.addTrainingTime(createTrainingTime(DayOfWeek.of(day), "Big Hall"));
    team.addTrainingTime(createTrainingTime(DayOfWeek.of(day + 1), "Small Hall"));
    team.addTrainingTime(createTrainingTime(DayOfWeek.of(day + 2), "Small Hall"));
    team.addTrainingTime(createTrainingTime(DayOfWeek.of(day + 3), "Small Hall"));
    return team;
  }

  @Test
  void getOneMonthOfClubScheduleRecords() {
    Club club = createClub();
    String clubId = club.getClubId();
    ClubRepository clubRepository = Mockito.mock(ClubRepository.class);
    Mockito.when(clubRepository.findByClubId(clubId)).thenReturn(Optional.of(club));
    ScheduleServiceImpl scheduleService = new ScheduleServiceImpl(clubRepository);
    Collection<ClubScheduleRecord> clubScheduleRecords = scheduleService.getClubSchedule(clubId, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31));

    Assertions.assertNotNull(clubScheduleRecords);
    Assertions.assertEquals(31, clubScheduleRecords.size());
    clubScheduleRecords.forEach(clubSchedule -> Assertions.assertTrue(clubSchedule.teamScheduleRecords().size() > 0));
  }

  @Test
  void getOneRecord() {
    Club club = createClub();
    String clubId = club.getClubId();
    ClubRepository clubRepository = Mockito.mock(ClubRepository.class);
    Mockito.when(clubRepository.findByClubId(clubId)).thenReturn(Optional.of(club));
    ScheduleServiceImpl scheduleService = new ScheduleServiceImpl(clubRepository);
    Collection<ClubScheduleRecord> clubScheduleRecords = scheduleService.getClubSchedule(clubId, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 1));

    Assertions.assertNotNull(clubScheduleRecords);
    Assertions.assertEquals(1, clubScheduleRecords.size());
  }
}