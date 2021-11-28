package nu.borjessons.clubhouse.impl.data;

import java.io.Serial;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "team")
public class Team extends BaseEntity {
  @Serial
  private static final long serialVersionUID = -6778870690760953845L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Setter(AccessLevel.PRIVATE)
  @Column(nullable = false, unique = true)
  private String teamId;

  @ManyToOne(fetch = FetchType.LAZY)
  private Club club;

  @ManyToMany(mappedBy = "managedTeams", fetch = FetchType.LAZY)
  private List<ClubUser> leaders = new ArrayList<>();

  @ManyToMany(mappedBy = "teams", fetch = FetchType.LAZY)
  private List<ClubUser> members = new ArrayList<>();

  private int minAge;

  private int maxAge;

  @Column(nullable = false)
  private String name;

  @OneToOne(mappedBy = "team", orphanRemoval = true, cascade = CascadeType.ALL)
  private Schedule schedule;

  public Team(String teamId) {
    this.teamId = teamId;
  }

  public Team() {
    this.teamId = UUID.randomUUID().toString();
  }

  public void addMember(ClubUser clubUser) {
    members.add(clubUser);
    clubUser.getTeams().add(this);
  }

  public void removeMember(ClubUser clubUser) {
    members.remove(clubUser);
    clubUser.getTeams().remove(this);
  }

  public void addLeader(ClubUser clubUser) {
    leaders.add(clubUser);
    clubUser.getManagedTeams().add(this);
  }

  public void removeLeader(ClubUser clubUser) {
    leaders.remove(clubUser);
    clubUser.getManagedTeams().remove(this);
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
    if (!(obj instanceof Team))
      return false;
    Team other = (Team) obj;
    return teamId.equals(other.teamId);
  }
}
