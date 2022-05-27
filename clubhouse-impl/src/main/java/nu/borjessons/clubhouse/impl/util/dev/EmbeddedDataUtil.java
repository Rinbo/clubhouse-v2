package nu.borjessons.clubhouse.impl.util.dev;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.repository.RoleRepository;

final class EmbeddedDataUtil {
  public static void loadRoles(RoleRepository roleRepository) {
    Collection<Role> existingRoleNames = roleRepository.findAll().stream().map(RoleEntity::getName).collect(Collectors.toSet());
    Collection<Role> allRoles = Arrays.asList(Role.values());
    Collection<Role> newRoles = allRoles.stream().filter(role -> !existingRoleNames.contains(role)).toList();
    roleRepository.saveAll(newRoles.stream().map(EmbeddedDataUtil::getRoleEntity).toList());
  }

  private static RoleEntity getRoleEntity(Role role) {
    final RoleEntity roleEntity = new RoleEntity();
    roleEntity.setName(role);
    return roleEntity;
  }

  private EmbeddedDataUtil() {
    throw new IllegalStateException("Utility class");
  }
}
