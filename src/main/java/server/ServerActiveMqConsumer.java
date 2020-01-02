package server;

import activemq.Consumer;
import auctionList.AuctionItemsList;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.StatusCode;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.AddBidMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.clientHandler.ChannelServices;
import server.clientHandler.HandleReadChannel;
import usersList.AbstractUsersList;
import util.PrintHelper;

import javax.jms.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;


public class ServerActiveMqConsumer implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ServerActiveMqConsumer.class);

    private final Socket socket;
    private final HandleReadChannel handleReadChannel;
    private final AuctionItemsList auctionItemsList;
    private final AbstractUsersList usersList;
    private final Consumer activeMqConsumer;
    private final ObjectMapper mapper;

    /*******************************/
    private final static String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private final static String QUEUE_NAME = "AUCTION.BIDDING";
    private final ActiveMQConnectionFactory connectionFactory;
    private final HandleReadQueueMessages handleReadQueueMessages;
    private final CyclicBarrier cyclicBarrier;

    public ServerActiveMqConsumer(
            Socket socket,
            AbstractUsersList usersList,
            AuctionItemsList auctionItemsList,
            CyclicBarrier cyclicBarrier
    ) throws IOException {

        this.socket = socket;
        this.usersList = usersList;
        this.mapper = new ObjectMapper();
        this.auctionItemsList = auctionItemsList;
        this.activeMqConsumer = new Consumer();
        this.handleReadChannel = new HandleReadChannel(socket,usersList,auctionItemsList);
        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        /*******************************/
        this.connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        this.handleReadQueueMessages = new HandleReadQueueMessages(auctionItemsList,usersList);
        this.cyclicBarrier = cyclicBarrier;
    }


    public void runConnection() throws IOException {

        Session session = null;
        MessageConsumer messageConsumer = null;
        Connection connection = null;
        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(QUEUE_NAME);
            messageConsumer = session.createConsumer(destination);

            messageConsumer.setMessageListener(this.handleReadQueueMessages);

            connection.start();

            /* the best solution to hold the thread instead of Thread.sleep() */
            cyclicBarrier.await();

        } catch (JMSException | InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
            try {
                messageConsumer.close();
                session.close();
                connection.close();
            } catch (JMSException err) {
                err.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            runConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class HandleReadQueueMessages implements MessageListener {
    private final static Logger logger = LoggerFactory.getLogger(HandleReadQueueMessages.class);
    private final AuctionItemsList auctionItemsList;
    private final AbstractUsersList usersList;
    //private final ChannelServices channelServices;

    private ObjectMapper mapper = new ObjectMapper();

    public HandleReadQueueMessages(AuctionItemsList auctionItemsList, AbstractUsersList usersList) {
        this.auctionItemsList = auctionItemsList;
        this.usersList = usersList;
    }


    @Override
    public void onMessage(Message message) {
        logger.info("Getting message from Queue");
        try {
            if (message instanceof ObjectMessage) {
                Command command = mapper.convertValue(((ObjectMessage) message).getObject(), Command.class);
                handleCommand(command);
            }

        } catch (JMSException | IOException e) {
            e.printStackTrace();
        }
    }

    public int handleCommand(Command command) throws IOException {
        PrintHelper.printPrettyInRed(command);

        if(command.getOpcode() == Opcodes.ADD_BID){
            AddBidMessage message = mapper.convertValue(command.getMessage(),AddBidMessage.class);
            Socket socket = usersList.findByUserName(message.getUserId()).getSocket();
            ChannelServices channelServices = new ChannelServices(socket,usersList,auctionItemsList);
            channelServices.handleAddBid(message);
        }

        return StatusCode.SUCCESS;
    }
}



