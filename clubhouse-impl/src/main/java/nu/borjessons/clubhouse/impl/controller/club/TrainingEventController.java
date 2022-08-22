package nu.borjessons.clubhouse.impl.controller.club;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @PostMapping
  public TrainingEventRecord create(@PathVariable String clubId, @PathVariable String teamId,
      @RequestBody TrainingEventRequestModel trainingEventRequestModel) {
    return trainingEventService.create(clubId, teamId, trainingEventRequestModel);
  }
}
