package nu.borjessons.clubhouse.impl.service.impl;

import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingTime;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingTimeRequest;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.repository.TrainingTimeRepository;
import nu.borjessons.clubhouse.impl.service.TrainingTimeService;

@RequiredArgsConstructor
@Service
public class TrainingTimeServiceImpl implements TrainingTimeService {
  private final TrainingTimeRepository scheduleRepository;
  private final TeamRepository teamRepository;
  private final BiFunction<TrainingTimeRequest, Team, TrainingTime> scheduleInputConverter;

}
