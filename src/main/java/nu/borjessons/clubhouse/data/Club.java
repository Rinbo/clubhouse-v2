package nu.borjessons.clubhouse.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "club")
public class Club extends BaseEntity  implements Serializable {

	public enum Type { SPORT, MUSIC, MISC }
	
	private static final long serialVersionUID = 799039294739280410L;
	
	public Club(String name, Type type) {
		this.name = name;
		this.path = name.toLowerCase().replace(" ", "-");
		this.type = type;
	}
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable=false, unique = true)
	private final String clubId = UUID.randomUUID().toString();
	
	@Column(nullable = false, length = 120, unique = true)
	private String name;
	
	@Column(nullable = false, length = 120, unique = true)
	private String path;
	
	@Column(nullable = false)
	private Type type;
	
	@OneToMany(mappedBy = "club", orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<ClubRole> clubRoles = new HashSet<>();
	
	@OneToMany(mappedBy = "club", orphanRemoval = true)
	private Set<Team> teams = new HashSet<>();
	
	public User getUser(String userId) {
		Optional<User> maybeUser =  getUsers().stream().filter(user -> user.getUserId().equals(userId)).findFirst();
		if (maybeUser.isPresent()) return maybeUser.get();
		Optional<User> maybeManagedUser =  getManagedUsers().stream().filter(managedUser -> managedUser.getUserId().equals(userId)).findFirst();
		if (maybeManagedUser.isPresent()) return maybeManagedUser.get();
		throw new UsernameNotFoundException(String.format("User with id %s is not present in club with id %s", userId, clubId));
	}
	
	public Set<User> getUsers() {
		return clubRoles.stream().map(ClubRole::getUser).collect(Collectors.toSet());
	}
	
	public Set<User> getManagedUsers() {
		return getUsers().stream().map(User::getChildren).flatMap(Set::stream).collect(Collectors.toSet());
	}
	
	public void addClubRole(ClubRole clubRole) {
		this.clubRoles.add(clubRole);
	}
	
	public void removeClubRole(ClubRole clubRole) {
		this.clubRoles.remove(clubRole);
	}
	
	public void addTeam(Team team) {
		teams.add(team);
		team.setClub(this);
	}
	
	public void removeTeam(Team team) {
		teams.remove(team);
		team.setClub(null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clubId == null) ? 0 : clubId.hashCode());
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
		Club other = (Club) obj;
		if (clubId == null) {
			if (other.clubId != null)
				return false;
		} else if (!clubId.equals(other.clubId))
			return false;
		return true;
	}
}
