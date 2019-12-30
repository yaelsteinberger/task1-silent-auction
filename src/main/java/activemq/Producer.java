package activemq;

import client.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.command.Command;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Producer implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(Producer.class);
    private final static String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private final static String QUEUE_NAME = "AUCTION.BIDDING";
    private final ActiveMQConnectionFactory connectionFactory;

    private Command command;

    public Producer(Command command) {
        this.command = command;
        connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
    }

    @Override
    public void run() {
        Connection connection =null;
        Session session = null;
        try {

            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(QUEUE_NAME);

            MessageProducer messageProducer = session.createProducer(destination);
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            ObjectMapper mapper = new ObjectMapper();
            Map obj = mapper.convertValue(command, HashMap.class);

            ObjectMessage message = session.createObjectMessage((Serializable) obj);
            messageProducer.send(message);


        } catch (JMSException e) {
            e.printStackTrace();
        }
        finally {
            try {
                session.close();
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
