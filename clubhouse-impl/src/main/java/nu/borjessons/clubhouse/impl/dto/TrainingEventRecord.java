package nu.borjessons.clubhouse.impl.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;
import nu.borjessons.clubhouse.impl.util.Validate;

public record TrainingEventRecord(
    long id,
    LocalDateTime localDateTime,
    Duration duration,
    String location,
    String notes,
    List<BaseUserRecord> presentLeaders,
    List<BaseUserRecord> presentMembers,
    String teamId) {

  public TrainingEventRecord {
    Validate.notNegative(id, "id");
    Validate.notNull(localDateTime, "localDateTime");
    Validate.notNull(duration, "duration");
    Validate.notNull(presentLeaders, "presentLeaders");
    Validate.notNull(presentMembers, "presentMembers");
    Validate.notEmpty(teamId, "teamId");
  }

  public TrainingEventRecord(TrainingEvent trainingEvent) {
    this(
        trainingEvent.getId(),
        trainingEvent.getLocalDateTime(),
        trainingEvent.getDuration(),
        trainingEvent.getLocation(),
        trainingEvent.getNotes(),
        trainingEvent.getPresentLeaders().stream().map(ClubUser::getUser).map(BaseUserRecord::new).toList(),
        trainingEvent.getPresentMembers().stream().map(ClubUser::getUser).map(BaseUserRecord::new).toList(),
        trainingEvent.getTeam().getTeamId()
    );
  }
}
