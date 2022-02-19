package nu.borjessons.clubhouse.impl.data;

import java.util.UUID;

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
import nu.borjessons.clubhouse.impl.data.converter.AnnouncementIdConverter;
import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;

@Getter
@Setter
@Entity
@Table(name = "announcement")
public class Announcement extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Setter(AccessLevel.PRIVATE)
  @Convert(converter = AnnouncementIdConverter.class)
  @Column(name = "announcement_id", nullable = false, unique = true, columnDefinition = "varchar(255)")
  private AnnouncementId announcementId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String body;

  @ManyToOne
  private ClubUser author;

  @ManyToOne(optional = false)
  private Club club;
  private boolean showAuthor;

  public Announcement() {
    announcementId = new AnnouncementId(UUID.randomUUID().toString());
  }
}
