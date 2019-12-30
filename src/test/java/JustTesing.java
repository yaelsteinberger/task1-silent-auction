import MOCKs.MockUsers;
import activemq.Consumer;

import activemq.Producer;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.RegisterUserMessage;
import org.junit.Test;
import util.PrintHelper;

import java.util.concurrent.*;


public class JustTesing {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void activemqProducerTest() throws Exception {
        User[] users = MockUsers.getUsers();
        Executor activemq = Executors.newFixedThreadPool(10);
        Command command = new Command(Opcodes.REGISTER_CLIENT,new RegisterUserMessage(users[1]));

        activemq.execute(new Producer(new Command(Opcodes.REGISTER_CLIENT,new RegisterUserMessage(users[0]))));
        activemq.execute(new Producer(new Command(Opcodes.REGISTER_CLIENT,new RegisterUserMessage(users[1]))));
        activemq.execute(new Producer(new Command(Opcodes.REGISTER_CLIENT,new RegisterUserMessage(users[2]))));


        Thread.sleep(4000);
    }

    @Test
    public void activemqConsumerTest() throws Exception {

        Executor activemq = Executors.newFixedThreadPool(10);



    }
}
