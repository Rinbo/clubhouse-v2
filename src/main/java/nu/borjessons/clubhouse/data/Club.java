package nu.borjessons.clubhouse.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class Club implements Serializable {

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
	
	@Column(nullable = false, length = 120, unique = true)
	private String name;
	
	@Column(nullable = false, length = 120, unique = true)
	private String path;
	
	@Column(nullable = false)
	private Type type;
	
	@OneToMany(mappedBy = "club", orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ClubRole> clubRoles = new ArrayList<>();
	
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
		Club other = (Club) obj;
		return (id != other.id);
	}
}
