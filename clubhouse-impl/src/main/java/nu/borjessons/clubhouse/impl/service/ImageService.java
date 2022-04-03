package nu.borjessons.clubhouse.impl.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;

public interface ImageService {
  ImageStream getImage(ImageTokenId imageTokenId) throws IOException;

  void deleteImage(ImageTokenId imageTokenId);

  ImageToken createClubLogo(String clubId, MultipartFile multipartFile);

  ImageToken createProfileImage(UserId userId, MultipartFile multipartFile);

  ImageToken createClubImage(String clubId, MultipartFile multipartFile);

  void createClubRootImageFolder(String clubId);
}
