package nu.borjessons.clubhouse.impl.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;
import nu.borjessons.clubhouse.impl.util.Validate;

@Slf4j
@RequiredArgsConstructor
public class FileImageRepository implements ImageRepository {
  private final Path imageDirectory;

  @Override
  public ImageStream findImageByImageToken(ImageToken imageToken) throws IOException {
    Validate.notNull(imageToken, "imageToken");

    ImageTokenId imageTokenId = imageToken.getImageTokenId();
    Path path = imageDirectory.resolve(Paths.get(imageTokenId.toString(), imageToken.getName()));
    return new ImageStream(imageToken, Files.newInputStream(path));
  }

  @Override
  public ImageTokenId saveImage(MultipartFile multipartFile) throws IOException {
    Validate.notNull(multipartFile, "multipartFile");

    String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename(), "originalFilename must not be null");
    String imageId = UUID.randomUUID().toString();

    Path absolutPath = imageDirectory.resolve(imageId);
    Files.createDirectories(absolutPath);

    log.info("absolutPath: {}", absolutPath);
    multipartFile.transferTo(absolutPath.resolve(fileName));

    return new ImageTokenId(imageId);
  }
}
