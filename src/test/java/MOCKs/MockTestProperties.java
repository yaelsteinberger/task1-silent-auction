package MOCKs;

import client.ClientProperties;
import server.ServerProperties;
import util.PrintHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;

public class MockTestProperties {
    private static final String testPropFilePath = "src\\test\\resources\\mockConfig.properties";
    private static Properties properties;

    static {
        try {
            properties = generateProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Integer authServerPort;
    private static String authServerHost;
    private static String authServerUrl;
    private static Integer serverPort;
    private static String serverHost;

    public static void reset() throws IOException {
        /* force generating properties to set new arbitrary props to the relevant ones (e.g. auth port) */
        properties = generateProperties();
    }

    public static Properties getProperties() {
        return properties;
    }

    public static Integer getAuthServerPort() {
        return authServerPort;
    }

    public static String getAuthServerHost() {
        return authServerHost;
    }

    public static String getAuthServerUrl() {
        return authServerUrl;
    }

    public static Integer getServerPort() {
        return serverPort;
    }

    public static String getServerHost() {
        return serverHost;
    }

    private static Properties generateProperties() throws IOException {
        try ( InputStream inputStream = new FileInputStream(testPropFilePath)) {
            properties = new Properties();
            properties.load(inputStream);
        }
        catch (NullPointerException | IOException e){
            properties  = readDefaultProps();
        }

        authServerPort = Integer.parseInt((String)properties.get("authServer.port"));
        authServerHost = (String)properties.get("authServer.host");
        authServerUrl = (String)properties.get("authServer.url");
        serverPort = Integer.parseInt((String)properties.get("server.port"));
        serverHost = (String)properties.get("server.host");

        /* Generate arbitrary props for auth server (port, url) */
        ServerSocket serverSocket = new ServerSocket(0);
        Integer arbAuthPort = serverSocket.getLocalPort();
        authServerUrl = authServerUrl.replace(String.valueOf(authServerPort),String.valueOf(arbAuthPort));
        authServerPort = arbAuthPort;
        properties.setProperty("authServer.url", authServerUrl);
        properties.setProperty("authServer.port", String.valueOf(arbAuthPort));
        serverSocket.close();



        setAppProperties();
        return properties;
    }

    private static void setAppProperties(){
        ServerProperties.setProperties(properties);
        ClientProperties.setProperties(properties);
    }

    private static Properties readDefaultProps(){
        Properties props = new Properties(){{
            put("server.port",6666);
            put("server.host","localhost");
            put("authServer.port", 8080);
            put("authServer.host","localhost");
        }};
        return props;
    }
}
