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
  public static final String LOGO_ROOT_FOLDER_NAME = "logo";
  private static final String CLUBS_ROOT_FOLDER_NAME = "clubs";
  private static final String IMAGE_TOKEN_ID_PARAMETER_NAME = "imageTokenId";
  private static final String MULTIPART_FILE_PARAMETER_NAME = "multipartFile";
  private static final String MULTIPART_FILE_STRING = ImageServiceImpl.MULTIPART_FILE_PARAMETER_NAME;
  private static final String PROFILE_IMAGES_ROOT_FOLDER_NAME = "profile images";
  private final ClubRepository clubRepository;
  private final ImageTokenRepository imageTokenRepository;
  private final ImageRepository imageRepository;
  private final UserRepository userRepository;

  @Override
  public ImageStream getImage(ImageTokenId imageTokenId) throws IOException {
    Validate.notNull(imageTokenId, IMAGE_TOKEN_ID_PARAMETER_NAME);

    ImageToken imageToken = imageTokenRepository.findByImageTokenId(imageTokenId).orElseThrow();
    return imageRepository.findImageByImageToken(imageToken);
  }

  @Override
  public void deleteImage(ImageTokenId imageTokenId) {
    Validate.notNull(imageTokenId, IMAGE_TOKEN_ID_PARAMETER_NAME);

    ImageToken imageToken = imageTokenRepository.findByImageTokenId(imageTokenId).orElseThrow();
    deleteImageFile(imageToken);
    imageTokenRepository.delete(imageToken);
  }

  @Override
  @Transactional
  public ImageToken createClubLogo(String clubId, MultipartFile multipartFile) {
    Validate.notNull(clubId, "clubId");
    Validate.notNull(multipartFile, MULTIPART_FILE_PARAMETER_NAME);

    Club club = clubRepository.findByClubId(clubId).orElseThrow();

    ImageToken existingLogo = club.getLogo();
    if (existingLogo != null) deleteImageFile(existingLogo);

    ImageToken imageToken = createImageToken(multipartFile, Paths.get(CLUBS_ROOT_FOLDER_NAME, clubId, LOGO_ROOT_FOLDER_NAME));

    club.setLogo(imageToken);
    return clubRepository.save(club).getLogo();
  }

  @Override
  @Transactional
  public ImageToken createProfileImage(UserId userId, MultipartFile multipartFile) {
    Validate.notNull(userId, "userId");
    Validate.notNull(multipartFile, ImageServiceImpl.MULTIPART_FILE_PARAMETER_NAME);

    User user = userRepository.findByUserId(userId).orElseThrow();

    ImageToken existingProfileImage = user.getImageToken();
    if (existingProfileImage != null) deleteImageFile(existingProfileImage);

    ImageToken imageToken = createImageToken(multipartFile, Path.of(PROFILE_IMAGES_ROOT_FOLDER_NAME));
    user.setImageToken(imageToken);
    return userRepository.save(user).getImageToken();
  }

  @Override
  public ImageToken createClubImage(String clubId, MultipartFile multipartFile) {
    return imageTokenRepository.save(createImageToken(multipartFile, Paths.get(CLUBS_ROOT_FOLDER_NAME, clubId)));
  }

  @Override
  public void createClubRootImageFolder(String clubId) {
    imageRepository.createClubRootImageDirectory(Paths.get(CLUBS_ROOT_FOLDER_NAME, clubId));
  }

  private ImageTokenId saveImage(MultipartFile multipartFile, Path path) {
    Validate.notNull(multipartFile, MULTIPART_FILE_STRING);

    try {
      return imageRepository.saveImage(multipartFile, path);
    } catch (IOException e) {
      throw new IllegalStateException("Could not save image", e);
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
