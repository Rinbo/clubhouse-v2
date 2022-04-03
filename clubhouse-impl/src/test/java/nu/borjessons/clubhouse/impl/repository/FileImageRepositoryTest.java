package nu.borjessons.clubhouse.impl.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.dto.ImageStream;
import nu.borjessons.clubhouse.impl.util.FileUtils;

@Slf4j
class FileImageRepositoryTest {
  public static final Path PATH = Paths.get("clubs", "club1");
  private static final Path BASE_IMAGE_DIRECTORY = Paths.get(System.getProperty("java.io.tmpdir"), "clubhouse-test");
  private static final String ORIGINAL_FILE_NAME = "cool-pic.jpg";

  private static ImageToken createImageToken() {
    ImageToken imageToken = new ImageToken();
    imageToken.setName(ORIGINAL_FILE_NAME);
    imageToken.setId(1L);
    imageToken.setContentType("image/jpeg");
    imageToken.setPath(PATH);
    return imageToken;
  }

  private static Path createTempFile(ImageToken imageToken) throws IOException {
    Path fileDirectory = Files.createDirectories(
        BASE_IMAGE_DIRECTORY
            .resolve(imageToken.getPath())
            .resolve(imageToken.getImageTokenId().toString()));
    return Files.createFile(fileDirectory.resolve(imageToken.getName()));
  }

  private static MultipartFile mockMultiPartFile() throws IOException {
    MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
    Mockito.doNothing().when(multipartFile).transferTo(ArgumentMatchers.any(Path.class));
    Mockito.when(multipartFile.getOriginalFilename()).thenReturn(ORIGINAL_FILE_NAME);
    return multipartFile;
  }

  @AfterEach
  void afterEach() throws IOException {
    FileUtils.deleteDirectoryRecursively(BASE_IMAGE_DIRECTORY);
  }

  @Test
  void getAllPathsInClubDirectoryTest() throws IOException {
    ImageToken imageToken1 = createImageToken();
    ImageToken imageToken2 = createImageToken();
    FileImageRepository fileImageRepository = new FileImageRepository(BASE_IMAGE_DIRECTORY);

    Assertions.assertEquals(0, fileImageRepository.getClubImagePaths(PATH).size());

    createTempFile(imageToken1);
    createTempFile(imageToken2);

    Assertions.assertEquals(2, fileImageRepository.getClubImagePaths(PATH).size());
  }

  @Test
  void findImageByImageTokenTest() throws IOException {
    Path imageDirectory = Mockito.mock(Path.class);
    ImageToken imageToken = createImageToken();
    Path path = createTempFile(imageToken);
    Mockito.when(imageDirectory.resolve(ArgumentMatchers.any(Path.class))).thenReturn(path);

    FileImageRepository fileImageRepository = new FileImageRepository(imageDirectory);
    ImageStream imageStream = fileImageRepository.findImageByImageToken(imageToken);

    ArgumentCaptor<Path> pathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
    Mockito.verify(imageDirectory).resolve(pathArgumentCaptor.capture());

    Assertions.assertEquals(ORIGINAL_FILE_NAME, pathArgumentCaptor.getValue().getFileName().toString());
    Assertions.assertSame(imageToken, imageStream.imageToken());
    Assertions.assertNotNull(imageStream.inputStream());

    Mockito.verifyNoMoreInteractions(imageDirectory);
  }

  @Test
  void saveImageTest() throws IOException {
    MultipartFile multipartFile = mockMultiPartFile();
    FileImageRepository fileImageRepository = new FileImageRepository(BASE_IMAGE_DIRECTORY);

    ImageTokenId imageTokenId = fileImageRepository.saveImage(multipartFile, PATH);
    Assertions.assertNotNull(imageTokenId);

    ArgumentCaptor<Path> pathArgumentCaptor = ArgumentCaptor.forClass(Path.class);

    Mockito.verify(multipartFile).getOriginalFilename();
    Mockito.verify(multipartFile).transferTo(pathArgumentCaptor.capture());
    Assertions.assertEquals(ORIGINAL_FILE_NAME, pathArgumentCaptor.getValue().getFileName().toString());
    Mockito.verifyNoMoreInteractions(multipartFile);
  }

  @Test
  void deleteImageTest() throws IOException {
    ImageToken imageToken = createImageToken();
    Path path = createTempFile(imageToken);
    Assertions.assertTrue(Files.exists(path));
    FileImageRepository fileImageRepository = new FileImageRepository(BASE_IMAGE_DIRECTORY);
    fileImageRepository.deleteImage(imageToken);
    Assertions.assertFalse(Files.exists(path));
  }
}