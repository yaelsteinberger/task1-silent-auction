package server.clientHandler;

import auctionList.AuctionItemsList;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import entity.User;
import entity.channels.ReadChannel;
import entity.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
}



