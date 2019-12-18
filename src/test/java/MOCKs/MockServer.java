package MOCKs;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.BaseMessage;
import entity.command.schemas.MessageToClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;
import util.PrintHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MockServer implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(MockServer.class);

    private ServerSocket listener;
    private Socket client;
    private ObjectMapper mapper;

    public MockServer(Integer serverPort) throws IOException {
        this.listener = new ServerSocket(serverPort);
        this.mapper = new ObjectMapper();
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    @Override
    public void run() {
        try {
            client = listener.accept();

            boolean isRun = true;
            while(isRun){
                InputStream reader = client.getInputStream();
                Command command = mapper.readValue(reader,Command.class);
                logger.debug("Received Message from client");

                if(command.getOpcode() != Opcodes.EXIT){
                    OutputStream writer = client.getOutputStream();
                    this.mapper.writeValue(writer,command);
                    logger.debug("Sending back Message to client");
                }else{
                    isRun = false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logger.debug("EXIT");
        }
    }
}
