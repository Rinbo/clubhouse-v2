package nu.borjessons.clubhouse.impl.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;
import nu.borjessons.clubhouse.impl.service.ImageService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ImageController {
  private static byte[] readBytes(InputStream inputStream) throws IOException {
    try (inputStream) {
      return inputStream.readAllBytes();
    }
  }

  private final ImageService imageService;

  @GetMapping(value = "/images/{imageId}")
  public ResponseEntity<byte[]> getImage(@PathVariable ImageTokenId imageTokenId) throws IOException {
    ImageStream imageStream = imageService.getImage(imageTokenId);
    ImageToken imageToken = imageStream.imageToken();
    MediaType mediaType = MediaType.valueOf(imageToken.getContentType());
    return ResponseEntity.ok().contentType(mediaType).body(readBytes(imageStream.inputStream()));
  }

  // TODO limit file size
  @PostMapping(value = "/clubs/{clubId}/upload-logo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageTokenId> uploadClubLogo(@PathVariable String clubId, @RequestParam(value = "file") MultipartFile multipartFile) {
    log.info("multipartFile: {}", multipartFile);
    ImageToken imageToken = imageService.createClubLogo(clubId, multipartFile);
    return ResponseEntity.ok().contentType(MediaType.valueOf(imageToken.getContentType())).body(imageToken.getImageTokenId());
  }

  // TODO Do I need this? For albums I guess. As a first step I want to add images to news items or club description. How do I upload several images?
  @PostMapping(value = "/clubs/{clubId}/upload-images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageTokenId> uploadClubImages(@PathVariable String clubId, @RequestParam(value = "file") List<MultipartFile> multipartFiles) {
    log.info("multipartFiles: {}", multipartFiles);
    // List<ImageId> imageIds = imageService.createClubImages(clubId, multipartFiles);
    return null;
  }

  // TODO figure out some smart resource authorization filter to allow for user and it's children
  @PostMapping(value = "/users/{userId}/upload-profile-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageTokenId> uploadProfileImage(@AuthenticationPrincipal User user, @PathVariable UserId userId,
      @RequestParam(value = "file") MultipartFile multipartFile) {
    log.info("multipartFile: {}", multipartFile);
    // ImageId imageId = imageService.createProfileImage(userId, multipartFile);
    return ResponseEntity.ok(null);
  }
}



