package nu.borjessons.clubhouse.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "team")
public class Team extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id @GeneratedValue private long id;

  @Column(nullable = false, unique = true)
  private final String teamId = UUID.randomUUID().toString();

  @Column(nullable = false)
  private String name;

  private int minAge;

  private int maxAge;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<User> members = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<User> leaders = new HashSet<>();

  @ManyToOne private Club club;

  public void addMember(User user) {
    members.add(user);
  }

  public void removeMember(User user) {
    members.remove(user);
  }

  public void addLeader(User leader) {
    leaders.add(leader);
  }

  public void removeLeader(User leader) {
    leaders.remove(leader);
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
    if (this == obj) return true;
    if (!(obj instanceof Team)) return false;
    Team other = (Team) obj;
    return teamId.equals(other.teamId);
  }
}
