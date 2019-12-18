package client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.BaseMessage;
import entity.command.schemas.EmptyMessage;
import entity.command.schemas.LoginUserMessage;
import entity.command.schemas.MessageToClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import usersList.StatusCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelWriteServices {
    private final static Logger logger = LoggerFactory.getLogger(ChannelWriteServices.class);

    private final Socket socket;
    private User clientIdentityDetails;
    private ObjectMapper mapper;

    public ChannelWriteServices(Socket socket, User clientIdentityDetails) throws IOException {
        this.socket = socket;
        this.clientIdentityDetails = clientIdentityDetails;
        this.mapper = new ObjectMapper();
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    public void sendMessageToServer(int opcode, BaseMessage message) throws IOException {
        logger.debug("Send message opcode {} to Server",opcode);
        Command writeCommand = new Command(opcode,message);
        OutputStream writer = socket.getOutputStream();
        this.mapper.writeValue(writer,writeCommand);
    }

    public int handleWrite(int opcode) throws IOException {
        int statusCode = StatusCode.SUCCESS;

        switch(opcode){
            case Opcodes.CLIENT_CONNECTED:{
                sendMessageToServer(opcode,new EmptyMessage());
                break;
            }

            case Opcodes.LOGIN_CLIENT:
            case Opcodes.REGISTER_CLIENT:{
                LoginUserMessage message = new LoginUserMessage(clientIdentityDetails);
                sendMessageToServer(opcode,message);
                break;
            }

            case Opcodes.EXIT:{
                statusCode = StatusCode.TERMINATE_SESSION;
                break;
            }
        }
        return statusCode;
    }
}



