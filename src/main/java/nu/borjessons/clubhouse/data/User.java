package nu.borjessons.clubhouse.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

	private static final long serialVersionUID = 2973075901622175140L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable = false, length = 120, unique = true)
	private String email;
	
	@Column(nullable = false, length = 50)
	private String firstname;

	@Column(nullable = false, length = 50)
	private String lastName;
	
	@Column(nullable = false)
	private String encryptedPassword;
	
	@OneToMany(mappedBy = "user", orphanRemoval = true)
	private Set<ClubRole> roles = new HashSet<>();
	
	private Club activeClub;
	
	public Set<String> getActiveRoles() {
		return roles.stream()
				.filter(clubRole -> clubRole.getClub().equals(activeClub))
				.map(clubRole -> clubRole.getName().name()).collect(Collectors.toSet());
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().filter(clubRole -> clubRole.getClub().equals(activeClub)).collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return encryptedPassword;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

}
