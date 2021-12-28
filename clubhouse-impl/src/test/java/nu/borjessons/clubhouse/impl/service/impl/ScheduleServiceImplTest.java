package nu.borjessons.clubhouse.impl.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

class ScheduleServiceImplTest {
  private static final String USER_ID = "user-1";
  private static final String COOL_TEAM = "Cool Team";
  private static final String GIGANTIC_HALL = "Gigantic Hall";
  private static final LocalTime START_TIME = LocalTime.of(16, 30);
  private static final LocalTime END_TIME = LocalTime.of(18, 0);
  private static final String ROBINS_CLUB = "Robins Club";

  private static Club createClub() {
    Club club = new Club();
    club.setName("Robins Club");
    club.setPath("robins-club");
    club.setType(Club.Type.SPORT);

    club.addTeam(createTeam("Team 1", 1));
    club.addTeam(createTeam("Team 2", 4));

    return club;
  }

  private static Club createClub(DayOfWeek dayOfWeek) {
    Club club = new Club();
    club.setName(ROBINS_CLUB);
    club.setPath("robins-club");
    club.setType(Club.Type.SPORT);

    club.addTeam(createTeam(dayOfWeek));

    return club;
  }

  private static TrainingTime createTrainingTime(DayOfWeek dayOfWeek, String location) {
    TrainingTime trainingTime = new TrainingTime();
    trainingTime.setStartTime(START_TIME);
    trainingTime.setEndTime(END_TIME);
    trainingTime.setDayOfWeek(dayOfWeek);
    trainingTime.setLocation(location);
    return trainingTime;
  }

  private static Team createTeam(String name, int day) {
    Team team = new Team();
    team.setName(name);
    team.addTrainingTime(createTrainingTime(DayOfWeek.of(day), "Big Hall"));
    team.addTrainingTime(createTrainingTime(DayOfWeek.of(day + 1), "Small Hall"));
    team.addTrainingTime(createTrainingTime(DayOfWeek.of(day + 2), "Small Hall"));
    team.addTrainingTime(createTrainingTime(DayOfWeek.of(day + 3), "Small Hall"));
    return team;
  }

  private static Team createTeam(DayOfWeek dayOfWeek) {
    Team team = new Team();
    team.setName(COOL_TEAM);
    team.addTrainingTime(createTrainingTime(dayOfWeek, GIGANTIC_HALL));
    return team;
  }

  private static User createUser() {
    User user = new User(USER_ID);
    ClubUser clubUser = new ClubUser();
    user.addClubUser(clubUser);

    User child = new User();
    ClubUser childClubUser = new ClubUser();
    child.addClubUser(childClubUser);

    user.addChild(child);
    return user;
  }

  private static void addUserChildToTeam(Club club, User user) {
    Team team = club.getTeams().iterator().next();
    team.addMember(user.getChildren().iterator().next().getClubUser(club.getClubId()).orElseThrow());
  }

  private static void addUserToTeam(Club club, User user) {
    Team team = club.getTeams().iterator().next();
    team.addMember(user.getClubUser(club.getClubId()).orElseThrow());
  }

  private static void addLeaderToTeam(Club club, User user) {
    Team team = club.getTeams().iterator().next();
    team.addLeader(user.getClubUser(club.getClubId()).orElseThrow());
  }

  private static void addUserToClub(Club club, User user) {
    user.getClubUsers().forEach(club::addClubUser);
    user.getChildren().stream().map(User::getClubUsers).flatMap(Collection::stream).forEach(club::addClubUser);
  }

  private static void validateRecord(ClubScheduleRecord clubScheduleRecord, String clubId) {
    LocalDate today = LocalDate.now();
    Assertions.assertEquals(today, clubScheduleRecord.localDate());
    Assertions.assertEquals(ROBINS_CLUB, clubScheduleRecord.clubName());
    Assertions.assertEquals(clubId, clubScheduleRecord.clubId());

    List<TeamScheduleRecord> teamScheduleRecords = clubScheduleRecord.teamScheduleRecords();
    Assertions.assertEquals(1, teamScheduleRecords.size());

    TeamScheduleRecord teamScheduleRecord = teamScheduleRecords.iterator().next();
    Assertions.assertEquals("Cool Team", teamScheduleRecord.teamName());

    TrainingTimeRecord trainingTimeRecord = teamScheduleRecord.trainingTimeRecord();
    Assertions.assertNotNull(trainingTimeRecord);
    Assertions.assertEquals(today.getDayOfWeek(), trainingTimeRecord.dayOfWeek());
    Assertions.assertEquals(START_TIME, trainingTimeRecord.startTime());
    Assertions.assertEquals(END_TIME, trainingTimeRecord.endTime());
    Assertions.assertEquals(GIGANTIC_HALL, trainingTimeRecord.location());
  }

  @Test
  void getOneMonthOfClubScheduleRecords() {
    Club club = createClub();
    String clubId = club.getClubId();
    ClubRepository clubRepository = Mockito.mock(ClubRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(clubRepository.findByClubId(clubId)).thenReturn(Optional.of(club));
    ScheduleServiceImpl scheduleService = new ScheduleServiceImpl(clubRepository, userRepository);
    Collection<ClubScheduleRecord> clubScheduleRecords = scheduleService.getClubSchedule(clubId, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 31));

    Assertions.assertNotNull(clubScheduleRecords);
    Assertions.assertEquals(31, clubScheduleRecords.size());
    clubScheduleRecords.forEach(clubSchedule -> Assertions.assertTrue(clubSchedule.teamScheduleRecords().size() > 0));
  }

  @Test
  void getOneRecord() {
    LocalDate today = LocalDate.now();
    Club club = createClub();
    String clubId = club.getClubId();
    ClubRepository clubRepository = Mockito.mock(ClubRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(clubRepository.findByClubId(clubId)).thenReturn(Optional.of(club));
    ScheduleServiceImpl scheduleService = new ScheduleServiceImpl(clubRepository, userRepository);
    Collection<ClubScheduleRecord> clubScheduleRecords = scheduleService.getClubSchedule(clubId, today, today);

    Assertions.assertNotNull(clubScheduleRecords);
    Assertions.assertEquals(1, clubScheduleRecords.size());
  }

  @Test
  void getUserSchedulesForChild() {
    LocalDate today = LocalDate.now();
    DayOfWeek dayOfWeek = today.getDayOfWeek();
    Club club = createClub(dayOfWeek);
    User user = createUser();
    addUserToClub(club, user);
    addUserChildToTeam(club, user);

    ClubRepository clubRepository = Mockito.mock(ClubRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    String clubId = club.getClubId();
    Mockito.when(clubRepository.findByClubId(clubId)).thenReturn(Optional.of(club));
    Mockito.when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));

    ScheduleServiceImpl scheduleService = new ScheduleServiceImpl(clubRepository, userRepository);
    Collection<ClubScheduleRecord> clubScheduleRecords = scheduleService.getUserClubSchedule(user.getUserId(), clubId, today, today);

    Assertions.assertNotNull(clubScheduleRecords);
    Assertions.assertEquals(1, clubScheduleRecords.size());
    validateRecord(clubScheduleRecords.iterator().next(), clubId);
  }

  @Test
  void getUserSchedules() {
    LocalDate today = LocalDate.now();
    DayOfWeek dayOfWeek = today.getDayOfWeek();
    Club club = createClub(dayOfWeek);
    User user = createUser();
    addUserToClub(club, user);
    addUserToTeam(club, user);

    ClubRepository clubRepository = Mockito.mock(ClubRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    String clubId = club.getClubId();
    Mockito.when(clubRepository.findByClubId(clubId)).thenReturn(Optional.of(club));
    Mockito.when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));

    ScheduleServiceImpl scheduleService = new ScheduleServiceImpl(clubRepository, userRepository);
    Collection<ClubScheduleRecord> clubScheduleRecords = scheduleService.getUserClubSchedule(user.getUserId(), clubId, today, today);

    Assertions.assertNotNull(clubScheduleRecords);
    Assertions.assertEquals(1, clubScheduleRecords.size());
    validateRecord(clubScheduleRecords.iterator().next(), clubId);
  }

  @Test
  void getLeaderSchedule() {
    LocalDate today = LocalDate.now();
    DayOfWeek dayOfWeek = today.getDayOfWeek();
    Club club = createClub(dayOfWeek);
    User user = createUser();
    addUserToClub(club, user);
    addLeaderToTeam(club, user);

    ClubRepository clubRepository = Mockito.mock(ClubRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    String clubId = club.getClubId();
    Mockito.when(clubRepository.findByClubId(clubId)).thenReturn(Optional.of(club));
    Mockito.when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));

    ScheduleServiceImpl scheduleService = new ScheduleServiceImpl(clubRepository, userRepository);
    Collection<ClubScheduleRecord> clubScheduleRecords = scheduleService.getLeaderClubSchedule(user.getUserId(), clubId, today, today);

    Assertions.assertNotNull(clubScheduleRecords);
    Assertions.assertEquals(1, clubScheduleRecords.size());
    validateRecord(clubScheduleRecords.iterator().next(), clubId);
  }
}