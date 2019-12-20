package client;
import entity.User;
import entity.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client {
    private final static Logger logger = LoggerFactory.getLogger(Client.class);

    private static final int CHAT_CHANNELS_NUM = 2;// Read and Write
    private static Properties props = null;
//    private static User clientIdentityDetails;
    private static Map<String, Command> commands;
    private final static String propFilePath = "clientConfig.properties";


    public static void main(String[] args) throws IOException {

        ClientProperties.setPropsFromConfigPropertiesFile(propFilePath);
        props = ClientProperties.getProperties();

//        clientIdentityDetails = GenerateUser.generateName(true);

        runClient();
    }

    private static void runClient() throws IOException {

        /* create two thread to be able simultaneously read and write to server */
        ExecutorService executor = Executors.newFixedThreadPool(CHAT_CHANNELS_NUM);

        /* establish a connection with the server */
        Socket socket = new Socket(
                (String)props.get("host.name"),
                Integer.parseInt((String)props.get("server.port"))
        );

        /* **** START COMMUNICATION ***** */
        /* run runnable in threads */
        /* NOTE: to be able to check the Future results independently, cannot
        use invokeAll but Submit*/
        List<Future> results = new ArrayList<Future>(){{
            add(executor.submit(Executors.callable(new ClientReadChannel(socket))));
//            add(executor.submit(Executors.callable(new ClientWriteChannel(socket,clientIdentityDetails))));
        }};

        boolean areAllTasksRunning = true;
        while(areAllTasksRunning){
            for (Future result : results) {
                areAllTasksRunning = areAllTasksRunning && !result.isDone();
            }
        }
        logger.info("User {} has left the chat", /*clientIdentityDetails.getUserName()*/ "Yael");

        socket.close();
        System.exit(0);
    }

    public static void getClientIdentityDetails(){

    }
}
