package nu.borjessons.clubhouse.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "club_role")
public class ClubRole  extends BaseEntity implements Serializable, GrantedAuthority {
	
	public static final String ROLE_PREFIX = "ROLE_"; 

	private static final long serialVersionUID = 5386658724998732091L;
	
	public enum Role {
		ADMIN, USER, SYSTEM_ADMIN, OWNER, PARENT, CHILD
	}
	
	public ClubRole(Role role) {
		this.role = role;
	}
	
	public ClubRole(Role role, User user, Club club) {
		this.role = role;
		this.user = user;
		this.club = club;
	}
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Club club;
	
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
		result = prime * result + ((club == null) ? 0 : club.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (club == null) {
			if (other.club != null)
				return false;
		} else if (!club.equals(other.club))
			return false;
		if (role != other.role)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
