package nu.borjessons.clubhouse.impl.data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "training_time", indexes = @Index(name = "ix_training_time_id", columnList = "training_time_id"))
public class TrainingTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "training_time_id", nullable = false, unique = true)
  private String trainingTimeId;

  @Column(name = "day_of_week", nullable = false)
  @Enumerated(EnumType.STRING)
  private DayOfWeek dayOfWeek;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  @ManyToOne
  private Team team;

  @Column(nullable = false)
  private String location;

  public TrainingTime() {
    this.trainingTimeId = UUID.randomUUID().toString();
  }
}
