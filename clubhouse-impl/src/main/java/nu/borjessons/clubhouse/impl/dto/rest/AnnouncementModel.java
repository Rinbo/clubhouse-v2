package nu.borjessons.clubhouse.impl.dto.rest;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.util.Validate;

@Getter
@Setter
@NoArgsConstructor
public final class AnnouncementModel {
  @NotNull(message = "Title cannot be null")
  private String title;
  @NotNull(message = "Body cannot be null")
  private String body;

  public AnnouncementModel(String title, String body) {
    Validate.notEmpty(title, "title");
    Validate.notEmpty(body, "body");

    this.title = title;
    this.body = body;
  }
}
