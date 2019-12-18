package MOCKs;

import server.ServerProperties;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class MockSocketTarget {
    private Socket socket;
    private final Properties props;

    public MockSocketTarget() {
        props = ServerProperties.getProperties();
    }

    public void openSocketToSource() throws IOException {
        String port = (String) props.get("server.port");
        this.socket = new Socket(
                (String)props.get("server.host"),
                Integer.parseInt(port)
        );
    }

    public Socket getSocket() {
        return socket;
    }
}
