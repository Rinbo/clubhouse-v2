package nu.borjessons.clubhouse.impl.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;

public interface ImageService {
  ImageToken createClubImage(String clubId, MultipartFile multipartFile);

  ImageToken createClubLogo(String clubId, MultipartFile multipartFile);

  void createClubRootImageFolder(String clubId);

  ImageToken createProfileImage(UserId userId, MultipartFile multipartFile);

  void deleteAllClubImages(String clubId) throws IOException;

  void deleteImage(ImageTokenId imageTokenId);

  void deleteImage(ImageToken imageToken);

  List<ImageTokenId> getClubImagePaths(String clubId);

  ImageStream getImage(ImageTokenId imageTokenId) throws IOException;
}
