package nu.borjessons.clubhouse.impl.data;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "club_role")
public class ClubRole extends BaseEntity implements GrantedAuthority {
  public static final String ROLE_PREFIX = "ROLE_";
  private static final long serialVersionUID = -7407722891109816623L;

  @ManyToOne
  @Getter
  private Club club;

  @Column(nullable = false, unique = true)
  @Getter
  private final String clubRoleId = UUID.randomUUID().toString();

  @Id
  @GeneratedValue
  @Getter
  private long id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Getter
  private RoleTemp role;

  @ManyToOne
  @Getter
  private User user;

  public ClubRole(RoleTemp role, User user, Club club) {
    this.role = role;
    setUser(Objects.requireNonNull(user));
    setClub(Objects.requireNonNull(club));
  }

  public void doOrphan() {
    user.removeClubRole(this);
    club.removeClubRole(this);
    user = null;
    club = null;
  }

  @Override
  public String getAuthority() {
    return ROLE_PREFIX + role;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((clubRoleId == null) ? 0 : clubRoleId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ClubRole other = (ClubRole) obj;
    if (clubRoleId == null) {
      return other.clubRoleId == null;
    } else
      return clubRoleId.equals(other.clubRoleId);
  }

  private void setClub(Club club) {
    this.club = club;
    club.addClubRole(this);
  }

  private void setUser(User user) {
    this.user = user;
    user.addClubRole(this);
  }

  public enum RoleTemp {
    ADMIN,
    USER,
    SYSTEM_ADMIN,
    OWNER,
    PARENT,
    CHILD,
    LEADER
  }
}
