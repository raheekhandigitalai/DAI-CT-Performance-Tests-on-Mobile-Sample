package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesReader {

    public PropertiesReader() {}

    public String getProperty(String property) {
        Properties props = System.getProperties();

        try {

            if (getOperatingSystem().toLowerCase().contains("mac")) {
                props.load(new FileInputStream(System.getProperty("user.dir") + "/config.properties"));
            } else if (getOperatingSystem().toLowerCase().contains("win")) {
                props.load(new FileInputStream(System.getProperty("user.dir") + "\\config.properties"));
            }

            if (props.getProperty("accessKey").isEmpty()) {
                throw new Exception("SeeTest Cloud Access Key not found. Please look in config.properties.");
            }

            if (props.getProperty("cloudUrl").isEmpty()) {
                throw new Exception("SeeTest Cloud URL not found. Please look in config.properties.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return System.getProperty(property);
    }

    protected String getOperatingSystem() {
        String os = System.getProperty("os.name");
        return os;
    }

}
