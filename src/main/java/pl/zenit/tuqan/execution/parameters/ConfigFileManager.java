package pl.zenit.tuqan.execution.parameters;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileManager {

    private final File configFile;

    private final Map<String, String> configMap = new HashMap<>();

    public ConfigFileManager(File file) {
        this.configFile = file;
    }

    public void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ( (line = reader.readLine()) != null ) {
                String[] keyValue = line.split("=", 2);
                if ( keyValue.length == 2 ) {
                    configMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        String value = configMap.get(key);
        if (value == null) 
            throw new RuntimeException(key + " property is not set");
        return value;
    }
    
    public String get(String key, String defaultValue) {
        String value = configMap.get(key);
        return value == null || value.isEmpty() ? defaultValue : value;
    }
    
    public void put(String key, String value) {
        configMap.put(key, value);
    }

    public void flushConfig() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
}

}
