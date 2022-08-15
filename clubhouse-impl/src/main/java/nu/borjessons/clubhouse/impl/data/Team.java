package nu.borjessons.clubhouse.impl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "team", indexes = {@Index(name = "idx_team_teamid", columnList = "teamId")})
public class Team extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  private Club club;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToMany(mappedBy = "managedTeams", fetch = FetchType.LAZY)
  private List<ClubUser> leaders = new ArrayList<>();

  @ManyToMany(mappedBy = "teams", fetch = FetchType.LAZY)
  private List<ClubUser> members = new ArrayList<>();

  @Column(nullable = false)
  private String name;

  @Setter(AccessLevel.PRIVATE)
  @Column(nullable = false, unique = true)
  private String teamId;

  @OneToMany(mappedBy = "team", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private final List<TeamPost> teamPosts = new ArrayList<>();

  @OneToMany(mappedBy = "team", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<TrainingTime> trainingTimes;

  public Team(String teamId) {
    this.teamId = teamId;
  }

  public Team() {
    this.teamId = UUID.randomUUID().toString();
    trainingTimes = new ArrayList<>();
  }

  public void addLeader(ClubUser clubUser) {
    leaders.add(clubUser);
    clubUser.getManagedTeams().add(this);
  }

  public void addMember(ClubUser clubUser) {
    members.add(clubUser);
    clubUser.getTeams().add(this);
  }

  public void addTrainingTime(TrainingTime trainingTime) {
    trainingTimes.add(trainingTime);
    trainingTime.setTeam(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((teamId == null) ? 0 : teamId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Team other))
      return false;
    return teamId.equals(other.teamId);
  }

  public void removeLeader(ClubUser clubUser) {
    leaders.remove(clubUser);
    clubUser.getManagedTeams().remove(this);
  }

  public void removeMember(ClubUser clubUser) {
    members.remove(clubUser);
    clubUser.getTeams().remove(this);
  }
}
