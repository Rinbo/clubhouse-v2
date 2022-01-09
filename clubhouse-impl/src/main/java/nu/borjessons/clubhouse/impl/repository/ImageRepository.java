package nu.borjessons.clubhouse.impl.repository;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;

public interface ImageRepository {
  ImageStream findImageByImageToken(ImageToken imageToken) throws IOException;

  ImageTokenId saveImage(MultipartFile multipartFile) throws IOException;
}
