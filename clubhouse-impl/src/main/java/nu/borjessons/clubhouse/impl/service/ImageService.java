package nu.borjessons.clubhouse.impl.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import nu.borjessons.clubhouse.impl.data.key.ImageId;

public interface ImageService {
  File getImage(ImageId imageId);

  ImageId createImage(MultipartFile multipartFile);
}
