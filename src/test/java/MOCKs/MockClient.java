package MOCKs;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.command.Command;
import server.ServerProperties;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;

public class MockClient {
    private Socket socket;
    private final Properties props;
    private final ObjectMapper mapper;

    public MockClient() {
        props = ServerProperties.getProperties();
        mapper = new ObjectMapper();

    }

    public void run() throws IOException {
        String serverPort = (String) props.get("server.port");
        this.socket = new Socket(
                (String)props.get("server.host"),
                Integer.parseInt((String)serverPort)
        );

//        while(true){
//            InputStream reader = socket.getInputStream();
//            Command readCommand = mapper.readValue(reader,Command.class);
//        }
    }
}
