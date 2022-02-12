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

  @Column(nullable = false)
  private String body;

  // TODO What happens if an "author" is deleted? It will fail. The field must be nullified first. Try it for good measure
  @ManyToOne(optional = false)
  private ClubUser author;

  // TODO What if the last person to update and the author are different? Complex. I will have to explicitly define the referenced relationship
  // What about the base user. What happens if he is deleted. Is there going to be a multi cascade event?
  @ManyToOne(optional = false)
  private ClubUser lastUpdatedBy;

  @ManyToOne(optional = false)
  private Club club;

  private boolean showAuthor;
}
