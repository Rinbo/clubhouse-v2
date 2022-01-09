package nu.borjessons.clubhouse.impl.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.ImageRepository;
import nu.borjessons.clubhouse.impl.repository.ImageTokenRepository;
import nu.borjessons.clubhouse.impl.service.ImageService;
import nu.borjessons.clubhouse.impl.util.Validate;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
  private final ClubRepository clubRepository;
  private final ImageTokenRepository imageTokenRepository;
  private final ImageRepository imageRepository;

  @Override
  public ImageStream getImage(ImageTokenId imageTokenId) throws IOException {
    Validate.notNull(imageTokenId, "imageTokenId");

    ImageToken imageToken = imageTokenRepository.findByImageTokenId(imageTokenId.toString()).orElseThrow();
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
  @Transactional
  public ImageToken createClubLogo(String clubId, MultipartFile multipartFile) {
    Validate.notNull(clubId, "clubId");
    Validate.notNull(multipartFile, "multipartFile");

    Club club = clubRepository.findByClubId(clubId).orElseThrow();

    try {
      ImageTokenId imageTokenId = imageRepository.saveImage(multipartFile);

      // TODO delete existing image before saving new token
      club.setLogoId(imageTokenId);
      clubRepository.save(club);

      ImageToken imageToken = new ImageToken(imageTokenId);
      imageToken.setContentType(multipartFile.getContentType());
      imageToken.setName(multipartFile.getOriginalFilename());

      return imageTokenRepository.save(imageToken);
    } catch (IOException e) {
      throw new IllegalStateException("Could not save image");
    }
  }
}
