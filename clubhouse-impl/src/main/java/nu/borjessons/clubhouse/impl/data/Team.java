package nu.borjessons.clubhouse.impl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "team")
public class Team extends BaseEntity {
  private static final long serialVersionUID = -6778870690760953845L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, unique = true)
  private final String teamId;

  @ManyToOne(fetch = FetchType.LAZY)
  private Club club;

  @ManyToMany(fetch = FetchType.LAZY)
  private List<ClubUser> leaders = new ArrayList<>();

  private int maxAge;

  @ManyToMany(fetch = FetchType.LAZY)
  private List<ClubUser> members = new ArrayList<>();

  private int minAge;

  @Column(nullable = false)
  private String name;

  public Team(String teamId) {
    this.teamId = teamId;
  }

  public Team() {
    this.teamId = UUID.randomUUID().toString();
  }

  public void addMember(ClubUser clubUser) {
    members.add(clubUser);
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

  public void removeLeader(ClubUser leader) {
    leaders.remove(leader);
  }

  public void removeMember(ClubUser member) {
    members.remove(member);
  }
}
