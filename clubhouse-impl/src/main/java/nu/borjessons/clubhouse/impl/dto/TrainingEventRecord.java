package nu.borjessons.clubhouse.impl.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;
import nu.borjessons.clubhouse.impl.util.Validate;

public record TrainingEventRecord(
    long id,
    LocalDateTime dateTime,
    Duration duration,
    String notes,
    List<BaseUserRecord> presentLeaders,
    List<BaseUserRecord> presentMembers) {

  public TrainingEventRecord {
    Validate.notNegative(id, "id");
    Validate.notNull(dateTime, "dateTime");
    Validate.notNull(duration, "duration");
    Validate.notEmpty(presentLeaders, "presentLeaders");
    Validate.notEmpty(presentMembers, "presentMembers");
  }

  public TrainingEventRecord(TrainingEvent trainingEvent) {
    this(
        trainingEvent.getId(),
        trainingEvent.getDateTime(),
        trainingEvent.getDuration(),
        trainingEvent.getNotes(),
        trainingEvent.getPresentLeaders().stream().map(ClubUser::getUser).map(BaseUserRecord::new).toList(),
        trainingEvent.getPresentMembers().stream().map(ClubUser::getUser).map(BaseUserRecord::new).toList()
    );
  }
}
