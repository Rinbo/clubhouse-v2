package nu.borjessons.clubhouse.impl.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "team")
public class Team extends BaseEntity {
  @ManyToOne
  private Club club;

  @Id
  @GeneratedValue
  private long id;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<User> leaders = new HashSet<>();

  private int maxAge;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<User> members = new HashSet<>();

  private int minAge;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String teamId = UUID.randomUUID().toString();

  public void addLeader(User leader) {
    leaders.add(leader);
  }

  public void addMember(User user) {
    members.add(user);
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((teamId == null) ? 0 : teamId.hashCode());
    return result;
  }

  public void removeLeader(User leader) {
    leaders.remove(leader);
  }

  public void removeMember(User user) {
    members.remove(user);
  }
}
