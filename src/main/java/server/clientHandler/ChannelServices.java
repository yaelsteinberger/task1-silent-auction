package server.clientHandler;

import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import authenticate.HttpStatusCode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.HttpResponse;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.AddBidMessage;
import entity.command.schemas.LoginUserMessage;
import entity.command.schemas.MessageToClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import usersList.AbstractUsersList;
import entity.StatusCode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public class ChannelServices {
    private final static Logger logger = LoggerFactory.getLogger(ChannelServices.class);

    private final Socket socket;
    private final AbstractUsersList usersList;
    private final AuctionItemsList auctionItemsList;
    private final ObjectMapper mapper;
    private String userId;

    public ChannelServices( Socket socket,
                            AbstractUsersList usersList,
                            AuctionItemsList auctionItemsList) {
        this.socket = socket;
        this.usersList = usersList;
        this.auctionItemsList = auctionItemsList;
        this.mapper = new ObjectMapper();
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }


    public int sendMessageToClient(int opcode, String message) throws IOException {
        logger.debug("Send message opcode {} to Client",opcode);

        MessageToClientMessage messageToClient = new MessageToClientMessage(message);
        Command writeCommand = new Command(opcode, messageToClient);

        OutputStream writer = socket.getOutputStream();
        this.mapper.writeValue(writer,writeCommand);

        return StatusCode.SUCCESS;
    }

    public int handleLoginClient(LoginUserMessage message) throws IOException {
        String userName = message.getUserName();
        String messageToClient = null;
        int opcode = Opcodes.NONE;

        int statusCode = this.usersList.loginUser(userName);

        switch(statusCode){
            case StatusCode.INVALID_USERNAME:{
                messageToClient = "You have chosen an invalid username, please chose another name (type \"exit\" to exit):";
                opcode = Opcodes.LOGIN_CLIENT;
                break;
            }
            case StatusCode.ACCOUNT_IS_DISABLED:{
                messageToClient = "Your account is disabled, please contact the administrators ";
                opcode = Opcodes.ACTION_FAILED;
                break;
            }
            case StatusCode.NO_ACCOUNT_EXISTS: {
                messageToClient = "No account exists, please register (type \"exit\" to exit): ";
                opcode = Opcodes.REGISTER_CLIENT;
                break;
            }
            case StatusCode.SUCCESS:{
                this.userId = userName;
                String list = auctionItemsList.itemsListToPrettyString();
                messageToClient = "YOU HAVE BEEN LOGGED IN SUCCESSFULLY!\n\n" +
                        list + "\n\n" +
                        getAuctionInstructions();
                opcode = Opcodes.LOGIN_SUCCESS;
                break;
            }
        }

        if(opcode != Opcodes.NONE){
            sendMessageToClient(opcode, messageToClient);
        }

        return statusCode;
    }

    public int handleRegisterActionResult(HttpResponse response) throws IOException {
        int statusCode = StatusCode.REGISTRATION_SUCCESSFUL;

        if(response.isError()){
            switch (response.getStatus()){
                case HttpStatusCode.FORBIDDEN: {
                    statusCode = StatusCode.ACCOUNT_ALREADY_EXISTS;

                    logger.error("Cannot signup client - Account already exists");
                    String message = "You are trying to signup to an existing account\nIf you cannot login, then contact the administrators";
                    sendMessageToClient(Opcodes.ACTION_FAILED, message);
                    break;
                }
            }
        }else{
            logger.debug("Registration successful - Sending welcome message to client");
            sendMessageToClient(Opcodes.WELCOME, getWelcomeMessage());
        }

        return statusCode;
    }

    public int handleAddBid(AddBidMessage message) throws IOException {

        AddBidMessage addBid = message;
        Long itemId = addBid.getAuctionItemId();
        User user = usersList.findByUserName(userId);
        AuctionItem auctionItem = this.auctionItemsList.findById(addBid.getAuctionItemId());
        int statusCode;
        int opcode;
        String messageToClient;

        if(auctionItem.addBidder(user,addBid.getBidValue())){
            /* send updated bidders list to client */
            opcode = Opcodes.AUCTION_ITEM;
            messageToClient = this.auctionItemsList.itemsListToPrettyString();
            statusCode = StatusCode.SUCCESS;
        }else{
            opcode = Opcodes.ACTION_FAILED;
            messageToClient = "The bid value you offered was invalid, please try again...";
            statusCode = StatusCode.INVALID_VALUE;
        }

        sendMessageToClient(opcode,messageToClient);

        return statusCode;
    }


    public String getWelcomeMessage(){
        return "WELCOME TO SILENT AUCTION\n" +
                "Please login or register if you don't have an account: \n" +
                "-> To login type \"login\" and press Enter\n" +
                "-> To register type \"reg\" and press Enter\n" +
                "To exit type \"exit\" and press Enter";
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(this.usersList.findByUserName(userId));
    }

    private String getAuctionInstructions(){
        return  "-> To display an auction item details type \"item\" and press Enter\n" +
                "-> To display auction items list for an item type \"list\" and press Enter\n" +
                "-> To add a bid type \"bid\" and press Enter\n" +
                "To exit you can type \"exit\" and press Enter\n";
    }
}
