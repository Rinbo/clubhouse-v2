package nu.borjessons.clubhouse.impl.util;

import java.time.Clock;
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

class UpcomingTrainingTimeProducerTest {
  public static final Instant INSTANT = LocalDateTime.of(2020, 1, 1, 9, 0).toInstant(ZoneOffset.UTC);

  private static Team createTeam() {
    Team team = new Team("teamId");
    team.setName("My Team");
    team.setTrainingTimes(createTrainingTimes());

    return team;
  }

  private static List<TrainingTime> createTrainingTimes() {
    TrainingTime trainingTime1 = new TrainingTime();
    trainingTime1.setLocation("Big Hall");
    trainingTime1.setStartTime(LocalTime.of(8, 0));
    trainingTime1.setEndTime(LocalTime.of(10, 0));

    TrainingTime trainingTime2 = new TrainingTime();
    trainingTime2.setLocation("Small Hall");
    trainingTime2.setStartTime(LocalTime.of(14, 0));
    trainingTime2.setEndTime(LocalTime.of(16, 0));

    return List.of(trainingTime1, trainingTime2);
  }

  @Test
  void createUpcomingTrainingEventsTest() {
    UpcomingTrainingEventProducer upcomingTrainingEventProducer = new UpcomingTrainingEventProducer(Duration.ofHours(2),
        Clock.fixed(INSTANT, ZoneId.systemDefault()));

    List<UpcomingTrainingEvent> upcomingTrainingEvents = upcomingTrainingEventProducer.createUpcomingTrainingEvents(List.of(createTeam()));
    Assertions.assertEquals(1, upcomingTrainingEvents.size());

    UpcomingTrainingEvent upcomingTrainingEvent = upcomingTrainingEvents.get(0);

    Assertions.assertEquals("My Team", upcomingTrainingEvent.teamName());
    Assertions.assertEquals("teamId", upcomingTrainingEvent.teamId());
    Assertions.assertEquals("Big Hall", upcomingTrainingEvent.location());
    LocalDateTime expectedLocalDateTime = LocalDateTime.of(2020, 1, 1, 8, 0);
    Assertions.assertEquals(expectedLocalDateTime, upcomingTrainingEvent.localDateTime());
  }
}