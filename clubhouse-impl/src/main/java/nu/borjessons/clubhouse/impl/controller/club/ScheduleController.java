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
import nu.borjessons.clubhouse.impl.dto.ScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ScheduleRequest;
import nu.borjessons.clubhouse.impl.service.ScheduleService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clubs/{clubId}")
public class ScheduleController {
  private final ScheduleService scheduleService;

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/teams/{teamId}/schedule")
  public ScheduleRecord getSchedule(@PathVariable String teamId) {
    return scheduleService.getSchedule(teamId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/teams/{teamId}/schedule")
  public ScheduleRecord createSchedule(@PathVariable String teamId, @Valid @RequestBody ScheduleRequest scheduleRequest) {
    return scheduleService.createSchedule(teamId, scheduleRequest);
  }
}
