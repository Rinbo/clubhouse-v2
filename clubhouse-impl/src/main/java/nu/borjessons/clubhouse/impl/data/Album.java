package nu.borjessons.clubhouse.impl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.converter.AlbumIdConverter;
import nu.borjessons.clubhouse.impl.data.key.AlbumId;

@Entity
@Getter
@Setter
@ToString
@Table(name = "album")
public class Album {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter(AccessLevel.PRIVATE)
  @Convert(converter = AlbumIdConverter.class)
  @Column(nullable = false, unique = true, columnDefinition = "varchar(64)")
  private AlbumId albumId;

  @ManyToOne(optional = false)
  private Club club;

  @ManyToMany
  @ToString.Exclude
  private List<ImageToken> imageTokens = new ArrayList<>();

  @Column(nullable = false, columnDefinition = "varchar(64)")
  private String name;

  public Album() {
    albumId = new AlbumId(UUID.randomUUID().toString());
  }
}
