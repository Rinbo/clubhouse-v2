package nu.borjessons.clubhouse.impl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
  @Column(nullable = false, columnDefinition = "TEXT")
  private String body;

  @ManyToOne
  private ClubUser clubUser;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private boolean sticky;

  @ManyToOne(optional = false)
  private Team team;

  @OneToMany(mappedBy = "teamPost", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
  private List<TeamPostComment> teamPostComments = new ArrayList<>();

  @Setter(AccessLevel.PRIVATE)
  @Convert(converter = TeamPostIdConverter.class)
  @Column(nullable = false, unique = true, columnDefinition = "varchar(64)")
  private TeamPostId teamPostId;

  private String title;

  public TeamPost() {
    teamPostId = new TeamPostId(UUID.randomUUID().toString());
  }

  public void addComment(TeamPostComment teamPostComment) {
    teamPostComment.setTeamPost(this);
    this.teamPostComments.add(teamPostComment);
  }
}

