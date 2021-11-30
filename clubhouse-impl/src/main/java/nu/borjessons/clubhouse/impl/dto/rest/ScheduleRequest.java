package nu.borjessons.clubhouse.impl.dto.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ScheduleRequest {
  @NotNull
  private LocalDate periodStart;

  @NotNull
  private LocalDate periodEnd;

  @NotEmpty
  private List<TrainingTimeRequest> trainingTimes = new ArrayList<>();
}
