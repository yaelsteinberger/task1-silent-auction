package client.channelHandler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.channels.ReadChannel;
import entity.command.Command;
import entity.command.schemas.MessageToClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entity.StatusCode;

import java.io.*;
import java.net.Socket;

public class ClientReadChannel implements ReadChannel {
    private final static Logger logger = LoggerFactory.getLogger(ClientReadChannel.class);

    private final Socket socket;
    private final ChannelReadServices channelReadServices;
    private ObjectMapper mapper;

    public ClientReadChannel(Socket socket) throws IOException {
        this.socket = socket;
        this.mapper = new ObjectMapper();
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        this.channelReadServices = new ChannelReadServices(socket);
    }

    @Override
    public Command read() throws IOException {
        logger.debug("Waiting for reply from server...");
        InputStream reader = socket.getInputStream();
        Command readCommand = mapper.readValue(reader,Command.class);
        logger.debug("Read reply from server: {}",readCommand.getOpcode());

        return readCommand;
    }

    @Override
    public int handleRead(Command command) throws IOException {
        logger.debug("Handling read command: {}",command.getOpcode());

        int statusCode = StatusCode.FATAL_ERROR;
        int opcode = command.getOpcode();
        String message = ((MessageToClientMessage)command.getMessage()).getMessage();

        /* Display message to client */
        displayMessageToClient(message);

        try {
            /* handle client input */
            statusCode = this.channelReadServices.handleUserRequest();

        } catch (InterruptedException e) {
            logger.error(e.getMessage());
//            e.printStackTrace();
        }
        return statusCode;
    }

    @Override
    public void run() {
        try {

            /* send to server connection acknowledgement */
            this.channelReadServices.handleClientConnectionEvent();

            boolean isRun = true;
            while(isRun){
                Command command = read();
                int statusCode = handleRead(command);
                isRun = (statusCode != StatusCode.FATAL_ERROR);
            }
        } catch (IOException e) {
            logger.error("{}", e.getMessage());
        } finally{
            logger.info("EXIT");

            try {socket.close();}
            catch (IOException e) {logger.error("{}", e.getMessage());}
        }
    }

    private void displayMessageToClient(String message){
        System.err.println(message);
    }
}



