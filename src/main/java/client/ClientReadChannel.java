package client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.channels.ReadChannel;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.BaseMessage;
import entity.command.schemas.EmptyMessage;
import entity.command.schemas.LoginUserMessage;
import entity.command.schemas.MessageToClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import usersList.StatusCode;
import util.PrintHelper;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientReadChannel implements ReadChannel {
    private final static Logger logger = LoggerFactory.getLogger(ClientReadChannel.class);

    private final Socket socket;
    private final ChannelReadServices channelReadServices;
    private ObjectMapper mapper;

    public ClientReadChannel(Socket socket, User clientIdentityDetails) throws IOException {
        this.socket = socket;
        this.mapper = new ObjectMapper();
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        this.channelReadServices = new ChannelReadServices(socket,clientIdentityDetails);
    }

    @Override
    public Command read() throws IOException {
        logger.debug("Waiting for reply from server...");
        InputStream reader = socket.getInputStream();

//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(reader));
//        String dataGram = bufferedReader.readLine();
//        PrintHelper.printPrettyInRed(dataGram);

        Command readCommand = mapper.readValue(reader,Command.class);
        logger.debug("Read reply from server: {}",readCommand.getOpcode());

        return readCommand;
    }

    @Override
    public int handleRead(Command command) throws IOException {
        int statusCode = StatusCode.FATAL_ERROR;
        logger.debug("Handling read command: {}",command.getOpcode());
        try {
            switch(command.getOpcode()){

                case Opcodes.WELCOME:{
                    statusCode = this.channelReadServices.handleWelcomeMessage((MessageToClientMessage) command.getMessage());
                    break;
                }

                case Opcodes.REGISTER_CLIENT:{
                    statusCode = this.channelReadServices.handleRegisterClientMessage((MessageToClientMessage) command.getMessage());
                    break;
                }

                case Opcodes.LOGIN_SUCCESS:{
                    this.channelReadServices.handleLoginSuccessMessage((MessageToClientMessage)command.getMessage());
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
}



