package nu.borjessons.clubhouse.impl.data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class TrainingEvent {
  private LocalDateTime dateTime;

  private Duration duration;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @ManyToMany
  private List<ClubUser> presentLeaders = new ArrayList<>();

  @ManyToMany
  private List<ClubUser> presentMembers = new ArrayList<>();

  @ManyToOne
  private Team team;
}
