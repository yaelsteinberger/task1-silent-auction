package server;

import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import entity.User;
import entity.channels.ReadChannel;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.AddBidMessage;
import entity.command.schemas.LoginUserMessage;
import entity.command.schemas.MessageToClientMessage;
import entity.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import authenticate.HttpAuthApi;
import authenticate.HttpStatusCode;
import usersList.AbstractUsersList;
import usersList.StatusCode;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ServerReadChannel implements ReadChannel {
    private final static Logger logger = LoggerFactory.getLogger(ServerReadChannel.class);

    private final Socket socket;
    private final AuctionItemsList auctionItemsList;
    private User user;
    private AbstractUsersList usersList;
    private ObjectMapper objectMapper;

    public ServerReadChannel(
            Socket socket,
            AbstractUsersList usersList,
            AuctionItemsList auctionItemsList
    ){

        this.auctionItemsList = auctionItemsList;
        this.usersList = usersList;
        this.socket = socket;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    @Override
    public Command read() throws IOException {
        logger.debug("Waiting for command from client...");
        InputStream reader = socket.getInputStream();
        Command readCommand = objectMapper.readValue(reader,Command.class);
        logger.debug("Read command from client: {}",readCommand.getOpcode());

        return readCommand;
    }

    @Override
    public int handleRead(Command readCommand) throws IOException {
        int statusCode = StatusCode.FATAL_ERROR;

        if(readCommand != null){
            int opcode = readCommand.getOpcode();

            logger.debug("Handling read command: {}", opcode);

            switch(opcode){
                case Opcodes.LOGIN_CLIENT:{
                    logger.debug("Handling read command: LOGIN_USER");
                    User user = ((LoginUserMessage)readCommand.getMessage()).getUser();
                    statusCode = this.usersList.loginUser(user);

                    if(statusCode == StatusCode.NO_ACCOUNT_EXISTS){
                        handleNotExistAccount();
                    }else if(statusCode == StatusCode.SUCCESS){
                        this.user = user;
                    }

                    break;
                }

                case Opcodes.REGISTER_CLIENT:{
                    logger.debug("Handling read command: REGISTER_CLIENT");
                    User user = ((LoginUserMessage)readCommand.getMessage()).getUser();
                    HttpAuthApi httpApi = new HttpAuthApi();
                    statusCode = handleRegisterClient(httpApi.registerUser(user));
                    break;
                }

                case Opcodes.GET_AUCTION_LIST:{
                    logger.debug("Handling read command: GET_AUCTION_LIST");

                    String message = auctionItemsList.itemsListToPrettyString();
                    sendMessageToClient(Opcodes.AUCTION_LIST, message);
                    break;
                }

                case Opcodes.ADD_BID:{
                    logger.debug("Handling read command: ADD_BID");
                    AddBidMessage addBid = (AddBidMessage)readCommand.getMessage();
                    AuctionItem auctionItem = auctionItemsList.findById(addBid.getAuctionItemId());
                    auctionItem.addBidder(user,addBid.getBidValue());
                    break;
                }

                case Opcodes.WINNER_ANNOUNCEMENT:{
                    //TODO
                    break;
                }
            }
        }

        return statusCode;
    }

    @Override
    public void run() {
        try {
            /* Before accepting communication from a client, the client must login/signup to the system */
            if(authenticateClient()){
                boolean isRun = true;
                while (isRun) {
                    int statusCode = handleRead(read());

                    isRun = (statusCode == StatusCode.FATAL_ERROR);
                }
            }
            else{
                logger.error("User {} Cannot be authenticated", user.getUserName());
            }
        }catch(IOException e){
            logger.error("ERROR: {}", e.getMessage());
            e.printStackTrace();
        } finally{
            logger.info("User {} has left the Chat", user.getUserName());
            usersList.removeByUserName(user.getUserName());

            try {socket.close();}
            catch (IOException e) {logger.error("ERROR: {}", e.getMessage());}
        }
    }

    private boolean authenticateClient() throws IOException {

        /* When client is connected send a message to login or signup */
        boolean isClientAuth = false;

        logger.debug("Sending welcome message to client");
        sendMessageToClient(Opcodes.WELCOME, getWelcomeMessage());

        while(!isClientAuth){
            /* wait for client response */
            Command readCommand = read();
            isClientAuth = ((handleRead(readCommand) == StatusCode.SUCCESS));
        }
        return isClientAuth;
    }

    private void handleNotExistAccount() throws IOException {
        logger.debug("Sending message to client that account doesn't exist");
        String message = "No account exists, please register - type 'reg' ";
        sendMessageToClient(Opcodes.REGISTER_CLIENT, message);
    }

    private int handleRegisterClient(HttpResponse response) throws IOException {
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

    private void sendMessageToClient(int opcode, String message) throws IOException {
        MessageToClientMessage messageToClient = new MessageToClientMessage(message);
        Command writeCommand = new Command(opcode, messageToClient);
        OutputStream writer = socket.getOutputStream();
        this.objectMapper.writeValue(writer,writeCommand);
    }

    private String getWelcomeMessage(){
        return "WELCOME TO SILENT AUCTION\n" +
                "Please login or register if you don't have an account: \n" +
                "-> To login type \"login\" and press Enter\n" +
                "-> To register type \"reg\" and press Enter";
    }
}



