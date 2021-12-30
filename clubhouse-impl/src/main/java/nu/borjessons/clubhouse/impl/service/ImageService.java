package nu.borjessons.clubhouse.impl.service;

import org.springframework.web.multipart.MultipartFile;

import nu.borjessons.clubhouse.impl.data.key.ImageId;

public interface ImageService {
  byte[] getImage(ImageId imageId);

  ImageId createImage(MultipartFile multipartFile);
}
