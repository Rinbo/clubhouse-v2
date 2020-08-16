package nu.borjessons.clubhouse.dto;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.Club.Type;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClubDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String path;
	private Type type;
	
	public ClubDTO(Club club) {
		this.name = club.getName();
		this.path = club.getPath();
		this.type = club.getType();
	}

}
