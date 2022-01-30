package nu.borjessons.clubhouse.impl.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {
  public static void deleteDirectoryRecursively(Path baseDirectory) throws IOException {
    try (Stream<Path> walk = Files.walk(baseDirectory)) {
      walk.sorted(Comparator.reverseOrder())
          .forEach(FileUtils::deleteFile);
    }
  }

  private static void deleteFile(Path path) {
    try {
      Files.delete(path);
    } catch (IOException e) {
      log.error("Failed to delete file {}", path, e);
    }
  }
}
