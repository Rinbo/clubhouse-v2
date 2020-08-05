package nu.borjessons.clubhouse.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "club")
public class Club extends BaseEntity  implements Serializable {

	public enum Type { SPORT, MUSIC, MISC }
	
	private static final long serialVersionUID = 799039294739280410L;
	
	public Club(@NonNull String name, @NonNull Type type) {
		this.name = name;
		this.path = name.toLowerCase().replace(" ", "-");
		this.type = type;
	}
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable=false, unique = true)
	private String clubId = UUID.randomUUID().toString();
	
	@Column(nullable = false, length = 120, unique = true)
	private String name;
	
	@Column(nullable = false, length = 120, unique = true)
	private String path;
	
	@Column(nullable = false)
	private Type type;
	
	@OneToMany(mappedBy = "club", orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<ClubRole> clubRoles = new HashSet<>();
	
	public User getUser(String userId) {
		Optional<ClubRole> maybeClubRole =  clubRoles.stream().filter(clubRole -> clubRole.getUser().getUserId().equals(userId)).findFirst();
		if (maybeClubRole.isPresent()) return maybeClubRole.get().getUser();
		throw new IllegalArgumentException(String.format("User with id %s is not present in club with id %s", userId, clubId));
	}
	
	public void addClubRole(ClubRole clubRole) {
		this.clubRoles.add(clubRole);
		clubRole.setClub(this);
	}
	
	public void removeClubRole(ClubRole clubRole) {
		this.clubRoles.remove(clubRole);
		clubRole.setClub(null);
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
