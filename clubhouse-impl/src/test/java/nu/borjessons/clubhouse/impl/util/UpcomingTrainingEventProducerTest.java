package nu.borjessons.clubhouse.impl.util;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.UpcomingTrainingEvent;
import nu.borjessons.clubhouse.impl.service.UpcomingTrainingEventProducer;

class UpcomingTrainingEventProducerTest {
  private static final String BIG_HALL = "Big Hall";
  private static final Instant INSTANT = LocalDateTime.of(2020, 1, 1, 9, 0).toInstant(ZoneOffset.UTC);
  private static final String TEAM_ID = "teamId";
  private static final String TEAM_NAME = "My Team";
  private static final Duration THRESHOLD = Duration.ofHours(2);

  private static Team createTeam(LocalDateTime lastActivated) {
    Team team = new Team(TEAM_ID);
    team.setName(TEAM_NAME);
    team.setTrainingTimes(createTrainingTimes(lastActivated));

    return team;
  }

  private static TrainingEvent createTrainingEvent(LocalDateTime localDateTime) {
    TrainingEvent trainingEvent = new TrainingEvent();
    trainingEvent.setLocation(BIG_HALL);
    trainingEvent.setDuration(Duration.ofHours(1));
    trainingEvent.setNotes("Notes");
    trainingEvent.setTeam(createTeam(null));
    trainingEvent.setLocalDateTime(localDateTime);
    return trainingEvent;
  }

  private static List<TrainingTime> createTrainingTimes(LocalDateTime lastActivated) {
    TrainingTime trainingTime1 = new TrainingTime();
    trainingTime1.setLocation(BIG_HALL);
    trainingTime1.setDayOfWeek(DayOfWeek.WEDNESDAY);
    trainingTime1.setStartTime(LocalTime.of(8, 0));
    trainingTime1.setEndTime(LocalTime.of(10, 0));
    trainingTime1.setLastActivated(lastActivated);

    TrainingTime trainingTime2 = new TrainingTime();
    trainingTime2.setLocation("Small Hall");
    trainingTime2.setDayOfWeek(DayOfWeek.WEDNESDAY);
    trainingTime2.setStartTime(LocalTime.of(14, 0));
    trainingTime2.setEndTime(LocalTime.of(16, 0));

    return List.of(trainingTime1, trainingTime2);
  }

  @Test
  void createUpcomingTrainingEventsNotThisDayOfWeekTest() {
    Instant wrongDay = LocalDateTime.of(2020, 1, 2, 9, 0).toInstant(ZoneOffset.UTC);
    UpcomingTrainingEventProducer upcomingTrainingEventProducer = new UpcomingTrainingEventProducer(THRESHOLD,
        Clock.fixed(wrongDay, ZoneId.systemDefault()));

    List<UpcomingTrainingEvent> upcomingTrainingEvents = upcomingTrainingEventProducer.getUpcomingTraining(List.of(createTeam(null)));
    Assertions.assertEquals(0, upcomingTrainingEvents.size());
  }

  @Test
  void createUpcomingTrainingEventsWithLastActivatedTest() {
    UpcomingTrainingEventProducer upcomingTrainingEventProducer = new UpcomingTrainingEventProducer(THRESHOLD,
        Clock.fixed(INSTANT, ZoneId.systemDefault()));

    Assertions.assertEquals(0, upcomingTrainingEventProducer.getUpcomingTraining(List.of(createTeam(LocalDateTime.of(2020, 1, 1, 7, 0)))).size());

    List<UpcomingTrainingEvent> upcomingTrainingEvents =
        upcomingTrainingEventProducer.getUpcomingTraining(List.of(createTeam(LocalDateTime.of(2020, 1, 1, 3, 0))));

    UpcomingTrainingEvent upcomingTrainingEvent = upcomingTrainingEvents.get(0);

    Assertions.assertEquals(TEAM_NAME, upcomingTrainingEvent.teamName());
    Assertions.assertEquals(TEAM_ID, upcomingTrainingEvent.teamId());
    Assertions.assertEquals(BIG_HALL, upcomingTrainingEvent.location());
    Assertions.assertNotNull(upcomingTrainingEvent.trainingTimeId());
    Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 8, 0), upcomingTrainingEvent.localDateTime());
  }

  @Test
  void createUpcomingTrainingEventsWithoutLastActivatedTest() {
    UpcomingTrainingEventProducer upcomingTrainingEventProducer = new UpcomingTrainingEventProducer(THRESHOLD,
        Clock.fixed(INSTANT, ZoneId.systemDefault()));

    List<UpcomingTrainingEvent> upcomingTrainingEvents = upcomingTrainingEventProducer.getUpcomingTraining(List.of(createTeam(null)));
    Assertions.assertEquals(1, upcomingTrainingEvents.size());

    UpcomingTrainingEvent upcomingTrainingEvent = upcomingTrainingEvents.get(0);

    Assertions.assertEquals(TEAM_NAME, upcomingTrainingEvent.teamName());
    Assertions.assertEquals(TEAM_ID, upcomingTrainingEvent.teamId());
    Assertions.assertEquals(BIG_HALL, upcomingTrainingEvent.location());
    Assertions.assertNotNull(upcomingTrainingEvent.trainingTimeId());
    Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 8, 0), upcomingTrainingEvent.localDateTime());
  }

  @Test
  void getUpcomingTrainingEventsTest() {
    UpcomingTrainingEventProducer upcomingTrainingEventProducer = new UpcomingTrainingEventProducer(THRESHOLD, Clock.fixed(INSTANT, ZoneOffset.UTC));
    LocalDateTime localDateTime = LocalDateTime.ofInstant(INSTANT, ZoneOffset.UTC);

    List<UpcomingTrainingEvent> upcomingTrainingEvents = upcomingTrainingEventProducer.getUpcomingTrainingEvents(List.of(createTrainingEvent(localDateTime)));

    Assertions.assertFalse(upcomingTrainingEvents.isEmpty());

    UpcomingTrainingEvent upcomingTrainingEvent = upcomingTrainingEvents.get(0);
    Assertions.assertEquals(BIG_HALL, upcomingTrainingEvent.location());
    Assertions.assertEquals(TEAM_NAME, upcomingTrainingEvent.teamName());
    Assertions.assertEquals(TEAM_ID, upcomingTrainingEvent.teamId());
    Assertions.assertEquals(localDateTime, upcomingTrainingEvent.localDateTime());

    List<TrainingEvent> trainingEvents = Stream.of(LocalDateTime.ofInstant(INSTANT.plus(THRESHOLD), ZoneOffset.UTC),
            LocalDateTime.ofInstant(INSTANT.minus(THRESHOLD), ZoneOffset.UTC))
        .map(UpcomingTrainingEventProducerTest::createTrainingEvent)
        .toList();

    Assertions.assertTrue(upcomingTrainingEventProducer.getUpcomingTrainingEvents(trainingEvents).isEmpty());
  }
}