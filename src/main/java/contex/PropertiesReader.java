package contex;

import exception.PropertiesFileException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesReader {

  public static Map<String,String> getStartUpPropertiesMap(String fileName)
      throws PropertiesFileException {
    Map<String, String> retMap = new HashMap<>();
    try(FileInputStream fileInputStream = new FileInputStream(fileName)) {
      Properties properties = new Properties();
      properties.load(fileInputStream);
      properties.forEach(
          (k, v) -> retMap.put((String) k, (String) v)
      );

    } catch (IOException e) {
      throw new PropertiesFileException("Properties file wasn`t read due to " + e.getClass(), e);
    }
    return retMap;
  }

}
