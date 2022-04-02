package nu.borjessons.clubhouse.impl.repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;

public interface ImageRepository {
  ImageStream findImageByImageToken(ImageToken imageToken) throws IOException;

  List<Path> getClubImagePaths(Path path) throws IOException;

  ImageTokenId saveImage(MultipartFile multipartFile, Path path) throws IOException;

  void deleteImage(ImageToken imageToken) throws IOException;
}
