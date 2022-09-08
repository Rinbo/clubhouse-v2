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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.UpcomingTrainingEvent;

class UpcomingTrainingEventProducerTest {
  private static final Instant INSTANT = LocalDateTime.of(2020, 1, 1, 9, 0).toInstant(ZoneOffset.UTC);

  private static Team createTeam(LocalDateTime lastActivated) {
    Team team = new Team("teamId");
    team.setName("My Team");
    team.setTrainingTimes(createTrainingTimes(lastActivated));

    return team;
  }

  private static List<TrainingTime> createTrainingTimes(LocalDateTime lastActivated) {
    TrainingTime trainingTime1 = new TrainingTime();
    trainingTime1.setLocation("Big Hall");
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
    UpcomingTrainingEventProducer upcomingTrainingEventProducer = new UpcomingTrainingEventProducer(Duration.ofHours(2),
        Clock.fixed(wrongDay, ZoneId.systemDefault()));

    List<UpcomingTrainingEvent> upcomingTrainingEvents = upcomingTrainingEventProducer.createUpcomingTrainingEvents(List.of(createTeam(null)));
    Assertions.assertEquals(0, upcomingTrainingEvents.size());
  }

  @Test
  void createUpcomingTrainingEventsWithLastActivatedTest() {
    UpcomingTrainingEventProducer upcomingTrainingEventProducer = new UpcomingTrainingEventProducer(Duration.ofHours(2),
        Clock.fixed(INSTANT, ZoneId.systemDefault()));

    Assertions.assertEquals(0, upcomingTrainingEventProducer.createUpcomingTrainingEvents(List.of(createTeam(LocalDateTime.of(2020, 1, 1, 7, 0)))).size());

    List<UpcomingTrainingEvent> upcomingTrainingEvents =
        upcomingTrainingEventProducer.createUpcomingTrainingEvents(List.of(createTeam(LocalDateTime.of(2020, 1, 1, 3, 0))));

    UpcomingTrainingEvent upcomingTrainingEvent = upcomingTrainingEvents.get(0);

    Assertions.assertEquals("My Team", upcomingTrainingEvent.teamName());
    Assertions.assertEquals("teamId", upcomingTrainingEvent.teamId());
    Assertions.assertEquals("Big Hall", upcomingTrainingEvent.location());
    Assertions.assertNotNull(upcomingTrainingEvent.trainingTimeId());
    Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 8, 0), upcomingTrainingEvent.localDateTime());
  }

  @Test
  void createUpcomingTrainingEventsWithoutLastActivatedTest() {
    UpcomingTrainingEventProducer upcomingTrainingEventProducer = new UpcomingTrainingEventProducer(Duration.ofHours(2),
        Clock.fixed(INSTANT, ZoneId.systemDefault()));

    List<UpcomingTrainingEvent> upcomingTrainingEvents = upcomingTrainingEventProducer.createUpcomingTrainingEvents(List.of(createTeam(null)));
    Assertions.assertEquals(1, upcomingTrainingEvents.size());

    UpcomingTrainingEvent upcomingTrainingEvent = upcomingTrainingEvents.get(0);

    Assertions.assertEquals("My Team", upcomingTrainingEvent.teamName());
    Assertions.assertEquals("teamId", upcomingTrainingEvent.teamId());
    Assertions.assertEquals("Big Hall", upcomingTrainingEvent.location());
    Assertions.assertNotNull(upcomingTrainingEvent.trainingTimeId());
    Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 8, 0), upcomingTrainingEvent.localDateTime());
  }
}