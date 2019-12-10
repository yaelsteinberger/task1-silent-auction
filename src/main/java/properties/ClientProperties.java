package properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientProperties {
    private static Properties properties;
    private final static String propFilePath = "clientConfig.properties";

    public static void readConfigPropertiesFile(){
        try ( InputStream inputStream = new FileInputStream(propFilePath)) {
            properties = new Properties();
            properties.load(inputStream);
        }
        catch (NullPointerException | IOException e){
            properties  = readDefaultProps();
        }
    }

    public static Properties getProperties() {
        return properties;
    }


    private static Properties readDefaultProps(){
        Properties props = new Properties(){{
            put("server.port", 6666);
            put("host.name", "localhost");
        }};

        return props;
    }
}
