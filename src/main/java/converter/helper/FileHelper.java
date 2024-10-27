package converter.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {

  private static final String ENCODING = "UTF-8";

  private static final Logger log
      = LoggerFactory.getLogger(FileHelper.class);

  public static String extractDirectoryName(String path) {
    return Paths.get(path).getParent().toString();
  }

  public static String extractFileName(String path) {
    return Paths.get(path).getFileName().toString();
  }

  synchronized public static List<String> listAllFilePathsFromSelectedDirectory(String path)
      throws IOException {
    List<String> list = new ArrayList<String>();
    if (path == null) {
      log.error("Path of the file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    try (Stream<Path> stream = Files.list(Paths.get(path))) {
      return stream
          .filter(file -> !Files.isDirectory(file))
          .map(Path::toString)
          .collect(Collectors.toList());
    }
  }

  synchronized public static List<String> listAllFilesFromDirectory(String path)
      throws IOException {
    if (path == null) {
      log.error("Path of the file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    try (Stream<Path> stream = Files.list(Paths.get(path))) {
      return stream
          .filter(file -> !Files.isDirectory(file))
          .map(Path::getFileName)
          .map(Path::toString)
          .collect(Collectors.toList());
    }
  }

  public static LocalDateTime obtainLatestDateFromFileNamesInDirectory(String path)
      throws IOException {
    List<String> given = FileHelper.listAllFilesFromDirectory(path);
    LocalDateTime actual = given.stream()
        .map(i -> StringProcessor.extractLocalDateTimeFromFileName(i))
        .max((o1, o2) -> o1.compareTo(o2))
        .orElseGet(() -> LocalDateTime.of(2022, 11, 23, 10, 56, 0));
    return actual;
  }

  public static void copyFilesToDirectory(List<String> src, String dst)
      throws IOException, InterruptedException {
    FileHelper.createDirectory(dst);
    if (!exists(dst)) {
      FileHelper.createFile(dst);
    }
    if (src == null ^ dst == null) {
      log.error("Passed argument is null");
      throw new IllegalArgumentException("Passed argument is null");
    }
    for (int i = 0; i < src.size(); i++) {
      String dstFilePath = dst + "/" + extractFileName(src.get(i));
      if (!exists(src.get(i))) {
        continue;
      }
      if (exists(dstFilePath)) {
        continue;
      }
      Files.copy(Paths.get(src.get(i)), Paths.get(dstFilePath));
      log.debug("Copied {} file to folder {}", Paths.get(src.get(i)).getFileName(), dst);
    }
  }

  public static void moveFilesToDirectory(List<String> src, String dst)
      throws IOException, InterruptedException {
    FileHelper.createDirectory(dst);
    if (src == null ^ dst == null) {
      log.error("Passed argument is null");
      throw new IllegalArgumentException("Passed argument is null");
    }
    for (int i = 0; i < src.size(); i++) {
      String dstFilePath = dst + "/" + extractFileName(src.get(i));
      if (!exists(src.get(i))) {
        continue;
      }
      if (exists(dstFilePath)) {
        continue;
      }
      Files.move(Paths.get(src.get(i)), Paths.get(dstFilePath));
      log.debug("Moved {} file to folder {}", Paths.get(src.get(i)).getFileName(), dst);
    }
  }

  public static void clearAllDirectory(String path) throws IOException {
    if (path == null) {
      log.error("Path of the file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    if (!exists(path)) {
      throw new IllegalArgumentException("Attempted to clean not existing directory");
    }
    FileUtils.cleanDirectory(new File(path));
  }

  public static void removeFileFromDirectory(String path) throws IOException {
    if (path == null) {
      log.error("Path of the file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    if (!exists(path)) {
      throw new IllegalArgumentException("Attempted to clean not existing directory");
    }
    if (!Files.isRegularFile(Paths.get(path))) {
      log.error("Provided path is not a file");
      throw new IllegalArgumentException("Attempted to delete not a file");
    }
    FileUtils.delete(new File(path));
  }

  public static void createFile(String path) throws IOException {
    if (path == null) {
      log.error("Path of the file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    String directory = extractDirectoryName(path);
    if (!exists(directory)) {
      log.debug("Folder {} does not exist", directory);
      Files.createDirectory(Paths.get(directory));
    }
    try {
      log.debug("Creating new file: {}", path);
      Files.createFile(Paths.get(path));
    } catch (IOException e) {
      log.error("Could not create file: {}", path, e.getMessage());
    }
  }

  public static void createDirectory(String path) throws IOException {
    if (path == null) {
      log.error("Path of the file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    if (exists(path)) {
      if (!Files.isDirectory(Paths.get(path))) {
        log.error("Provided path {} is not a directory", path);
        throw new IllegalArgumentException("Provided path is existing filepath");
      }
      log.debug("Folder already exists");
    } else if (!exists(path)) {
      Files.createDirectory(Paths.get(path));
    }
  }

  public static boolean exists(String filePath) {
    if (filePath == null) {
      log.error("Path of the file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    return Files.exists(Paths.get(filePath));
  }

  public static void delete(String filePath) throws IOException {
    if (filePath == null) {
      log.error("Path of file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    log.debug("Deleting file: {}", filePath);
    Files.deleteIfExists(Paths.get(filePath));
  }

  public static boolean isEmpty(String filePath) throws IOException {
    if (filePath == null) {
      log.error("Path of file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    if (!Files.exists(Paths.get(filePath))) {
      log.error("File does not exist");
      throw new FileNotFoundException("File does not exist.");
    }
    return (new File(filePath).length() == 0);
  }

  public static void clear(String filePath) throws IOException {
    if (filePath == null) {
      log.error("Path of the file cannot be null");
      throw new IllegalArgumentException("Path of the file cannot be null");
    }
    FileUtils.write(new File(filePath), "", ENCODING);
  }
}
