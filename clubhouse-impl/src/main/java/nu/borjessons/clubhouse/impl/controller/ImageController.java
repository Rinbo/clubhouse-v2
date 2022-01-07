package nu.borjessons.clubhouse.impl.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.activation.FileTypeMap;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.key.ImageId;
import nu.borjessons.clubhouse.impl.service.ImageService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/images")
public class ImageController {
  private final ImageService imageService;

  @GetMapping(value = "/{imageId}")
  public ResponseEntity<byte[]> getImage(@PathVariable ImageId imageId) throws IOException {
    File file = imageService.getImage(imageId);
    MediaType mediaType = MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file));
    return ResponseEntity.ok().contentType(mediaType).body(Files.readAllBytes(file.toPath()));
  }

  @GetMapping(value = "/octet/{imageId}")
  public ResponseEntity<byte[]> getOctetImage(@PathVariable ImageId imageId) throws IOException {
    File file = imageService.getImage(imageId);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(Files.readAllBytes(file.toPath()));
  }

  @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImageId> uploadImage(@RequestParam(value = "file") MultipartFile multipartFile) {
    log.info("multipartFile: {}", multipartFile);
    ImageId imageId = imageService.createImage(multipartFile);
    return ResponseEntity.ok(imageId);
  }
}



