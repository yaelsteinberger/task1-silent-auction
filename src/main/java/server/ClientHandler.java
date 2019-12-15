package server;

import auctionList.AuctionItemsList;
import usersList.AbstractUsersList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientHandler implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static final int CHANNELS_NUM = 1; // Read
    private final Socket socket;
    private final AbstractUsersList usersList;
    private final AuctionItemsList auctionItemsList;



    public ClientHandler(Socket client, AbstractUsersList usersList, AuctionItemsList auctionItemsList) throws IOException {
        this.socket = client;
        this.usersList = usersList;
        this.auctionItemsList = auctionItemsList;
    }

    @Override
    public void run() {
        /* create two thread to be able simultaneously read and write to server */
        ExecutorService executor = Executors.newFixedThreadPool(CHANNELS_NUM);

        try{
            /* The server reads comm from client and handles the requests */
            Future readResult = executor.submit(Executors.callable(new ServerReadChannel(socket, usersList, auctionItemsList)));

            logger.info("Client [{}] is connected and running on Thread: {}", socket.getPort(), Thread.currentThread().getId());

            /* Will wait until read task is finished */
            readResult.get();

            /* if task is finished meaning the user quite the session (or an error occurred) */
            socket.close();

        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.error("ERROR: {}", e.getMessage());;
        } finally {
            logger.debug("Client [{}] has left the chat", socket.getPort());
        }
    }
}
