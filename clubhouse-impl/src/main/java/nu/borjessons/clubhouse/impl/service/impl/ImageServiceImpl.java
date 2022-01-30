package nu.borjessons.clubhouse.impl.service.impl;

import java.io.IOException;

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
  public ImageTokenId createImage(MultipartFile multipartFile) {
    Validate.notNull(multipartFile, "multipartFile");

    try {
      return imageRepository.saveImage(multipartFile);
    } catch (IOException e) {
      throw new IllegalStateException("Could not save image");
    }
  }

  @Override
  public void deleteImage(ImageTokenId imageTokenId) {
    Validate.notNull(imageTokenId, "imageTokenId");

    ImageToken imageToken = imageTokenRepository.findByImageTokenId(imageTokenId).orElseThrow();
    deleteImageFile(imageToken);
    imageTokenRepository.delete(imageToken);
  }

  // TODO add metric for initiating logo save and one for success. Then provide metric with percentage of successful saves
  @Override
  @Transactional
  public ImageToken createClubLogo(String clubId, MultipartFile multipartFile) {
    Validate.notNull(clubId, "clubId");
    Validate.notNull(multipartFile, "multipartFile");

    Club club = clubRepository.findByClubId(clubId).orElseThrow();

    ImageToken existingLogo = club.getLogo();
    if (existingLogo != null) deleteImageFile(existingLogo);

    ImageToken imageToken = createImageToken(multipartFile);

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

    ImageToken imageToken = createImageToken(multipartFile);
    user.setProfileImage(imageToken);
    return userRepository.save(user).getProfileImage();
  }

  private ImageToken createImageToken(MultipartFile multipartFile) {
    ImageTokenId imageTokenId = createImage(multipartFile);

    ImageToken imageToken = new ImageToken(imageTokenId);
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
