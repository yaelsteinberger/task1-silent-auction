package client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.BaseMessage;
import entity.command.schemas.MessageToClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelReadServices {
    private final static Logger logger = LoggerFactory.getLogger(ChannelReadServices.class);

    private final Socket socket;
    private final String userId;
    private User clientIdentityDetails;
    private ChannelWriteServices channelWriteServices;
    private ObjectMapper mapper;

    /* Create stream to read from keyboard */
    private BufferedReader kbd;

    public ChannelReadServices(
            Socket socket,
            User clientIdentityDetails) throws IOException {
        this.socket = socket;
        this.userId = String.valueOf(socket.getLocalPort());
        this.clientIdentityDetails = clientIdentityDetails;
        this.channelWriteServices = new ChannelWriteServices(socket, clientIdentityDetails);
        this.mapper = new ObjectMapper();
        this.kbd = new BufferedReader(new InputStreamReader(System.in));
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    public void handleClientConnectionEvent() throws IOException {
        logger.debug("Sending CLIENT_CONNECTED to Server");
        this.channelWriteServices.handleWrite(Opcodes.CLIENT_CONNECTED);
    }

    public int handleWelcomeMessage(MessageToClientMessage message) throws IOException, InterruptedException {

        /* Display on screen the welcome message and wait for user input */
        System.err.println(message);

        /* get from user an input */
        int opcode = getUserInputOpcodeCommand();

        if(opcode == Opcodes.REGISTER_CLIENT){
            opcode = getUserRegistrationDetails();
        }

        return this.channelWriteServices.handleWrite(opcode);
    }

    public int handleRegisterClientMessage(MessageToClientMessage message) throws IOException {
        /* Display on screen the message and wait for user input */
        System.err.println(message);

        int opcode = getUserRegistrationDetails();

        return this.channelWriteServices.handleWrite(opcode);
    }

    public void handleLoginSuccessMessage(MessageToClientMessage message){
        /* Display on screen the message and wait for user input */
        System.err.println(message);
    }

    private int getUserRegistrationDetails() throws IOException {
        AtomicInteger opcode = new AtomicInteger(Opcodes.NONE);

        HashMap<String,String> userDetails = new HashMap<>(){{
            put("firstName","Enter First Name: ");
            put("lastName","Enter Last Name: ");
            put("userName","Enter Chosen UserName: ");
        }};

        userDetails.forEach((key,value) -> {
            if(opcode.get() != Opcodes.EXIT){
                try {
                    /* Display request */
                    System.err.println(value);

                    /* get input from user */
                    String dataGram = kbd.readLine();

                    /* check input isn't to exit */
                    if(parseUserInputCommand(dataGram) == Opcodes.EXIT){
                        opcode.set(Opcodes.EXIT);
                    }else{
                        userDetails.put(key,dataGram);
                    }

                } catch (IOException e) {
                    logger.error(e.getMessage());
//                e.printStackTrace();
                }
            }
        });

        if(opcode.get() != Opcodes.EXIT){
            clientIdentityDetails = mapper.convertValue(userDetails, User.class);
            opcode.set(Opcodes.REGISTER_CLIENT);
        }

        return opcode.get();

    }

    private int getUserInputOpcodeCommand() throws IOException, InterruptedException {
        int opcode;
        String dataGram;

        while(true) {
            /* get input from keyboard */
            Thread.sleep(500);
            System.out.println("[YOU] > ");
            dataGram = kbd.readLine();

            opcode = parseUserInputCommand(dataGram);

            if(opcode != Opcodes.NONE) break;

            System.out.println("Unrecognised command, please try again... ");

        }
        return opcode;
    }

    private int parseUserInputCommand(String command) {
        int opcode = Opcodes.NONE;

        opcode =
                command.equals("exit") ? Opcodes.EXIT : (
                        command.contains("login") ? Opcodes.LOGIN_CLIENT : (
                                command.contains("reg") ? Opcodes.REGISTER_CLIENT : opcode));

        /* DEBUG */
        opcode = command.equals("debug") ? Opcodes.DEBUG : opcode;
        return opcode;
    }
}



