package server;

import MOCKs.*;
import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import entity.User;
import entity.auction.Bidder;
import entity.command.Command;
import entity.command.schemas.LoginUserMessage;
import entity.command.schemas.RegisterUserMessage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import server.clientHandler.ServerReadChannel;
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
    private static UsersList usersList;
    private static User[] users;
    private static AuctionItemsList auctionItemsList;
    private static ServerReadChannel serverReadChannel;
    private static Socket client;
//    private static Properties props;
    private static ServerSocket listener;



    @BeforeClass
    public static void setup() throws IOException, InterruptedException {

        //Given
//        String propFilePath = "src\\test\\resources\\mockConfig.properties";
//        ServerProperties.setPropsFromConfigPropertiesFile(propFilePath);
//        props = ServerProperties.getProperties();
//        String mockHost = (String) props.get("authServer.host");
//        String mockPort = (String) props.get("authServer.port");
//        MockAuthServer.setAttributes(mockHost,Integer.parseInt(mockPort));
        MockAuthServer.startServer();

        auctionItemsList = MockAuctionItems.generateAuctionItemsList();
        users = MockUsers.getUsers();

        /* run listener */
        listener = new ServerSocket(MockTestProperties.getServerPort());

    }

    @AfterClass
    public static void tearDown() throws IOException {
        MockAuthServer.stopServer();
        listener.close();
    }

    @Before
    public void cleanServerCache() {
        usersList = new UsersList();

        /* Before each test clear the cache from any previous responses and expectations */
        MockAuthServer.resetServer();
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
            runTestReadLoginCommand(users[0], command,httpStatusCode,expectation);
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

        //Given
        /* first "connect" user to channel by logging in */
        Command command = MockCommands.getMockLoginCommand(users[0]);
        HttpStatusCode httpStatusCode = HttpStatusCode.OK_200;
        MockAuthServer.isUserAuthExpectations(((RegisterUserMessage)command.getMessage()).getUser(), httpStatusCode);
        serverReadChannel.handleRead(command);

        /* add few bidders to an acutionItem */
        Long id = 2L;
        Long bidValue = 4600L;
        AuctionItem auctionItem = auctionItemsList.findById(id);
        auctionItem.addBidder(users[0],4150L);
        auctionItem.addBidder(users[2],4300L);
        auctionItem.addBidder(users[1],4450L);

        //When - Success
        command = MockCommands.getMockAddBidCommand(id, bidValue);//Ring item
        int successStatus = serverReadChannel.handleRead(command);
        Bidder bidderSuccess = auctionItemsList.findById(id).getBiddersList().peekLast();

        //When - Fail
        Long newValue = 4750L;
        auctionItem.addBidder(users[1],newValue);
        command = MockCommands.getMockAddBidCommand(id, bidValue);//Ring item
        int failStatus = serverReadChannel.handleRead(command);
        Bidder bidderFail = auctionItemsList.findById(id).getBiddersList().peekLast();

        //Then
        int expectedSuccessStatus = StatusCode.SUCCESS;
        int expectedFailStatus = StatusCode.INVALID_VALUE;
        assertThat(successStatus, is(expectedSuccessStatus));
        assertThat(bidderSuccess.getBidder(), is(users[0]));
        assertThat(bidderSuccess.getBidderValue(), is(bidValue));
        assertThat(failStatus, is(expectedFailStatus));
        assertThat(bidderFail.getBidder(), is(users[1]));
        assertThat(bidderFail.getBidderValue(), is(newValue));

        client.close();
    }

    private void connectToClientSocket() throws IOException {
        /* run client */
        MockSocketTarget mockClient = new MockSocketTarget();
        mockClient.openSocketToSource();

        /* get client socket */
        client = listener.accept();

        /* create new readChannel */
        serverReadChannel = new ServerReadChannel(client,usersList,auctionItemsList);
    }

    private void runTestReadLoginCommand(
            User user,
            Command command,
            HttpStatusCode httpStatusCode,
            Map expectation) throws IOException {

        connectToClientSocket();
        MockAuthServer.resetServer();

        //Given
        int sizeBefore = usersList.getUsersList().size();
        MockAuthServer.isUserAuthExpectations(user, httpStatusCode);

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

        //Given
        MockAuthServer.registerAuthExpectations(((RegisterUserMessage)command.getMessage()).getUser(), httpStatusCode);

        //When
        int status = serverReadChannel.handleRead(command);

        //Then
        assertThat(status, is(expectedStatus));

        client.close();
    }
}

