package nu.borjessons.clubhouse.impl.dto.rest;

import java.time.Duration;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TrainingTimeRequest {
  @NotNull
  private int dayOfWeek;

  @NotNull
  private Duration duration;

  @NotNull
  private String location;
}
