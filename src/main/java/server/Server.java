package server;

import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import entity.auction.Item;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.MessageToClientMessage;
import file.reader.InputFileReader;
import file.reader.InputFileReaderFactory;
import server.clientHandler.ClientHandler;
import server.timer.AuctionTimer;
import usersList.AbstractUsersList;
import usersList.UsersList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/*
* Server waits for a client's connection and then sends a data to the client
*/
public class Server {
    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    private static final int THREADS_NUM = 3;
    private static final long TIME = 2;
    private static final TimeUnit TIME_UNIT = TimeUnit.HOURS;
    private static AuctionTimer auctionTimer;
    private static Properties props = null;
    private static AbstractUsersList userList;
    private static AuctionItemsList auctionItemsList;
    private static List<Socket> clientsSockets;
    private final static String propFilePath = "serverConfig.properties";

    private static ExecutorService threadsClientsPool = Executors.newFixedThreadPool(THREADS_NUM);
    private static ExecutorService adminThread = Executors.newFixedThreadPool(1);
    private static TimeLimiter timeLimiter = SimpleTimeLimiter.create(Executors.newFixedThreadPool(1));



    public static void main(String[] args) throws IOException {

        ServerProperties.setPropsFromConfigPropertiesFile(propFilePath);
        props = ServerProperties.getProperties();
        userList = new UsersList();
        auctionItemsList = getAuctionItemsList();
        clientsSockets = new ArrayList();

        runServer();
    }

    private static void runServer() throws IOException {

        /* establish connection with port */
        ServerSocket listener = new ServerSocket(Integer.parseInt((String)(props.get("server.port"))));

        adminThread.execute(new ServerReadActiveMq(null,userList,auctionItemsList));
        auctionTimer = new AuctionTimer(TIME, TIME_UNIT);

        try{
            timeLimiter.runWithTimeout(() -> {
                try {
                    acceptConnectedClients(listener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, TIME,TIME_UNIT);

        }catch (InterruptedException err){
           err.getStackTrace();

        }catch (TimeoutException err){
            System.err.println("TIME IS UP");
            sendMessageToAllSockets();

        }finally {
//            System.err.println("TIME IS UP");
        }
    }

    private static void acceptConnectedClients(ServerSocket listener) throws IOException {
        while(true) {
            /* wait for client connection */
            Socket client = listener.accept();
            logger.info("[{}] Joined in",client.getPort());
            clientsSockets.add(client);

            ClientHandler clientHandler = new ClientHandler(client,userList,auctionItemsList);
            threadsClientsPool.execute(clientHandler);
        }
    }

    public static void createAuctionItemsList() throws IOException {
        /* read auction list from file */
        auctionItemsList = getAuctionItemsList();
    }

    private static AuctionItemsList getAuctionItemsList() throws IOException {
        String filePath = props.getProperty("auctionItems.file");
        InputFileReader fileReader = InputFileReaderFactory.of(filePath);
        Stream<Item> itemsStream = fileReader.readFile();

        return new AuctionItemsList(itemsStream.map(AuctionItem::new));
    }

    public static void sendMessageToAllSockets() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        String message = "Auction time is over!";

        MessageToClientMessage messageToClient = new MessageToClientMessage(message);
        Command writeCommand = new Command(Opcodes.WINNER_ANNOUNCEMENT, messageToClient);

        clientsSockets.forEach(client -> {
            OutputStream writer = null;
            try {
                logger.debug("Send Winner message to socket {}", client.getPort());
                writer = client.getOutputStream();
                mapper.writeValue(writer,writeCommand);
            } catch (IOException e) {
                logger.error("Cannot send message to socket {}",client.getPort());
                //e.printStackTrace();
            }
        });

    }

}
