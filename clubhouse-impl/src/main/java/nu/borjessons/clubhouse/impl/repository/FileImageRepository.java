package nu.borjessons.clubhouse.impl.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;
import nu.borjessons.clubhouse.impl.util.FileUtils;
import nu.borjessons.clubhouse.impl.util.Validate;

@Slf4j
@RequiredArgsConstructor
public class FileImageRepository implements ImageRepository {

  private final Path imageDirectory;

  @Override
  public void createClubRootImageDirectory(Path path) {
    try {
      Files.createDirectories(imageDirectory.resolve(path));
    } catch (IOException e) {
      log.error("Could not create root club image directory {}", path, e);
    }
  }

  @Override
  public void deleteFoldersRecursively(Path path) {
    try {
      FileUtils.deleteDirectoryRecursively(path);
    } catch (IOException e) {
      log.error("failed to delete folders in path: " + path);
    }
  }

  @Override
  public void deleteImage(ImageToken imageToken) throws IOException {
    FileUtils.deleteDirectoryRecursively(
        imageDirectory
            .resolve(imageToken.getPath())
            .resolve(imageToken.getImageTokenId().toString()));
  }

  @Override
  public ImageStream findImageByImageToken(ImageToken imageToken) throws IOException {
    Validate.notNull(imageToken, "imageToken");

    ImageTokenId imageTokenId = imageToken.getImageTokenId();
    Path relativePath = imageToken.getPath().resolve(Paths.get(imageTokenId.toString(), imageToken.getName()));
    Path path = imageDirectory.resolve(relativePath);
    return new ImageStream(imageToken, Files.newInputStream(path));
  }

  @Override
  public List<ImageTokenId> getFilePathsInFolder(Path path) throws IOException {
    try (Stream<Path> stream = Files.walk(path)) {
      return stream.sorted(Comparator.reverseOrder())
          .filter(Files::isRegularFile)
          .map(Path::getParent)
          .map(Path::getFileName)
          .map(Path::toString)
          .map(ImageTokenId::new)
          .toList();
    }
  }

  @Override
  public ImageTokenId saveImage(MultipartFile multipartFile, Path path) throws IOException {
    Validate.notNull(multipartFile, "multipartFile");

    String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename(), "originalFilename must not be null");
    String imageId = UUID.randomUUID().toString();

    Path absolutPath = imageDirectory
        .resolve(path)
        .resolve(imageId);

    Files.createDirectories(absolutPath);

    log.info("absolutPath: {}", absolutPath);
    multipartFile.transferTo(absolutPath.resolve(fileName));

    return new ImageTokenId(imageId);
  }
}
