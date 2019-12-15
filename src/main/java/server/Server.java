package server;

import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import entity.auction.Item;
import file.reader.InputFileReader;
import file.reader.InputFileReaderFactory;
import usersList.AbstractUsersList;
import usersList.UsersList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/*
* Server waits for a client's connection and then sends a data to the client
*/
public class Server {
    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    private static final int THREADS_NUM = 3;
    private static Properties props = null;
    private static AbstractUsersList userList;
    private static AuctionItemsList auctionItemsList;
    private final static String propFilePath = "serverConfig.properties";

    private static ExecutorService threadsClientsPool = Executors.newFixedThreadPool(THREADS_NUM);


    public static void main(String[] args) throws IOException {

        ServerProperties.readConfigPropertiesFile(propFilePath);
        props = ServerProperties.getProperties();
        userList = new UsersList();
        auctionItemsList = getAuctionItemsList();

        runServer();
    }

    private static void runServer() throws IOException {

        /* establish connection with port */
        ServerSocket listener = new ServerSocket(Integer.parseInt((String)(props.get("server.port"))));

        while(true) {
            /* wait for client connection */
            Socket client = listener.accept();
            logger.info("[{}] Joined in",client.getPort());

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
}
