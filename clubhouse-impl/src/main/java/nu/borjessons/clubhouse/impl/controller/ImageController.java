package nu.borjessons.clubhouse.impl.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.activation.FileTypeMap;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.key.ImageId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.service.ImageService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ImageController {
  private final ImageService imageService;

  @GetMapping(value = "/images/{imageId}")
  public ResponseEntity<byte[]> getImage(@PathVariable ImageId imageId) throws IOException {
    File file = imageService.getImage(imageId);
    MediaType mediaType = MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file));
    return ResponseEntity.ok().contentType(mediaType).body(Files.readAllBytes(file.toPath()));
  }

  @PostMapping(value = "/clubs/{clubId}/upload-images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageId> uploadImage(@PathVariable String clubId, @RequestParam(value = "file") MultipartFile multipartFile) {
    log.info("multipartFile: {}", multipartFile);
    // ImageId imageId = imageService.createClubLogo(clubId, multipartFile);
    return null;
  }

  // TODO Do I need this? For albums I guess. As a first step I want to add images to news items or club description. How do I upload several images?
  @PostMapping(value = "/clubs/{clubId}/upload-images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageId> uploadImage(@PathVariable String clubId, @RequestParam(value = "file") List<MultipartFile> multipartFiles) {
    log.info("multipartFiles: {}", multipartFiles);
    // List<ImageId> imageIds = imageService.createClubImages(clubId, multipartFiles);
    return null;
  }

  // TODO saves imageId in the UserEntity. When uploading a new image first delete the old one. This makes sure there is only on image
  @PostMapping(value = "/users/{userId}/upload-profile-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageId> uploadImage(@PathVariable UserId userId, @RequestParam(value = "file") MultipartFile multipartFile) {
    log.info("multipartFile: {}", multipartFile);
    // ImageId imageId = imageService.createProfileImage(userId, multipartFile);
    return ResponseEntity.ok(null);
  }
}



