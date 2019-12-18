package client;

import MOCKs.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.BaseMessage;
import entity.command.schemas.EmptyMessage;
import entity.command.schemas.LoginUserMessage;
import entity.command.schemas.MessageToClientMessage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerProperties;
import util.PrintHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChannelWriteServicesTest {
    private final static Logger logger = LoggerFactory.getLogger(ChannelWriteServicesTest.class);

    private static Properties props;
    private static Socket client;
    private static ChannelWriteServices channelWriteServices;
    private static ObjectMapper mapper;
    private static ExecutorService serverThread;
    private static User testUser;



    @BeforeClass
    public static void setup() throws IOException, InterruptedException {

        //Given
        serverThread = Executors.newFixedThreadPool(1);
        String propFilePath = "src\\test\\resources\\mockConfig.properties";
        ServerProperties.readConfigPropertiesFile(propFilePath);
        props = ServerProperties.getProperties();
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        /* start server thread */
        int port = Integer.parseInt((String) (props.get("server.port")));
        serverThread.execute(new MockServer(port));

        /* open client socket */
        MockSocketTarget mockSocketTarget = new MockSocketTarget();
        mockSocketTarget.openSocketToSource();
        client = mockSocketTarget.getSocket();

        testUser = new User("username","user","name");
        channelWriteServices = new ChannelWriteServices(client,testUser);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        Command command = new Command(Opcodes.EXIT,new EmptyMessage());
        OutputStream writer = client.getOutputStream();
        mapper.writeValue(writer,command);

        client.close();
    }


    @Test
    public void sendMessageToServerTest() throws IOException {
        // When
        channelWriteServices.sendMessageToServer(Opcodes.CLIENT_CONNECTED,new EmptyMessage());

        // Then
        InputStream reader = client.getInputStream();
        Command command = mapper.readValue(reader,Command.class);

        int expectedOpcode = Opcodes.CLIENT_CONNECTED;
        BaseMessage expectedMessage = new EmptyMessage();

        assertThat(command.getOpcode(), is(expectedOpcode));
        assertThat(command.getMessage().getClass().toString(), is(expectedMessage.getClass().toString()));
    }

    @Test
    public void loginMessageTest() throws IOException {
        // When
        channelWriteServices.handleWrite(Opcodes.LOGIN_CLIENT);

        InputStream reader = client.getInputStream();
        Command command = mapper.readValue(reader,Command.class);
        LoginUserMessage message = (LoginUserMessage) command.getMessage();
        User user = message.getUser();

        //Then
        int expectedOpcode = Opcodes.LOGIN_CLIENT;
        assertThat(command.getOpcode(), is(expectedOpcode));
        assertThat(user.getUserName(), is(testUser.getUserName()));
        assertThat(user.getFirstName(), is(testUser.getFirstName()));
        assertThat(user.getLastName(), is(testUser.getLastName()));
    }

    @Test
    public void registerMessageTest() throws IOException {
        // When
        channelWriteServices.handleWrite(Opcodes.REGISTER_CLIENT);

        InputStream reader = client.getInputStream();
        Command command = mapper.readValue(reader,Command.class);
        LoginUserMessage message = (LoginUserMessage) command.getMessage();
        User user = message.getUser();

        //Then
        int expectedOpcode = Opcodes.REGISTER_CLIENT;
        assertThat(command.getOpcode(), is(expectedOpcode));
        assertThat(user.getUserName(), is(testUser.getUserName()));
        assertThat(user.getFirstName(), is(testUser.getFirstName()));
        assertThat(user.getLastName(), is(testUser.getLastName()));
    }
}
