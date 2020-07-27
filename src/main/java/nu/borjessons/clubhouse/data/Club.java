package nu.borjessons.clubhouse.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "club")
public class Club implements Serializable {

	public enum Type { SPORT, MUSIC, MISC };
	
	private static final long serialVersionUID = 799039294739280410L;
	
	public Club(String name, Set<ClubRole> clubRoles) {
		this.name = name;
		this.path = name.toLowerCase().replace(" ", "-");
		this.clubRoles = clubRoles;
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
	
	@OneToMany(mappedBy = "club", orphanRemoval = true)
	private Set<ClubRole> clubRoles = new HashSet<>();

}
