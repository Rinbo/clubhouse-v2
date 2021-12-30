package nu.borjessons.clubhouse.impl.repository;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import nu.borjessons.clubhouse.impl.data.key.ImageId;

public interface ImageRepository {
  byte[] findImageById(ImageId imageId) throws IOException;

  ImageId saveImage(MultipartFile multipartFile) throws IOException;
}
