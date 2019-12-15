package server;

import MOCKs.*;
import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import entity.User;
import entity.auction.Bidder;
import entity.command.Command;
import entity.command.schemas.LoginUserMessage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import usersList.StatusCode;
import usersList.UsersList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ServerReadChannelTest {
//    private static MockAuthServer mockAuthServer;
    private static UsersList usersList;
    private static User[] users;
    private static AuctionItemsList auctionItemsList;
    private static ServerReadChannel serverReadChannel;
    private static Socket client;
    private static Properties props;
    private static ServerSocket listener;



    @BeforeClass
    public static void setup() throws IOException, InterruptedException {

        //Given
        String propFilePath = "src\\test\\resources\\mockConfig.properties";
        ServerProperties.readConfigPropertiesFile(propFilePath);
        props = ServerProperties.getProperties();
        String mockHost = (String) props.get("authServer.host");
        String mockPort = (String) props.get("authServer.port");
        MockAuthServer.setAttributes(mockHost,Integer.parseInt(mockPort));
//        mockAuthServer = new MockAuthServer(mockHost,Integer.parseInt(mockPort));
        MockAuthServer.startServer();
//        mockAuthServer.startServer();

        auctionItemsList = MockAuctionItems.generateAuctionItemsList();
        users = MockUsers.getUsers();

        /* run listener */
        listener = new ServerSocket(Integer.parseInt((String)(props.get("server.port"))));

    }

    @AfterClass
    public static void tearDown() throws IOException {
        MockAuthServer.stopServer();
//        mockAuthServer.stopServer();
        listener.close();
    }

    @Before
    public void cleanServerCache() {
        usersList = new UsersList();

        /* Before each test clear the cache from any previous responses and expectations */
        MockAuthServer.resetServer();
//        mockAuthServer.resetServer();
    }

    @Test
    public void handleReadLoginCommandTest() throws IOException {
        //Given
        Command command = MockCommands.getMockLoginCommand(users[0]);
        Map<HttpStatusCode,Object> expectations = new HashMap<>(){{
            put(HttpStatusCode.OK_200, new HashMap<String,Integer>(){{
                put("sizeBefore",0);
                put("sizeAfter",1);
                put("status",StatusCode.SUCCESS);
            }});
            put(HttpStatusCode.FORBIDDEN_403, new HashMap<String,Integer>(){{
                put("sizeBefore",0);
                put("sizeAfter",0);
                put("status",StatusCode.ACCOUNT_IS_DISABLED);
            }});
            put(HttpStatusCode.NOT_FOUND_404, new HashMap<String,Integer>(){{
                put("sizeBefore",0);
                put("sizeAfter",0);
                put("status",StatusCode.NO_ACCOUNT_EXISTS);
            }});
        }};

        Set<HttpStatusCode> httpStatusCodes = expectations.keySet();

        //When
        for (HttpStatusCode httpStatusCode : httpStatusCodes) {
            usersList = new UsersList();

            Map expectation = (Map)expectations.get(httpStatusCode);
            testReadLoginCommand(command,httpStatusCode,expectation);
        }
    }


    @Test
    public void handleReadRegisterCommandTest() throws IOException {
        //Given
        Command command = MockCommands.getMockRegisterCommand(users[0]);
        Map<HttpStatusCode,Integer> expectations = new HashMap<>(){{
            put(HttpStatusCode.OK_200, StatusCode.REGISTRATION_SUCCESSFUL);
            put(HttpStatusCode.FORBIDDEN_403, StatusCode.ACCOUNT_ALREADY_EXISTS);
        }};

        Set<HttpStatusCode> httpStatusCodes = expectations.keySet();

        //When
        for (HttpStatusCode httpStatusCode : httpStatusCodes) {
            Integer expectedStatus = expectations.get(httpStatusCode);
            testReadRegisterCommand(command,httpStatusCode,expectedStatus);
        }
    }

    @Test
    public void handleReadAddBidCommandTest() throws IOException {

        connectToClientSocket();
        MockAuthServer.resetServer();
//        mockAuthServer.resetServer();

        //Given
        /* first "connect" user to channel by logging in */
        Command command = MockCommands.getMockLoginCommand(users[0]);
        HttpStatusCode httpStatusCode = HttpStatusCode.OK_200;
        MockAuthServer.isUserAuthExpectations(((LoginUserMessage)command.getMessage()).getUser().getUserName(), httpStatusCode);
//        mockAuthServer.isUserAuthExpectations(((LoginUserMessage)command.getMessage()).getUser().getUserName(), httpStatusCode);
        serverReadChannel.handleRead(command);

        /* add few bidders to an acutionItem */
        Long id = 2L;
        Long bidValue = 4600L;
        AuctionItem auctionItem = auctionItemsList.findById(id);
        auctionItem.addBidder(users[0],4150L);
        auctionItem.addBidder(users[2],4300L);
        auctionItem.addBidder(users[1],4450L);

        //When
        command = MockCommands.getMockAddBidCommand(id, bidValue);//Ring item
        serverReadChannel.handleRead(command);
        Bidder lastBidderInList = auctionItemsList.findById(id).getBiddersList().peekLast();

        //Then
        assertThat(lastBidderInList.getBidder(), is(users[0]));
        assertThat(lastBidderInList.getBidderValue(), is(bidValue));

        client.close();
    }

    private void connectToClientSocket() throws IOException {
        /* run client */
        MockClient mockClient = new MockClient();
        mockClient.run();

        /* get client socket */
        client = listener.accept();

        /* create new readChannel */
        serverReadChannel = new ServerReadChannel(client,usersList,auctionItemsList);
    }

    private void testReadLoginCommand(
            Command command,
            HttpStatusCode httpStatusCode,
            Map expectation) throws IOException {

        connectToClientSocket();
        MockAuthServer.resetServer();
//        mockAuthServer.resetServer();

        //Given
        int sizeBefore = usersList.getUsersList().size();
        MockAuthServer.isUserAuthExpectations(((LoginUserMessage)command.getMessage()).getUser().getUserName(), httpStatusCode);
//        mockAuthServer.isUserAuthExpectations(((LoginUserMessage)command.getMessage()).getUser().getUserName(), httpStatusCode);

        //When
        int status = serverReadChannel.handleRead(command);
        int sizeAfter = usersList.getUsersList().size();

        //Then
        assertThat(status, is(expectation.get("status")));
        assertThat(sizeBefore, is(expectation.get("sizeBefore")));
        assertThat(sizeAfter, is(expectation.get("sizeAfter")));

        client.close();
    }


    private void testReadRegisterCommand(
            Command command,
            HttpStatusCode httpStatusCode,
            Integer expectedStatus) throws IOException {

        connectToClientSocket();
        MockAuthServer.resetServer();
//        mockAuthServer.resetServer();

        //Given
        MockAuthServer.registerAuthExpectations(((LoginUserMessage)command.getMessage()).getUser(), httpStatusCode);
//        mockAuthServer.registerAuthExpectations(((LoginUserMessage)command.getMessage()).getUser(), httpStatusCode);

        //When
        int status = serverReadChannel.handleRead(command);

        //Then
        assertThat(status, is(expectedStatus));

        client.close();
    }
}

