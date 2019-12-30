package server;

import activemq.Consumer;
import auctionList.AuctionItemsList;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.StatusCode;
import entity.User;
import entity.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.clientHandler.HandleReadChannel;
import usersList.AbstractUsersList;

import java.io.IOException;
import java.net.Socket;
import java.util.stream.Stream;


public class ServerReadActiveMq implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ServerReadActiveMq.class);

    private final Socket socket;
    private final HandleReadChannel handleReadChannel;
    private final AuctionItemsList auctionItemsList;
    private final AbstractUsersList usersList;
    private final Consumer activeMqConsumer;
    private final ObjectMapper mapper;


    public ServerReadActiveMq(
            Socket socket,
            AbstractUsersList usersList,
            AuctionItemsList auctionItemsList
    ){

        this.socket = socket;
        this.usersList = usersList;
        this.mapper = new ObjectMapper();
        this.auctionItemsList = auctionItemsList;
        this.activeMqConsumer = new Consumer();
        this.handleReadChannel = new HandleReadChannel(socket,usersList,auctionItemsList);
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }


    public Stream<Command> readCommandsFromQueue() throws IOException {

        logger.debug("Checking if there are activeMq commands");
        activeMqConsumer.run();

        if(activeMqConsumer.isCommandsExists()){
            return activeMqConsumer.getCommands();
        }

        return null;
    }


    public int handleRead(Stream<Command> commands) throws IOException {

        Integer[] statusCodes = commands
                .filter(command -> command != null)
                .map(command -> {
                    try {
                        return this.handleReadChannel.handleReadCommand(
                                command.getOpcode(),command.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return StatusCode.FATAL_ERROR;
                    }
                })
                .filter(statusCode -> statusCode == StatusCode.FATAL_ERROR)
                .toArray(Integer[]::new);

        return (statusCodes.length == 0) ? StatusCode.SUCCESS : StatusCode.FATAL_ERROR;
    }

    @Override
    public void run() {
        try {
            int statusCode;
            boolean isRun = true;

            while (isRun) {
                Stream commands = readCommandsFromQueue();

                if(commands != null){
                    System.err.println("Got somthing to read");
                    statusCode = handleRead(commands);
                }else{
                    statusCode = StatusCode.SUCCESS;
                }


                isRun = (statusCode != StatusCode.FATAL_ERROR);

                Thread.sleep(3000);
            }
        }catch(IOException | InterruptedException e){
            logger.error("{}", e.getMessage());
            e.printStackTrace();
        } finally{

        }
    }
}



