package nu.borjessons.clubhouse.impl.data;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.converter.TeamPostIdConverter;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;

@Getter
@Setter
@Entity
@Table(name = "team_post", indexes = {@Index(name = "idx_teampost_teampostid", columnList = "teamPostId")})
public class TeamPost extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  @Setter(AccessLevel.PRIVATE)
  @Convert(converter = TeamPostIdConverter.class)
  @Column(nullable = false, unique = true, columnDefinition = "varchar(64)")
  private TeamPostId teamPostId;

  private boolean sticky;

  private String title;

  private String body;

  @ManyToOne
  private ClubUser clubUser;

  @ManyToOne(optional = false)
  private Team team;

  @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
  private List<TeamPostComment> teamPostComments;
}

