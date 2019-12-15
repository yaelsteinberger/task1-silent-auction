package client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.channels.ReadChannel;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.LoginUserMessage;
import entity.command.schemas.MessageToClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import usersList.StatusCode;
import util.PrintHelper;

import java.io.*;
import java.net.Socket;

public class ClientReadChannel implements ReadChannel {
    private final static Logger logger = LoggerFactory.getLogger(ClientReadChannel.class);

    private final Socket socket;
    private final String userId;
    private ObjectMapper objectMapper;

    /* Create stream to read from keyboard */
    private BufferedReader kbd;

    public ClientReadChannel(Socket socket) throws IOException {
        this.socket = socket;
        this.userId = String.valueOf(socket.getLocalPort());;
        this.objectMapper = new ObjectMapper();
        this.kbd = new BufferedReader(new InputStreamReader(System.in));
        this.objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);

//        /* Send Client's login details to server */
//        this.objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
//        Command writeCommand = new Command(Opcodes.LOGIN_CLIENT,new LoginUserMessage(new User("guypeleg","Guy","Peleg")));
//        OutputStream writer = socket.getOutputStream();
//        this.objectMapper.writeValue(writer,writeCommand);
    }

    @Override
    public Command read() throws IOException {
        logger.debug("Waiting for reply from server...");
        InputStream reader = socket.getInputStream();
        Command readCommand = objectMapper.readValue(reader,Command.class);
        logger.debug("Read reply from server: {}",readCommand.getOpcode());

        return readCommand;
    }

    @Override
    public int handleRead(Command command) throws JsonProcessingException {
        int statusCode = StatusCode.FATAL_ERROR;
        logger.debug("Handling read command: {}",command.getOpcode());

        switch(command.getOpcode()){

            case Opcodes.WELCOME:{
                statusCode = handleWelcomeMessage(command);
                break;
            }
        }

        return statusCode;
    }

    @Override
    public void run() {
        try {
            boolean isRun = true;
            while(isRun){
                int statusCode = handleRead(read());
                isRun = (statusCode == StatusCode.FATAL_ERROR);
            }
        } catch (IOException e) {
            logger.error("{}", e.getMessage());
        } finally{
            logger.info("EXIT");

            try {socket.close();}
            catch (IOException e) {logger.error("{}", e.getMessage());}
        }
    }

    private int handleWelcomeMessage(Command command) throws JsonProcessingException {
        /* Display on screen the welcome message and wait for user input */
        MessageToClientMessage message = (MessageToClientMessage)command.getMessage();
        System.err.println(message.getMessage());
        return StatusCode.SUCCESS;
    }
}



