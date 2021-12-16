package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.function.Function;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingTimeRequest;
import nu.borjessons.clubhouse.impl.service.TrainingTimeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clubs/{clubId}")
public class TrainingTimeController {
  private final TrainingTimeService trainingTimeService;
  private final Function<TrainingTimeRequest, TrainingTime> scheduleInputConverter;

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/teams/{teamId}/training-time")
  public Collection<TrainingTimeRecord> getSchedule(@PathVariable String teamId) {
    return trainingTimeService.getTrainingTimes(teamId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/teams/{teamId}/training-time")
  public TrainingTimeRecord createTrainingTime(@PathVariable String teamId, @Valid @RequestBody TrainingTimeRequest trainingTimeRequest) {
    return trainingTimeService.createTrainingTime(teamId, scheduleInputConverter.apply(trainingTimeRequest));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/teams/{teamId}/training-time/{trainingTimeId}")
  public TrainingTimeRecord updateTrainingTime(@PathVariable String teamId, @PathVariable String trainingTimeId,
      @Valid @RequestBody TrainingTimeRequest trainingTimeRequest) {
    return trainingTimeService.updateTrainingTime(teamId, scheduleInputConverter.apply(trainingTimeRequest));
  }
}
