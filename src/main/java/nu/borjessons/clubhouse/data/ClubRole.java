package nu.borjessons.clubhouse.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "club_role")
public class ClubRole extends BaseEntity implements Serializable, GrantedAuthority {

    public static final String ROLE_PREFIX = "ROLE_";

    private static final long serialVersionUID = 5386658724998732091L;

    public enum Role {
        ADMIN, USER, SYSTEM_ADMIN, OWNER, PARENT, CHILD
    }

    public ClubRole(Role role, User user, Club club) {
        this.role = role;
        setUser(Objects.requireNonNull(user));
        setClub(Objects.requireNonNull(club));
    }

    @Id
    @GeneratedValue
    @Getter
    private long id;

    @Column(nullable = false, unique = true)
    @Getter
    private final String clubRoleId = UUID.randomUUID().toString();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private Role role;

    @ManyToOne
    @Getter
    private User user;

    @ManyToOne
    @Getter
    private Club club;

    private void setUser(User user) {
        this.user = user;
        user.addClubRole(this);
    }

    private void setClub(Club club) {
        this.club = club;
        club.addClubRole(this);
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
            if (other.clubRoleId != null)
                return false;
        } else if (!clubRoleId.equals(other.clubRoleId))
            return false;
        return true;
    }

}
