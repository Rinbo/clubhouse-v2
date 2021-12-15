package nu.borjessons.clubhouse.impl.controller.club;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.service.TrainingTimeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clubs/{clubId}")
public class TrainingTimeController {
  private final TrainingTimeService trainingTimeService;

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/teams/{teamId}/training-time")
  public TrainingTimeRecord getSchedule(@PathVariable String teamId) {
    return null;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/teams/{teamId}/training-time")
  public TrainingTimeRecord createSchedule(@PathVariable String teamId, @Valid @RequestBody ScheduleRequest scheduleRequest) {
    return null;
  }
}
