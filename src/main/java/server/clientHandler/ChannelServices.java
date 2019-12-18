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
import usersList.StatusCode;

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
        User user = (message).getUser();

        int statusCode = this.usersList.loginUser(user);

        if(statusCode == StatusCode.NO_ACCOUNT_EXISTS){
            handleNotExistAccount();
        }else if(statusCode == StatusCode.SUCCESS){
            this.userId = user.getUserName();
            handleSuccessLoginAccount();
        }

        return statusCode;
    }

    public int handleRegisterActionResult(HttpResponse response) throws IOException {
        int statusCode = StatusCode.REGISTRATION_SUCCESSFUL;

        if(response.isError()){
            switch (response.getStatusCode()){
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
        User user = usersList.findByUserName(userId);
        AuctionItem auctionItem = this.auctionItemsList.findById(addBid.getAuctionItemId());

        boolean isSuccess = auctionItem.addBidder(user,addBid.getBidValue());
        int statusCode = isSuccess ? StatusCode.SUCCESS : StatusCode.INVALID_VALUE;

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

    private void handleNotExistAccount() throws IOException {
        logger.debug("Sending message to client that account doesn't exist");
        String message = "No account exists, please register (type \"exit\" to exit): ";
        sendMessageToClient(Opcodes.REGISTER_CLIENT, message);
    }

    private void handleSuccessLoginAccount() throws IOException {
        /* when user logged in successfully, send the auction items list with instructions */

        String list = auctionItemsList.itemsListToPrettyString();
        String message = "YOU HAVE BEEN LOGGED IN SUCCESSFULLY!\n\n" +
                list + "\n\n" +
                getAuctionInstructions();

        sendMessageToClient(Opcodes.LOGIN_SUCCESS, message);
    }

    private String getAuctionInstructions(){
        return  "-> To display an auction item details type \"item [item id]\" and press Enter\n" +
                "-> To display auction items list for an item type \"list\" and press Enter\n" +
                "-> To add a bid type \"bid item:[id] value:[value*]\" and press Enter\n" +
                "* Invalid value will not be accepted\n" +
                "To exit you can type \"exit\" and press Enter\n";
    }
}
