package converter.helper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.FILES.FILES_TESTING;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileHelperTest {

  private static final String GIVEN_FOLDER = FILES_TESTING.FILE_HELPER_GIVEN_DIRECTORY;

  private static final File inputFile = new File(
      FILES_TESTING.FILE_HELPER_GIVEN_DIRECTORY + "/test.txt");
  private static final File expectedFile = new File(
      FILES_TESTING.FILE_HELPER_EXPECTED_DIRECTORY + "/test.txt");
  private static final String ENCODING = "UTF-8";

  @BeforeAll
  static void beforeAll() throws IOException {
    FileHelper.createDirectory(FILES_TESTING.FILE_HELPER_GIVEN_DIRECTORY);
    FileHelper.createDirectory(FILES_TESTING.FILE_HELPER_ACTUAL_DIRECTORY);
    FileHelper.createDirectory(FILES_TESTING.FILE_HELPER_EXPECTED_DIRECTORY);
  }

  @BeforeEach
  void beforeEach() throws IOException {
    FileUtils.cleanDirectory(new File(FILES_TESTING.FILE_HELPER_GIVEN_DIRECTORY));
    FileUtils.cleanDirectory(new File(FILES_TESTING.FILE_HELPER_ACTUAL_DIRECTORY));
    FileUtils.cleanDirectory(new File(FILES_TESTING.FILE_HELPER_EXPECTED_DIRECTORY));
  }

  @Test
  void listAllFilesFromDirectoryMethodShoultThrowIllegalArgumentException() throws IOException {
    assertThrows(IllegalArgumentException.class,
        () -> FileHelper.listAllFilePathsFromSelectedDirectory(null));
  }

  @Test
  void createDirectoryFunctionTest() throws IOException {
    //FileHelper.createDirectory("src/test/resources/FILE_HELPER/DIRECTORY_TEST/");
    String directoryName = LocalDateTime.now().withNano(0).toString()
        .replaceAll("[^A-Za-z0-9]", "_");
    String temporaryDirectoryPath = String.format("src/main/resources/temp/%s/", directoryName);
    FileHelper.createDirectory(temporaryDirectoryPath);

    Files.createFile(Path.of(String.format("%s%s", temporaryDirectoryPath, "TESTING.txt")));
    Files.deleteIfExists(Paths.get("src/test/resources/FILE_HELPER/DIRECTORY_TEST"));
  }

  @Test
  void createFileFunctionTest() throws IOException {
    FileHelper.createDirectory(GIVEN_FOLDER);

    assertTrue(Files.exists(Paths.get(GIVEN_FOLDER)));
  }

  @Test
  void deleteFileFunctionTest() throws IOException {
    inputFile.createNewFile();
    FileHelper.delete(inputFile.getPath());

    assertFalse(Files.exists(Paths.get(inputFile.getPath())));
  }

  @Test
  void shouldReturnFalseIfFileIsNotEmpty() throws IOException {
    FileUtils.writeLines(inputFile, Collections.singleton("bla bla bla"), ENCODING, true);

    assertFalse(FileHelper.isEmpty(inputFile.getPath()));
  }

  @Test
  void shouldReturnTrueIfFileIsEmpty() throws IOException {
    inputFile.createNewFile();

    assertTrue(FileHelper.isEmpty(inputFile.getPath()));
  }

  @Test
  void clearFileFunctionTest() throws IOException {
    expectedFile.createNewFile();
    FileUtils.writeLines(inputFile, Collections.singleton("bla bla bla"), ENCODING, true);

    FileHelper.clear(inputFile.getPath());

    assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
  }

  @Test
  void clearDirectoryFunctionTestShouldDeleteWholeDirectoryContentAndAllOfItsSubpath()
      throws IOException {
    FileHelper.createDirectory(GIVEN_FOLDER);
    Path subPathCreated = Paths.get(GIVEN_FOLDER, "SUBPATH");
    Path fileSubPath = Paths.get(subPathCreated.toString(), "File1.txt");
    FileHelper.createFile(fileSubPath.toString());

    FileHelper.createDirectory(subPathCreated.toString());

    FileHelper.clearAllDirectory(GIVEN_FOLDER);

    assertFalse(Files.exists(subPathCreated));
  }

  @Test
  void test()
      throws IOException {
    FileHelper.clearAllDirectory("./STOCK_FILES");
  }

  @Test
  void deleteMethodShouldThrowExceptionForNullFilePathArgument() {
    assertThrows(IllegalArgumentException.class, () -> FileHelper.delete(null));
  }

  @Test
  void existsMethodShouldThrowExceptionForNullFilePathArgument() {
    assertThrows(IllegalArgumentException.class, () -> FileHelper.exists(null));
  }

  @Test
  void isEmptyMethodShouldThrowExceptionForNullFilePathArgument() {
    assertThrows(IllegalArgumentException.class, () -> FileHelper.isEmpty(null));
  }

  @Test
  void clearMethodShouldThrowExceptionForNullFilePathArgument() {
    assertThrows(IllegalArgumentException.class, () -> FileHelper.clear(null));
  }

  @Test
  void isEmptyMethodShouldThrowExceptionForNonExistingFile() {
    assertThrows(FileNotFoundException.class, () -> FileHelper.isEmpty(inputFile.getPath()));
  }
}
