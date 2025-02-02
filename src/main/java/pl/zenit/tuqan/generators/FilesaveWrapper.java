package pl.zenit.tuqan.generators;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class FilesaveWrapper {

    public static void saveToFile(List<String> sl, String filePath) {
         File target = new File(filePath);
         if (target.exists())
             target.delete();
          try {
              Files.write(target.toPath(),
                          sl.stream().collect(Collectors.joining("\r\n")).getBytes(StandardCharsets.UTF_8),
                          StandardOpenOption.CREATE);
          }
          catch (IOException ex) {
              throw new RuntimeException(ex);
          }
      }
    
}
