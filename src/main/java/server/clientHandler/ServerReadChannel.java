package server.clientHandler;

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

import java.io.*;
import java.net.Socket;


public class ServerReadChannel implements ReadChannel {
    private final static Logger logger = LoggerFactory.getLogger(ServerReadChannel.class);

    private final Socket socket;
    private final HandleReadChannel handleReadChannel;
    private final AuctionItemsList auctionItemsList;
    private final AbstractUsersList usersList;
    private final ObjectMapper mapper;
    private User user;

    public ServerReadChannel(
            Socket socket,
            AbstractUsersList usersList,
            AuctionItemsList auctionItemsList
    ){



        this.socket = socket;
        this.usersList = usersList;
        this.mapper = new ObjectMapper();
        this.auctionItemsList = auctionItemsList;
        this.handleReadChannel = new HandleReadChannel(socket,usersList,auctionItemsList);
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    @Override
    public Command read() throws IOException {

        logger.debug("Waiting for command from client...");
        InputStream reader = socket.getInputStream();

//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(reader));
//        String dataGram = bufferedReader.readLine();
//        PrintHelper.printPrettyInRed(dataGram);

        Command readCommand = mapper.readValue(reader,Command.class);
        logger.debug("Read command from client: {}",readCommand.getOpcode());

        return readCommand;
    }

    @Override
    public int handleRead(Command readCommand) throws IOException {
        int statusCode = StatusCode.FATAL_ERROR;

        if(readCommand != null){
            int opcode = readCommand.getOpcode();

            statusCode = handleReadChannel.handleReadCommand(
                    readCommand.getOpcode(),readCommand.getMessage());
        }
        return statusCode;
    }

    @Override
    public void run() {
        try {
            int statusCode;
            boolean isRun = true;

            while (isRun) {
                statusCode = handleRead(read());

                isRun = ((statusCode != StatusCode.FATAL_ERROR) ||
                        (statusCode != StatusCode.TERMINATE_SESSION));

                logger.debug("IsRun = {}", isRun);
            }
        }catch(IOException e){
            logger.error("{}", e.getMessage());
            e.printStackTrace();
        } finally{
            logger.info("User {} has left the Chat", user.getUserName());
            usersList.removeByUserName(user.getUserName());

            try {socket.close();}
            catch (IOException e) {logger.error("{}", e.getMessage());}
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
        String message = "No account exists, please register (type \"exit\" to exit): ";
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
        logger.debug("Send message opcode {} to Client",opcode);
        MessageToClientMessage messageToClient = new MessageToClientMessage(message);
        Command writeCommand = new Command(opcode, messageToClient);
        OutputStream writer = socket.getOutputStream();
        this.mapper.writeValue(writer,writeCommand);
    }

    private String getWelcomeMessage(){
        return "WELCOME TO SILENT AUCTION\n" +
                "Please login or register if you don't have an account: \n" +
                "-> To login type \"login\" and press Enter\n" +
                "-> To register type \"reg\" and press Enter\n" +
                "To exit type \"exit\" and press Enter";
    }
}



