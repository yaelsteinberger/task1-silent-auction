package client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.command.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ChannelReadServices {
    private final static Logger logger = LoggerFactory.getLogger(ChannelReadServices.class);

    private final Socket socket;
    private final String userId;
    private ChannelWriteServices channelWriteServices;
    private ObjectMapper mapper;

    /* Create stream to read from keyboard */
    private BufferedReader kbd;

    public ChannelReadServices(Socket socket) throws IOException {
        this.socket = socket;
        this.userId = String.valueOf(socket.getLocalPort());
        this.channelWriteServices = new ChannelWriteServices(socket);
        this.mapper = new ObjectMapper();
        this.kbd = new BufferedReader(new InputStreamReader(System.in));
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    public void handleClientConnectionEvent() throws IOException {
        logger.debug("Sending CLIENT_CONNECTED to Server");
        this.channelWriteServices.handleWrite(Opcodes.CLIENT_CONNECTED, null);
    }

    public int handleWelcomeMessage() throws IOException, InterruptedException {

        /* get from user an input */
        int opcode = (int) getUserRequest().get("opcode");


        if(opcode == Opcodes.REGISTER_CLIENT){
            return handleRegisterClientMessage();
        }

        return this.channelWriteServices.handleWrite(opcode,null);
    }

    public int handleRegisterClientMessage() throws IOException {
        Map result = getUserRegistrationDetails();
        return this.channelWriteServices.handleWrite((Integer) result.get("opcode"), result.get("data"));
    }

    public int handleUserRequest() throws IOException, InterruptedException {
        logger.debug("Handling User Request...");
        Map result = getUserRequest();

        return this.channelWriteServices.handleWrite(
                (Integer) result.get("opcode"),
                result.get("data"));

    }

    private Map getUserRegistrationDetails() throws IOException {
        Map result = new HashMap();
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
                    if((Integer)parseUserInputCommand(dataGram).get("opcode") == Opcodes.EXIT){
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

            User user = mapper.convertValue(userDetails, User.class);
            result.put("data",user);
            opcode.set(Opcodes.REGISTER_CLIENT);
        }else{
            result.put("data","");
        }

        result.put("opcode",opcode.get());

        return result;
    }

    private Map getUserRequest() throws IOException, InterruptedException {
        Map result;

        String dataGram;

        while(true) {
            /* get input from keyboard */
            Thread.sleep(500);
            System.out.println("[YOU] > ");
            dataGram = kbd.readLine();

            result = parseUserInputCommand(dataGram);

            if((Integer)result.get("opcode") != Opcodes.NONE) break;

            System.out.println("Unrecognised command, please try again... ");
        }

        return result;
    }

    private Map parseUserInputCommand(String command) {
        Map result = new HashMap();

        int opcode = Opcodes.NONE;

        opcode =
                command.equals("exit") ? Opcodes.EXIT : (
                command.contains("login") ? Opcodes.LOGIN_CLIENT : (
                command.contains("reg") ? Opcodes.REGISTER_CLIENT : (
                command.equals("list") ? Opcodes.GET_AUCTION_LIST : (
                command.contains("bid") ? Opcodes.ADD_BID : (
                command.contains("item") ? Opcodes.GET_AUCTION_ITEM : opcode)))));

        Optional inputData = parseInputData(opcode, command);

        if(!inputData.isPresent()){
            opcode = Opcodes.NONE;
        }else{
            result.put("data",inputData.get());
        }

        result.put("opcode",opcode);

        return result;
    }

    private Optional parseInputData(int opcode, String inputData){
        Optional parsedInputData = Optional.empty();

        switch(opcode){
            case Opcodes.LOGIN_CLIENT:{
                String[] arr = inputData.trim().split(" ");

                /* assuming the last cell has the item id */
                String username = arr[arr.length-1];

                if((username.length() > 0 && !username.contentEquals(" "))){
                    parsedInputData = Optional.of(username);
                }else{
                    /* Display error to user */
                    System.err.println("username is invalid\n");
                }
                break;
            }
            case Opcodes.GET_AUCTION_ITEM:{
                String[] arr = inputData.trim().split(" ");

                /* assuming the last cell has the item id */
                String idStr = arr[arr.length-1];

                /* verify the value is a number */
                try{
                    Long itemId = Long.parseLong(idStr);
                    parsedInputData = Optional.of(itemId);
                }
                catch (Exception e){
                    /* Display error to user */
                    System.err.println("Item id is invalid\n");
                }
                break;
            }

            case Opcodes.ADD_BID:{
                Map<String,Long> retInput = new HashMap();
                String[] arr = inputData.trim().split(" ");

                for (String input : arr) {
                    if(input.contains("item:") || input.contains("value:")){
                        String[] inputArr = input.split(":");

                        /* verify the value is a number */
                        try{
                            Long value = Long.parseLong(inputArr[1]);
                            retInput.put(inputArr[0],value);
                        }
                        catch (Exception e){
                            /* Display error to user */
                            System.err.println("Request details are invalid\n");
                            break;
                        }
                    }
                }

                if(retInput.size() == 2){
                    parsedInputData = Optional.of(retInput);
                }

                break;
            }

            default:
                parsedInputData = Optional.of("");

        }

        return parsedInputData;
    }
}



