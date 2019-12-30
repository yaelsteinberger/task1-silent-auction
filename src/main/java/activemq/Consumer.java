package activemq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import entity.command.Command;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.collections4.list.SetUniqueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.beans.ExceptionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

public class Consumer implements Runnable, ExceptionListener {
    private final static Logger logger = LoggerFactory.getLogger(Producer.class);
    private final static String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private final static String QUEUE_NAME = "AUCTION.BIDDING";
    private final ActiveMQConnectionFactory connectionFactory;
    private final MessageConsumerReader meassageConsumerReader;

    public Consumer() {
        this.connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        this.meassageConsumerReader = new MessageConsumerReader();
    }

    public boolean isCommandsExists(){
        return meassageConsumerReader.getCommandsQueue().size() > 0;
    }

    public Stream<Command> getCommands(){
        Collection<Command> commands = new ArrayList();
        meassageConsumerReader.getCommandsQueue().drainTo(commands);

        return commands.stream();
    }

    @Override
    public void run() {

        Connection connection = null;
        Session session = null;
        MessageConsumer messageConsumer = null;
        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(QUEUE_NAME);
            messageConsumer = session.createConsumer(destination);

            messageConsumer.setMessageListener(meassageConsumerReader);

            connection.start();

        } catch (JMSException e) {
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
    public void exceptionThrown(Exception e) {

    }
}

class MessageConsumerReader implements MessageListener {
    private List<Command> commandsFromQueue = new ArrayList();
    private LinkedBlockingDeque<Command> commandsQueue = new LinkedBlockingDeque();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message) {

        try {
            if (message instanceof ObjectMessage) {

                Command command = mapper.convertValue(((ObjectMessage) message).getObject(),Command.class);
                commandsQueue.add(command);

            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public LinkedBlockingDeque<Command> getCommandsQueue() {
        return commandsQueue;
    }
}



