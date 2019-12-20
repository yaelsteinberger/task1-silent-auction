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
//        Integer port = MockTestProperties.getServerPort();//(String) props.get("server.port");
        this.socket = new Socket(
                MockTestProperties.getServerHost(),
                MockTestProperties.getServerPort()
        );
    }

    public Socket getSocket() {
        return socket;
    }
}
