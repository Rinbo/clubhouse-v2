package nu.borjessons.clubhouse.impl.repository;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import nu.borjessons.clubhouse.impl.data.key.ImageId;

public interface ImageRepository {
  File findImageById(ImageId imageId);

  ImageId saveImage(MultipartFile multipartFile) throws IOException;
}
