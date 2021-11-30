package nu.borjessons.clubhouse.impl.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "schedule")
public class Schedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToOne
  private Team team;

  @Column(name = "period_start", nullable = false)
  private LocalDate periodStart;

  @Column(name = "period_end", nullable = false)
  private LocalDate periodEnd;

  @Column(name = "training_times")
  @OneToMany(mappedBy = "schedule", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<TrainingTime> trainingTimes = new ArrayList<>();

  public void addTrainingTime(TrainingTime trainingTime) {
    trainingTime.setSchedule(this);
    trainingTimes.add(trainingTime);
  }
}
