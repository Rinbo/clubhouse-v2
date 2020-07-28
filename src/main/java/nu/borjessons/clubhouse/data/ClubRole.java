package nu.borjessons.clubhouse.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "club_role")
public class ClubRole implements Serializable, GrantedAuthority {
	
	//public static final String ROLE_PREFIX = "ROLE_"; AUTHORITIES WORKS WITHOUT THIS????

	private static final long serialVersionUID = 5386658724998732091L;
	
	public enum Role {
		ADMIN, USER, SYSTEM_ADMIN, OWNER, PARENT, CHILD
	}
	
	public ClubRole(Role role) {
		this.role = role;
	}
	
	@Id
	@GeneratedValue
	@Getter @Setter private long id;
	
	@Column(nullable = false)
	@Getter @Setter private Role role;
	
	@ManyToOne
	@Getter @Setter private User user;
	
	@ManyToOne
	@Getter @Setter private Club club;

	@Override
	public String getAuthority() {
		return role.name();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		return id == other.id;
	}

}
