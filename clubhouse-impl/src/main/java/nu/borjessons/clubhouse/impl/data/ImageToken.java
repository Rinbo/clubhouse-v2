package nu.borjessons.clubhouse.impl.data;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.converter.ImageTokenIdConverter;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.util.Validate;

@Entity
@Getter
@Setter
@ToString
@Table(name = "image_token", indexes = @Index(name = "ix_image_token_id", columnList = "imageTokenId"))
public class ImageToken extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Setter(AccessLevel.PRIVATE)
  @Convert(converter = ImageTokenIdConverter.class)
  @Column(nullable = false, unique = true, columnDefinition = "varchar(64)")
  private ImageTokenId imageTokenId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String contentType;

  public ImageToken(ImageTokenId imageTokenId) {
    Validate.notNull(imageTokenId, "imageTokenId");

    this.imageTokenId = imageTokenId;
  }

  public ImageToken() {
    this.imageTokenId = new ImageTokenId(UUID.randomUUID().toString());
  }
}
