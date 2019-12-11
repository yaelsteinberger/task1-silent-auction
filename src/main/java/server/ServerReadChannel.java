package server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import entity.User;
import entity.channels.ReadChannel;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.LoginUserMessage;
import entity.command.schemas.MessageToClientMessage;
import entity.response.AbstractResponse;
import entity.response.ResponseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.authenticate.HttpAuthApi;
import services.authenticate.HttpStatusCode;
import usersList.AbstractUsersList;
import usersList.StatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ServerReadChannel implements ReadChannel {
    private final static Logger logger = LoggerFactory.getLogger(ServerReadChannel.class);

    private final Socket socket;
    private String userName;
    private AbstractUsersList usersList;
    private ObjectMapper objectMapper;
    private Command readCommand;
    private Command writeCommand;



    public ServerReadChannel(Socket socket, AbstractUsersList usersList ){
        this.usersList = usersList;
        this.socket = socket;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    @Override
    public void read() throws IOException {
        logger.debug("Waiting for command from client...");
        InputStream reader = socket.getInputStream();
        this.readCommand = objectMapper.readValue(reader,Command.class);
        logger.debug("Read command from client: {}",this.readCommand.getOpcode());
    }

    @Override
    public int handleRead() throws IOException {
        int statusCode = StatusCode.FATAL_ERROR;

        if(this.readCommand != null){
            int opcode = this.readCommand.getOpcode();

            logger.debug("Handling read command: {}", opcode);

            switch(opcode){
                case Opcodes.LOGIN_CLIENT:{
                    logger.debug("Handling read command: LOGIN_USER");
                    User user = ((LoginUserMessage)this.readCommand.getMessage()).getUser();
                    User userToAdd = new User(
                            user.getUserName(),
                            user.getFirstName(),
                            user.getLastName()
                    );
                    statusCode = this.usersList.loginUser(userToAdd);

                    if(statusCode == StatusCode.NO_ACCOUNT_EXISTS){
                        handleNotExistAccount();
                    }else if(statusCode == StatusCode.SUCCESS){
                        this.userName = user.getUserName();
                    }

                    break;
                }

                case Opcodes.REGISTER_CLIENT:{
                    logger.debug("Handling read command: REGISTER_CLIENT");
                    User user = ((LoginUserMessage)this.readCommand.getMessage()).getUser();
                    User userToAdd = new User(
                            user.getUserName(),
                            user.getFirstName(),
                            user.getLastName()
                    );
                    HttpAuthApi httpApi = new HttpAuthApi();
                    handleRegisterClient(httpApi.registerUser(user));
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
                while (true) {
                    read();
                    handleRead();
                }
            }
            else{
                logger.error("User {} Cannot be authenticated", userName);
            }
        }catch(IOException e){
            logger.error("ERROR: {}", e.getMessage());
            e.printStackTrace();
        } finally{
            logger.info("User {} has left the Chat", userName);
            usersList.removeByUserName(userName);

            try {socket.close();}
            catch (IOException e) {logger.error("ERROR: {}", e.getMessage());}
        }
    }

    private boolean authenticateClient() throws IOException {

        /* When client is connected send a message to login or signup */
        boolean isClientAuth = false;

        logger.debug("Sending request to client to login/signup");
        String message = "WELCOME TO SILENT AUCTION\n" +
                "Please login or register if you don't have an account: \n" +
                "-> To login type \"login\" and press Enter\n" +
                "-> To register type \"reg\" and press Enter";
        sendMessageToClient(Opcodes.WELCOME, message);

        while(!isClientAuth){
            /* wait for client response */
            read();
            isClientAuth = ((handleRead() == StatusCode.SUCCESS));
        }
        return isClientAuth;
    }

    private void handleNotExistAccount() throws IOException {
        logger.debug("Sending message to client that account doesn't exist");
        String message = "No account exists, please register - type 'reg' ";
        sendMessageToClient(Opcodes.REGISTER_CLIENT, message);
    }

    private void handleRegisterClient(AbstractResponse response) throws IOException {
        int statusCode = StatusCode.SUCCESS;

        if(response.isError()){
            ResponseError errorResponse = (ResponseError) response;

            switch (errorResponse.getStatusCode()){
                case HttpStatusCode.FORBIDDEN: {
                    statusCode = StatusCode.ACCOUNT_ALREADY_EXISTS;

                    logger.error("Cannot signup client - Account already exists");
                    String message = "You are trying to signup to an existing account\nIf you cannot login, then contact the administrators";
                    sendMessageToClient(Opcodes.ACTION_FAILED, message);
                    break;
                }
            }
        }
    }

    private void sendMessageToClient(int opcode, String message) throws IOException {
        MessageToClientMessage messageToClient = new MessageToClientMessage(message);
        this.writeCommand = new Command(opcode, messageToClient);
        OutputStream writer = socket.getOutputStream();
        this.objectMapper.writeValue(writer,this.writeCommand);
    }
}



