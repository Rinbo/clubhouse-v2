package nu.borjessons.clubhouse.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "club_role")
public class ClubRole implements Serializable, GrantedAuthority {
	
	//public static final String ROLE_PREFIX = "ROLE_"; AUTHORITIES WORKS WITHOUT THIS????
	
	public enum Role {
		ADMIN, USER, SYSTEM_ADMIN, OWNER, PARENT, CHILD
	}

	private static final long serialVersionUID = 5386658724998732091L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable = false)
	private Role name;
	
	@ManyToOne
	@Column(nullable = false)
	private User user;
	
	@ManyToOne
	@Column(nullable = false)
	private Club club;

	@Override
	public String getAuthority() {
		return name.name();
	}

}
