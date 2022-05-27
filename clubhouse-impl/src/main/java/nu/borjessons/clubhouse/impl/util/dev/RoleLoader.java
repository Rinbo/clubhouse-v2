package nu.borjessons.clubhouse.impl.util.dev;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.repository.RoleRepository;

@Component
@Profile({"local"})
@Slf4j
@RequiredArgsConstructor
public final class RoleLoader {
  private final RoleRepository roleRepository;

  @PostConstruct
  void loadData() {
    EmbeddedDataUtil.loadRoles(roleRepository);
  }
}
