package nu.borjessons.clubhouse.impl.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "team_post_comment")
public class TeamPostComment extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String comment;

  @ManyToOne(optional = false)
  private TeamPost teamPost;

  @ManyToOne
  private ClubUser clubUser;
}
