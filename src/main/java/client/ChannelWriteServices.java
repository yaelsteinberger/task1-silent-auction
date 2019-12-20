package client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.*;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import usersList.StatusCode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class ChannelWriteServices {
    private final static Logger logger = LoggerFactory.getLogger(ChannelWriteServices.class);

    private final Socket socket;;
    private ObjectMapper mapper;

    public ChannelWriteServices(Socket socket) throws IOException {
        this.socket = socket;
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

    public int handleWrite(int opcode, @Nullable Object requestData) throws IOException {
        logger.debug("Send user request {}", opcode);
        int statusCode = StatusCode.SUCCESS;

        switch(opcode){
            case Opcodes.CLIENT_CONNECTED:
            case Opcodes.GET_AUCTION_LIST:{
                sendMessageToServer(opcode,new EmptyMessage());
                break;
            }

            case Opcodes.LOGIN_CLIENT:{
                LoginUserMessage message = new LoginUserMessage((String) requestData);
                sendMessageToServer(opcode,message);
                break;
            }
            case Opcodes.REGISTER_CLIENT:{
                RegisterUserMessage message = new RegisterUserMessage((User) requestData);
                sendMessageToServer(opcode,message);
                break;
            }

            case Opcodes.GET_AUCTION_ITEM:{
                GetAuctionItemMessage message = new GetAuctionItemMessage((Long) requestData);
                sendMessageToServer(opcode,message);
                break;
            }

            case Opcodes.ADD_BID:{
                Long itemId = ((Map<String,Long>)requestData).get("item");
                Long value = ((Map<String,Long>)requestData).get("value");
                AddBidMessage message = new AddBidMessage(itemId,value);
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



