package converter.services;

import converter.configuration.FileFormatType;
import converter.exceptions.ServiceOperationException;
import converter.helper.FileHelper;
import converter.mapper.Mapper;
import converter.mapper.ObjectTranslator;
import converter.model.baseLinkerModel.XML.OrdersBaseLinkerXml;
import converter.serializers.ObjectSerializer;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileTranslation<X, Y> {

  protected final Logger log = LoggerFactory.getLogger(getClass());
  protected List<X> inputObjects;
  private String outputDirectory;
  private String inputDirectory;
  private Mapper<X, Y> mapper;
  private FileFormatType fileFormatType;

  public FileTranslation(String inputDirectory, String outputDirectory, Mapper<X, Y> mapper)
      throws ServiceOperationException {
    this.inputDirectory = Optional.ofNullable(inputDirectory).orElseThrow(
        () -> new ServiceOperationException("No File path provided file translations"));
    this.outputDirectory = Optional.ofNullable(outputDirectory).orElseThrow(
        () -> new ServiceOperationException("No File path provided file translations"));
    this.mapper = Optional.ofNullable(mapper)
        .orElseThrow(() -> new ServiceOperationException("No mapper provided"));
  }

  public FileTranslation(String inputDirectory, String outputDirectory, Mapper<X, Y> mapper,
      FileFormatType fileFormatType) throws ServiceOperationException {
    this.inputDirectory = Optional.ofNullable(inputDirectory).orElseThrow(
        () -> new ServiceOperationException("No File path provided file translations"));
    this.outputDirectory = Optional.ofNullable(outputDirectory).orElseThrow(
        () -> new ServiceOperationException("No File path provided file translations"));
    this.mapper = Optional.ofNullable(mapper)
        .orElseThrow(() -> new ServiceOperationException("No mapper provided"));
    this.fileFormatType = fileFormatType;
  }

  public FileTranslation(String outputDirectory, Mapper<X, Y> mapper)
      throws ServiceOperationException {
    this.outputDirectory = Optional.ofNullable(outputDirectory).orElseThrow(
        () -> new ServiceOperationException("No File path provided file translations"));
    this.mapper = Optional.ofNullable(mapper)
        .orElseThrow(() -> new ServiceOperationException("No mapper provided"));
  }

  public FileTranslation(Mapper<X, Y> mapper) throws ServiceOperationException {
    this.mapper = Optional.ofNullable(mapper)
        .orElseThrow(() -> new ServiceOperationException("No mapper provided"));
  }

  private void loadInputFromFiles() throws IOException {
    List<String> filesToLoad = new ArrayList<>();
    filesToLoad = FileHelper.listAllFilePathsFromSelectedDirectory(inputDirectory);
    inputObjects = new LinkedList<>();
    if (filesToLoad.size() == 0) {
      log.error("Input folder path {} does not have any files to process", inputDirectory);
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < filesToLoad.size(); i++) {
      X object;
      try (DataInputStream inputStream = new DataInputStream(
          new FileInputStream(filesToLoad.get(i)))) {
        object = (X) ObjectsDeserializer.load(inputStream, OrdersBaseLinkerXml.class,
            FileFormatType.XML);
      }
      inputObjects.add(object);
    }
  }

  private String extractValue(String key, Y object) {
    return Pattern.compile(String.format("(%s)=([\\w-]+),", key)).matcher(object.toString())
        .results().map(s -> s.group(2)).findFirst().orElse("DEFAULT");
  }

  private void saveTranslationToOutputDirectory(List<X> inputObjects, String preffix, String key)
      throws IOException, ParseException {
    List<Y> translatedOutputObjects = ObjectTranslator.translate(mapper, inputObjects);
    for (int i = 0; i < translatedOutputObjects.size(); i++) {
      Y objectTranslated = translatedOutputObjects.get(i);
      String suffix = extractValue(key, objectTranslated);
      String filePath = String.format("%s%s_%s.xml", outputDirectory, preffix, suffix);
      try (DataOutputStream dataOutputStream = new DataOutputStream(
          new FileOutputStream(filePath))) {
        ObjectSerializer.save(dataOutputStream, objectTranslated, FileFormatType.XML);
      }
      log.debug("Object translated and saved as file {}", Paths.get(filePath).getFileName());
    }
  }

  private void saveTranslationToOutputDirectory(List<X> inputObjects, String preffix, String key,
      Path outputDirectory) throws IOException, ParseException {
    List<Y> translatedOutputObjects = ObjectTranslator.translate(mapper, inputObjects);
    for (int i = 0; i < translatedOutputObjects.size(); i++) {
      Y objectTranslated = translatedOutputObjects.get(i);
      String suffix = extractValue(key, objectTranslated);
      Path filePath = Paths.get(outputDirectory.toString(),
          String.format("%s_%s.xml", preffix, suffix));
      try (DataOutputStream dataOutputStream = new DataOutputStream(
          new FileOutputStream(filePath.toFile()))) {
        ObjectSerializer.save(dataOutputStream, objectTranslated, FileFormatType.XML);
      }
      log.debug("Object translated and saved as file {}", filePath.getFileName());
    }
  }

  private void saveTranslationToOutputDirectory(List<X> inputObjects, String preffix, String key,
      Path outputDirectory, FileFormatType fileFormatType) throws IOException, ParseException {
    List<Y> translatedOutputObjects = ObjectTranslator.translate(mapper, inputObjects);
    for (int i = 0; i < translatedOutputObjects.size(); i++) {
      Y objectTranslated = translatedOutputObjects.get(i);
      String suffix = extractValue(key, objectTranslated);
      Path filePath = Paths.get(outputDirectory.toString(),
          String.format("%s_%s.%s", preffix, suffix, fileFormatType.toString().toLowerCase()));
      try (DataOutputStream dataOutputStream = new DataOutputStream(
          new FileOutputStream(filePath.toFile()))) {
        ObjectSerializer.save(dataOutputStream, objectTranslated, fileFormatType);
      }
      log.debug("Object translated and saved as file {}", filePath.getFileName());
    }
  }

  private void saveTranslationToOutputDirectory(String preffix, String key)
      throws IOException, ParseException {
    List<Y> translatedOutputObjects = ObjectTranslator.translate(mapper, inputObjects);
    for (int i = 0; i < translatedOutputObjects.size(); i++) {
      Y objectTranslated = translatedOutputObjects.get(i);
      String suffix = extractValue(key, objectTranslated);
      String filePath = String.format("%s%s_%s.xml", outputDirectory, preffix, suffix);
      try (DataOutputStream dataOutputStream = new DataOutputStream(
          new FileOutputStream(filePath))) {
        ObjectSerializer.save(dataOutputStream, objectTranslated, FileFormatType.XML);
      }
      log.debug("Object translated and saved as file {}", Paths.get(filePath).getFileName());
    }
  }

  private void saveTranslationToOutputDirectory(String preffix, String key,
      FileFormatType fileFormatType) throws IOException, ParseException {
    List<Y> translatedOutputObjects = ObjectTranslator.translate(mapper, inputObjects);
    for (int i = 0; i < translatedOutputObjects.size(); i++) {
      Y objectTranslated = translatedOutputObjects.get(i);
      String suffix = extractValue(key, objectTranslated);
      String filePath = String.format("%s%s_%s.xml", outputDirectory, preffix, suffix);
      try (DataOutputStream dataOutputStream = new DataOutputStream(
          new FileOutputStream(filePath))) {
        ObjectSerializer.save(dataOutputStream, objectTranslated, fileFormatType);
      }
      log.debug("Object translated and saved as file {}", Paths.get(filePath).getFileName());
    }
  }

  protected void execute(String preffix, String key) throws ServiceOperationException {
    try {
      log.debug("File translations operations started");
      loadInputFromFiles();
      saveTranslationToOutputDirectory(preffix, key);
    } catch (Exception e) {
      log.error("Error occured: {}", e.getMessage());
      throw new ServiceOperationException(e.getMessage());
    }
    log.debug("File translations operations finished");
  }

  protected void execute(List<X> inputObjects, String preffix, String key)
      throws ServiceOperationException {
    try {
      log.debug("File translations operations started");
      saveTranslationToOutputDirectory(inputObjects, preffix, key);
    } catch (Exception e) {
      log.error("Error occured: {}", e.getMessage());
      throw new ServiceOperationException(e.getMessage());
    }
    log.debug("File translations operations finished");
  }

  protected void execute(List<X> inputObjects, String preffix, String key, Path outputDirectory)
      throws ServiceOperationException {
    try {
      log.debug("File translations operations started");
      saveTranslationToOutputDirectory(inputObjects, preffix, key, outputDirectory);
    } catch (Exception e) {
      log.error("Error occured: {}", e.getMessage());
      throw new ServiceOperationException(e.getMessage());
    }
    log.debug("File translations operations finished");
  }

  abstract public void executeTask(List<?> input, Path path)
      throws ServiceOperationException, ParseException;

  abstract public void executeTask(List<?> input) throws ServiceOperationException, ParseException;
}
