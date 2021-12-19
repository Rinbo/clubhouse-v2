package nu.borjessons.clubhouse.impl.dto.rest;

import java.time.DayOfWeek;
import java.time.LocalTime;

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
  private DayOfWeek dayOfWeek;

  @NotNull
  private String location;

  @NotNull
  private LocalTime startTime;

  @NotNull
  private LocalTime endTime;
}
