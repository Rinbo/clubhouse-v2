package nu.borjessons.clubhouse.impl.service.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.ImageRepository;
import nu.borjessons.clubhouse.impl.repository.ImageTokenRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.ImageService;
import nu.borjessons.clubhouse.impl.util.Validate;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
  public static final Path EMPTY_PATH = Path.of("");
  private static final String MULTIPART_FILE_STRING = "multipartFile";

  private final ClubRepository clubRepository;
  private final ImageTokenRepository imageTokenRepository;
  private final ImageRepository imageRepository;
  private final UserRepository userRepository;

  @Override
  public ImageStream getImage(ImageTokenId imageTokenId) throws IOException {
    Validate.notNull(imageTokenId, "imageTokenId");

    ImageToken imageToken = imageTokenRepository.findByImageTokenId(imageTokenId).orElseThrow();
    return imageRepository.findImageByImageToken(imageToken);
  }

  @Override
  public void deleteImage(ImageTokenId imageTokenId) {
    Validate.notNull(imageTokenId, "imageTokenId");

    ImageToken imageToken = imageTokenRepository.findByImageTokenId(imageTokenId).orElseThrow();
    deleteImageFile(imageToken);
    imageTokenRepository.delete(imageToken);
  }

  @Override
  @Transactional
  public ImageToken createClubLogo(String clubId, MultipartFile multipartFile) {
    Validate.notNull(clubId, "clubId");
    Validate.notNull(multipartFile, "multipartFile");

    Club club = clubRepository.findByClubId(clubId).orElseThrow();

    ImageToken existingLogo = club.getLogo();
    if (existingLogo != null) deleteImageFile(existingLogo);

    ImageToken imageToken = createImageToken(multipartFile, Paths.get("clubs", clubId, "logo"));

    club.setLogo(imageToken);
    return clubRepository.save(club).getLogo();
  }

  @Override
  @Transactional
  public ImageToken createProfileImage(UserId userId, MultipartFile multipartFile) {
    Validate.notNull(userId, "userId");
    Validate.notNull(multipartFile, "multipartFile");

    User user = userRepository.findByUserId(userId).orElseThrow();

    ImageToken existingProfileImage = user.getProfileImage();
    if (existingProfileImage != null) deleteImageFile(existingProfileImage);

    ImageToken imageToken = createImageToken(multipartFile, Path.of("profile images"));
    user.setProfileImage(imageToken);
    return userRepository.save(user).getProfileImage();
  }

  private ImageTokenId saveImage(MultipartFile multipartFile, Path path) {
    Validate.notNull(multipartFile, MULTIPART_FILE_STRING);

    try {
      return imageRepository.saveImage(multipartFile, path);
    } catch (IOException e) {
      throw new IllegalStateException("Could not save image");
    }
  }

  private ImageToken createImageToken(MultipartFile multipartFile, Path path) {
    ImageTokenId imageTokenId = saveImage(multipartFile, path);

    ImageToken imageToken = new ImageToken(imageTokenId, path);
    imageToken.setContentType(multipartFile.getContentType());
    imageToken.setName(multipartFile.getOriginalFilename());
    return imageToken;
  }

  private void deleteImageFile(ImageToken imageToken) {
    try {
      imageRepository.deleteImage(imageToken);
    } catch (IOException e) {
      throw new IllegalStateException("Could not delete image: " + imageToken, e);
    }
  }
}
