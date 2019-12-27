package client.channelHandler;
import client.entity.OpcodeCommandsQuestions;
import client.entity.Question;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entity.StatusCode;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChannelReadServices {
    private final static Logger logger = LoggerFactory.getLogger(ChannelReadServices.class);

    private final Socket socket;
    private ObjectMapper mapper;

    /* Create stream to read from keyboard */
    private BufferedReader kbd;

    public ChannelReadServices(Socket socket) {
        this.socket = socket;
        this.mapper = new ObjectMapper();
        this.kbd = new BufferedReader(new InputStreamReader(System.in));
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    public void handleClientConnectionEvent() throws IOException {
        logger.debug("Sending CLIENT_CONNECTED to Server");
        sendMessageToServer(Opcodes.CLIENT_CONNECTED, new EmptyMessage());
    }

    public int handleUserRequest() throws IOException, InterruptedException {
        logger.debug("Handling User Request...");

        /* get user input */
        Map result = getUserRequest();
        int opcode = (Integer)result.get("opcode");
        int statusCode = StatusCode.SUCCESS;

        BaseMessage message = convertResultObjectToMessageObject(
                opcode,
                (Map)result.get("data"));

        if(opcode != Opcodes.EXIT ) {
            sendMessageToServer(opcode, message);
        }
        else{
            statusCode =  StatusCode.TERMINATE_SESSION;
        }

        return statusCode;
    }

    private void sendMessageToServer(int opcode, BaseMessage message) throws IOException {
        logger.debug("Send message opcode {} to Server",opcode);
        Command writeCommand = new Command(opcode,message);
        OutputStream writer = socket.getOutputStream();
        this.mapper.writeValue(writer,writeCommand);
    }

    private Map getUserRequest() throws IOException, InterruptedException {
        Map data = new HashMap();
        Map userRequest = new HashMap(){{
            put("opcode",Opcodes.EXIT);
            put("data",new EmptyMessage());
        }};

        Integer statusCode = StatusCode.MENU;
        OpcodeCommandsQuestions result = null;
        Integer totalQuestions = 0;
        Integer questionNum = 0;
        Question currentQuestion = null;
        boolean isRun = true;
        String dataGram = "";

        while(isRun) {
            /* get input from keyboard */
            Thread.sleep(200);

            switch(statusCode){
                case StatusCode.MENU:{
                    System.out.println("[YOU] > ");
                    dataGram = kbd.readLine().trim();

                    result = ParseUserInputData.parseOpcodeFromInputData(dataGram);
                    statusCode = StatusCode.START_PROCESS;
                    if(result.opcode() == Opcodes.NONE){
                        System.out.println("Unrecognised command, please try again... ");
                        statusCode = StatusCode.MENU;
                    }
                    break;
                }
                case StatusCode.START_PROCESS:{
                    statusCode = StatusCode.END_PROCESS;

                    /* ask questions if needed */
                    if(result.questions() != null) {
                        totalQuestions = result.questions().size();
                        statusCode = StatusCode.IN_PROCESS_ASK;
                    }
                    break;
                }
                case StatusCode.IN_PROCESS_ASK:{
                    /* ask a question */
                    currentQuestion = result.questions().get(questionNum);
                    String ask =  currentQuestion.getQuestion();
                    System.err.println(ask);
                    dataGram = kbd.readLine().trim();

                    statusCode = StatusCode.IN_PROCESS_ANSWER;
                    break;
                }
                case StatusCode.IN_PROCESS_ANSWER:{
                    /* ask a question */
                    if(ParseUserInputData.validateUserInputFromQuestion(dataGram,
                            currentQuestion.getAnswerTypeClass())) {
                        data.put(currentQuestion.getAnswerLabel(),dataGram);
                        questionNum++;
                        statusCode = (questionNum == totalQuestions) ?
                                StatusCode.END_PROCESS : StatusCode.IN_PROCESS_ASK;
                    }else {
                        System.out.println("Invalid input, please try again... ");
                        statusCode = StatusCode.IN_PROCESS_ASK;
                    }
                    break;
                }
                case StatusCode.END_PROCESS:{
                    userRequest.put("opcode",result.opcode());
                    userRequest.put("data",data);
                    statusCode = StatusCode.EXIT_PROCESS;
                    break;
                }

            }
            isRun = !((result.opcode() == Opcodes.EXIT) || (statusCode == StatusCode.EXIT_PROCESS));
        }

        return userRequest;
    }

    private BaseMessage convertResultObjectToMessageObject(int opcode,Map data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        BaseMessage message;

        switch(opcode){
            case Opcodes.LOGIN_CLIENT:{
                data.put("type","LoginUserMessage");
                message = mapper.convertValue(data, LoginUserMessage.class);
                break;
            }
            case Opcodes.REGISTER_CLIENT:{
                Map tempData = new HashMap();
                tempData.put("user",mapper.convertValue(data, User.class));
                tempData.put("type", "RegisterUserMessage");
                message = mapper.convertValue(tempData, RegisterUserMessage.class);
                break;
            }
            case Opcodes.GET_AUCTION_ITEM:{
                data.put("type","GetAuctionItemMessage");
                message = mapper.convertValue(data, GetAuctionItemMessage.class);
                break;
            }
            case Opcodes.ADD_BID:{
                data.put("type", "AddBidMessage");
                message = mapper.convertValue(data, AddBidMessage.class);
                break;
            }
            default:
                message = new EmptyMessage();
        }

        return message;
    }
}



