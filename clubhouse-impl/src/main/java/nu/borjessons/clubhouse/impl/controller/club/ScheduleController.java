package nu.borjessons.clubhouse.impl.controller.club;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.ClubScheduleRecord;
import nu.borjessons.clubhouse.impl.service.ScheduleService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clubs/{clubId}")
public class ScheduleController {
  private static void validateDates(LocalDate startDate, LocalDate endDate) {
    if (startDate.isAfter(endDate)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate must come before endDate");
    if (startDate.plus(32, ChronoUnit.DAYS).isAfter(endDate)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date range cannot exceed one month");
  }

  private final ScheduleService scheduleService;

  @GetMapping("/schedule")
  public Collection<ClubScheduleRecord> getClubSchedule(@PathVariable String clubId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate endDate) {
    validateDates(startDate, endDate);
    return scheduleService.getClubSchedule(clubId, startDate, endDate);
  }

  @GetMapping("/my-schedule")
  public Collection<ClubScheduleRecord> getMyClubSchedule(@AuthenticationPrincipal User principal, @PathVariable String clubId,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate startDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate endDate) {
    return scheduleService.getUserClubSchedule(principal.getUserId(), clubId, startDate, endDate);
  }
}
