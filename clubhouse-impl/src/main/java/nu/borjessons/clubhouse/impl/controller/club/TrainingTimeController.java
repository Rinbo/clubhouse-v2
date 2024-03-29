package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.function.Function;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  private final Function<TrainingTimeRequest, TrainingTime> scheduleInputConverter;
  private final TrainingTimeService trainingTimeService;

  @PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
  @PostMapping("/teams/{teamId}/training-time")
  public TrainingTimeRecord createTrainingTime(@PathVariable String clubId, @PathVariable String teamId,
      @Valid @RequestBody TrainingTimeRequest trainingTimeRequest) {
    return trainingTimeService.createTrainingTime(teamId, scheduleInputConverter.apply(trainingTimeRequest));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
  @DeleteMapping("/teams/{teamId}/training-time/{trainingTimeId}")
  public ResponseEntity<String> deleteTrainingTime(@PathVariable String clubId, @PathVariable String teamId, @PathVariable String trainingTimeId) {
    trainingTimeService.deleteTrainingTime(trainingTimeId);
    return ResponseEntity.ok(String.format("trainingTime with id %s was successfully deleted", trainingTimeId));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/teams/{teamId}/training-time")
  public Collection<TrainingTimeRecord> getSchedule(@PathVariable String clubId, @PathVariable String teamId) {
    return trainingTimeService.getTrainingTimes(teamId);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
  @PutMapping("/teams/{teamId}/training-time/{trainingTimeId}")
  public TrainingTimeRecord updateTrainingTime(@PathVariable String clubId, @PathVariable String teamId, @PathVariable String trainingTimeId,
      @Valid @RequestBody TrainingTimeRequest trainingTimeRequest) {
    return trainingTimeService.updateTrainingTime(trainingTimeId, scheduleInputConverter.apply(trainingTimeRequest));
  }
}
