package nu.borjessons.clubhouse.impl.data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.converter.TeamPostIdConverter;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;

@Getter
@Setter
@Entity
@Table(name = "team_post")
public class TeamPost extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter(AccessLevel.PRIVATE)
  @Convert(converter = TeamPostIdConverter.class)
  @Column(nullable = false, unique = true, columnDefinition = "varchar(64)")
  private TeamPostId teamPostId;

  private boolean sticky;

  @ManyToOne
  private ClubUser clubUser;

  private String title;

  private String body;
}
