package nu.borjessons.clubhouse.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
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
	
	@OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ClubRole> roles = new ArrayList<>();
	
	@OneToOne
	private Club activeClub;
	
	@OneToMany(mappedBy = "id", orphanRemoval = true)
	private List<User> parents = new ArrayList<>();
	
	@OneToMany(mappedBy = "id", orphanRemoval = true)
	private List<User> children = new ArrayList<>();
	
	public Set<String> getActiveRoles() {
		return roles.stream()
				.filter(clubRole -> clubRole.getClub().getId() == activeClub.getId())
				.map(clubRole -> clubRole.getRole().name()).collect(Collectors.toSet());
	}
	
	public void addClubRole(ClubRole clubRole) {
		roles.add(clubRole);
		clubRole.setUser(this);
	}
	
	public void removeClubRole(ClubRole clubRole) {
		roles.remove(clubRole);
		clubRole.setUser(null);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().filter(clubRole -> clubRole.getClub().getId() == activeClub.getId()).collect(Collectors.toSet());
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
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
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
		User other = (User) obj;
		return (id != other.id);
	}
}
