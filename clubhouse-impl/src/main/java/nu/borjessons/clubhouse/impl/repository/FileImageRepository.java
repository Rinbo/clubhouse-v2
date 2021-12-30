package nu.borjessons.clubhouse.impl.repository;

import java.io.IOException;
import java.nio.file.Files;
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
  public byte[] findImageById(ImageId imageId) throws IOException {
    Path path = imageDirectory.resolve(Paths.get(imageId.toString()));
    return Files.readAllBytes(path);
  }

  @Override
  public ImageId saveImage(MultipartFile multipartFile) throws IOException {
    Path filename = Paths.get(UUID.randomUUID() + ".jpg");

    log.info("filename: {}", filename);

    Path filepath = imageDirectory.resolve(filename);
    log.info("filepath: {}", filepath);

    multipartFile.transferTo(filepath);
    //Files.write(filepath, multipartFile.getBytes(), StandardOpenOption.APPEND);

    return new ImageId(filename.toString());
  }
}
