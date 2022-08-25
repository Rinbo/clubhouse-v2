package nu.borjessons.clubhouse.impl.controller.club;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;
import nu.borjessons.clubhouse.impl.service.TrainingEventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs/{clubId}/teams/{teamId}/training-events")
public class TrainingEventController {
  private final TrainingEventService trainingEventService;

  @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
  @PostMapping
  public TrainingEventRecord create(@PathVariable String clubId, @PathVariable String teamId,
      @RequestBody TrainingEventRequestModel trainingEventRequestModel) {
    return trainingEventService.create(clubId, teamId, trainingEventRequestModel);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
  @DeleteMapping("/{trainingEventId}")
  public ResponseEntity<String> delete(@PathVariable String clubId, @PathVariable String teamId, @PathVariable long trainingEventId) {
    trainingEventService.delete(trainingEventId);
    return ResponseEntity.ok("TrainingEvent was deleted");
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{trainingEventId}")
  public TrainingEventRecord get(@PathVariable String clubId, @PathVariable String teamId, @PathVariable long trainingEventId) {
    return trainingEventService.get(trainingEventId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping
  public List<TrainingEventRecord> getAll(@PathVariable String clubId, @PathVariable String teamId, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return trainingEventService.get(teamId, PageRequest.of(page, size, Sort.by("dateTime").descending()));
  }

  @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
  @PutMapping("/{trainingEventId}")
  public TrainingEventRecord update(@PathVariable String clubId, @PathVariable String teamId, @PathVariable long trainingEventId,
      @RequestBody TrainingEventRequestModel trainingEventRequestModel) {
    return trainingEventService.update(clubId, trainingEventId, trainingEventRequestModel);
  }
}
