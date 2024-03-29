package nu.borjessons.clubhouse.impl.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.AppUserDetails;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;
import nu.borjessons.clubhouse.impl.security.resource.authorization.UserResourceAuthorization;
import nu.borjessons.clubhouse.impl.service.ImageService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ImageController {
  public static final int MAX_FILE_SIZE_BYTES = 2_000_000;

  private static CacheControl createCacheControlHeader() {
    return CacheControl.maxAge(24, TimeUnit.HOURS)
        .noTransform()
        .mustRevalidate();
  }

  private static byte[] readBytes(InputStream inputStream) throws IOException {
    try (inputStream) {
      return inputStream.readAllBytes();
    }
  }

  private static void validateFileSize(MultipartFile multipartFile) {
    if (multipartFile.getSize() > MAX_FILE_SIZE_BYTES)
      throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, String.format(Locale.ROOT, "Size cannot exceed %d bytes", MAX_FILE_SIZE_BYTES));
  }

  private final ImageService imageService;
  private final UserResourceAuthorization userResourceAuthorization;

  @GetMapping(value = "/images/{imageTokenId}")
  public ResponseEntity<byte[]> getImage(@PathVariable String imageTokenId) throws IOException {
    ImageStream imageStream = imageService.getImage(new ImageTokenId(imageTokenId));
    ImageToken imageToken = imageStream.imageToken();
    MediaType mediaType = MediaType.valueOf(imageToken.getContentType());
    return ResponseEntity.ok()
        .cacheControl(createCacheControlHeader())
        .contentType(mediaType).body(readBytes(imageStream.inputStream()));
  }

  // TODO Do I need this? For albums I guess. As a first step I want to add images to news items or club description. How do I upload several images?
  @PostMapping(value = "/clubs/{clubId}/upload-images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageTokenId> uploadClubImages(@PathVariable String clubId, @RequestParam(value = "files") List<MultipartFile> multipartFiles) {
    multipartFiles.forEach(ImageController::validateFileSize);

    return null;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(value = "/clubs/{clubId}/upload-logo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageTokenId> uploadClubLogo(@PathVariable String clubId, @RequestParam(value = "file") MultipartFile multipartFile) {
    validateFileSize(multipartFile);

    ImageToken imageToken = imageService.createClubLogo(clubId, multipartFile);
    return ResponseEntity.ok(imageToken.getImageTokenId());
  }

  @PostMapping(value = "/users/{userId}/upload-profile-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageTokenId> uploadProfileImage(@AuthenticationPrincipal AppUserDetails user, @PathVariable UserId userId,
      @RequestParam(value = "file") MultipartFile multipartFile) {
    userResourceAuthorization.validateUserOrChild(userId, user.getUserId());
    validateFileSize(multipartFile);

    ImageToken imageToken = imageService.createProfileImage(userId, multipartFile);
    return ResponseEntity.ok(imageToken.getImageTokenId());
  }
}
