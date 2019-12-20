package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {
    private static Properties properties;

    public static void setPropsFromConfigPropertiesFile(String propFilePath){
        try ( InputStream inputStream = new FileInputStream(propFilePath)) {
            properties = new Properties();
            properties.load(inputStream);
        }
        catch (NullPointerException | IOException e){
            properties  = readDefaultProps();
        }
    }

    public static void setProperties(Properties props) {
        properties = props;
    }

    public static Properties getProperties() {
        return properties;
    }


    private static Properties readDefaultProps(){
        Properties props = new Properties(){{
            put("server.port", 6666);
        }};

        return props;
    }
}
