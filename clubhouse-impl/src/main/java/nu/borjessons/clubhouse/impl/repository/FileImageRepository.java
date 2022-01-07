package nu.borjessons.clubhouse.impl.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.key.ImageId;

@Slf4j
@RequiredArgsConstructor
public class FileImageRepository implements ImageRepository {
  private final Path imageDirectory;

  @Override
  public File findImageById(ImageId imageId) {
    return imageDirectory.resolve(Paths.get(imageId.toString() + ".jpg")).toFile();
  }

  @Override
  public ImageId saveImage(MultipartFile multipartFile) throws IOException {
    UUID uuid = UUID.randomUUID();
    Path filename = Paths.get(uuid + ".jpg");

    log.info("filename: {}", filename);

    Path filepath = imageDirectory.resolve(filename);
    log.info("filepath: {}", filepath);

    multipartFile.transferTo(filepath);

    return new ImageId(uuid.toString());
  }
}
