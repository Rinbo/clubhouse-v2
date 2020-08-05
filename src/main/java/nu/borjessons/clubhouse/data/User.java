package nu.borjessons.clubhouse.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nu.borjessons.clubhouse.data.ClubRole.Role;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User extends BaseEntity implements UserDetails, Serializable {

	private static final long serialVersionUID = 2973075901622175140L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable = false, unique = true)
	private String userId = UUID.randomUUID().toString();
	
	@Column(nullable = false, length = 120, unique = true)
	private String email;
	
	@Column(nullable = false, length = 50)
	private String firstName;

	@Column(nullable = false, length = 50)
	private String lastName;
	
	@Column(nullable = false)
	private String encryptedPassword;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<ClubRole> roles = new HashSet<>();
	
	@OneToOne
	private Club activeClub;
	
	@Column(nullable = false)
	private LocalDate dateOfBirth;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Address> addresses = new HashSet<>();
	
	private boolean showAddress;
	
	private LocalDateTime lastLoginTime;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private Set<User> parents = new HashSet<>();
	
	@ManyToMany(mappedBy = "parents", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private Set<User> children = new HashSet<>();
	
	public Set<String> getActiveRoles() {
		return roles.stream()
				.filter(clubRole -> clubRole.getClub().getId() == activeClub.getId())
				.map(clubRole -> clubRole.getRole().name()).collect(Collectors.toSet());
	}
	
	public Set<String> getRolesForClub(String clubId) {
		return roles.stream()
				.filter(clubRole -> clubRole.getClub().getClubId().equals(clubId))
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
	
	public void addAddress(Address address) {
		addresses.add(address);
		address.setUser(this);
	}
	
	public void removeAddress(Address address) {
		addresses.remove(address);
		address.setUser(null);
	}
	
	public void addParent(User parent) {
		parents.add(parent);
		parent.addChild(this);
	}
	
	public void removeParent(User parent) {
		parents.remove(parent);
		parent.removeChild(this);
	}
	
	public void addChild(User child) {
		children.add(child);
	}
	
	public void removeChild(User child) {
		children.add(child);
	}
	
	public Set<Address> getAddresses() {
		if (getActiveRoles().contains(Role.CHILD.name())) return parents.stream().map(User::getAddresses).flatMap(Set::stream).collect(Collectors.toSet());
		return addresses;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().filter(clubRole -> clubRole.getClub().getId() == activeClub.getId()).collect(Collectors.toSet());
	}
	
	public Club getActiveClub() {
		if (activeClub != null) return activeClub;
		throw new IllegalStateException(String.format("User with id %s does not have an active club set", userId));
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
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
}
