package nu.borjessons.clubhouse.impl.service.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.key.ImageId;
import nu.borjessons.clubhouse.impl.repository.ImageRepository;
import nu.borjessons.clubhouse.impl.service.ImageService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
  private final ImageRepository imageRepository;

  @Override
  public File getImage(ImageId imageId) {
    return imageRepository.findImageById(imageId);
  }

  @Override
  public ImageId createImage(MultipartFile multipartFile) {
    log.info("bytes: {}", multipartFile);
    try {
      return imageRepository.saveImage(multipartFile);
    } catch (IOException e) {
      throw new IllegalStateException("Could not save image");
    }
  }
}
