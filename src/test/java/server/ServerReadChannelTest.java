package server;

import MOCKs.*;
import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.RegisterUserMessage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import server.clientHandler.ServerReadChannel;
import entity.StatusCode;
import usersList.UsersList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ServerReadChannelTest {
    private static UsersList usersList;
    private static User[] users;
    private static AuctionItemsList auctionItemsList;
    private static ServerReadChannel serverReadChannel;
    private static MockSocket mockClientSocket;


    @BeforeClass
    public static void setup() throws IOException {

        //Given
        MockAuthServer.startServer();

        auctionItemsList = MockAuctionItems.generateAuctionItemsList();
        users = MockUsers.getUsers();

        mockClientSocket = new MockSocket();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        MockAuthServer.stopServer();
    }

    @Before
    public void cleanServerCache() throws IOException {
        usersList = new UsersList();

        /* Before each test clear the cache from any previous responses and expectations */
        MockAuthServer.resetServer();
        connectToClientMockSocket();
    }

    private void connectToClientMockSocket() throws IOException {
        serverReadChannel = new ServerReadChannel(mockClientSocket,usersList,auctionItemsList);
    }

    private void generateBiddersListInAuctionItem(Long itemId){
        AuctionItem auctionItem = auctionItemsList.findById(itemId);
        auctionItem.addBidder(users[0],4150L);
        auctionItem.addBidder(users[2],4300L);
        auctionItem.addBidder(users[1],4450L);
    }


    @Test
    public void testConnectedCommand() throws IOException {

        //Given
        Command command = MockCommands.getMockConnectedCommand();

        //When
        int status = serverReadChannel.handleRead(command);

        //Then
        int expectedStatus = StatusCode.SUCCESS;
        assertThat(status, is(expectedStatus));
    }

    @Test
    public void handleReadLoginCommandTest() throws IOException {
        //Given
        Command command = MockCommands.getMockLoginCommand(users[0]);
        Command commandInvalidUserName = MockCommands.getMockLoginCommand(
                new User("admini", "FirstName", "LastName"));
        Map<HttpStatusCode,Object> expectations = new HashMap<>(){{
            put(HttpStatusCode.OK_200, new HashMap<String,Object>(){{
                put("command",command);
                put("sizeBefore",0);
                put("sizeAfter",1);
                put("status",StatusCode.SUCCESS);
                put("opcodeToClient",Opcodes.LOGIN_SUCCESS);
            }});
            put(HttpStatusCode.FORBIDDEN_403, new HashMap<String,Object>(){{
                put("command",command);
                put("sizeBefore",0);
                put("sizeAfter",0);
                put("status",StatusCode.ACCOUNT_IS_DISABLED);
                put("opcodeToClient",Opcodes.ACTION_FAILED);
            }});
            put(HttpStatusCode.NOT_FOUND_404, new HashMap<String,Object>(){{
                put("command",command);
                put("sizeBefore",0);
                put("sizeAfter",0);
                put("status",StatusCode.NO_ACCOUNT_EXISTS);
                put("opcodeToClient",Opcodes.REGISTER_CLIENT);
            }});
            put(HttpStatusCode.OK_200, new HashMap<String,Object>(){{
                put("command",commandInvalidUserName);
                put("sizeBefore",0);
                put("sizeAfter",0);
                put("status",StatusCode.INVALID_USERNAME);
                put("opcodeToClient",Opcodes.LOGIN_CLIENT);
            }});
        }};

        Set<HttpStatusCode> httpStatusCodes = expectations.keySet();

        //When
        for (HttpStatusCode httpStatusCode : httpStatusCodes) {

            /* restart usersList so MockSocket will connect with an empty one */
            usersList = new UsersList();
            connectToClientMockSocket();
            MockAuthServer.resetServer();

            Map expectation = (Map)expectations.get(httpStatusCode);
            runTestReadLoginCommand(users[0], httpStatusCode,expectation);
        }
    }


    @Test
    public void handleReadRegisterCommandTest() throws IOException {
        //Given
        Command command = MockCommands.getMockRegisterCommand(users[0]);
        Map<HttpStatusCode,Object> expectations = new HashMap<>(){{
            put(HttpStatusCode.OK_200,
                    new HashMap(){{
                        put("status",StatusCode.REGISTRATION_SUCCESSFUL);
                        put("opcodeToClient",Opcodes.WELCOME);
                    }});
            put(HttpStatusCode.FORBIDDEN_403,
                    new HashMap(){{
                        put("status",StatusCode.ACCOUNT_ALREADY_EXISTS);
                        put("opcodeToClient",Opcodes.ACTION_FAILED);
                    }});
        }};

        Set<HttpStatusCode> keys = expectations.keySet();

        //When
        for (HttpStatusCode key : keys) {
            connectToClientMockSocket();
            MockAuthServer.resetServer();

            Map expectation = (Map) expectations.get(key);
            testReadRegisterCommand(command,key,expectation);
        }
    }

    @Test
    public void handleGetAuctionItemsListTest() throws IOException {

        //Given
        Command command = MockCommands.getMockGetAuctionListCommand();

        // When
        serverReadChannel.handleRead(command);
        Command commandToClient = mockClientSocket.getOutPutStreamCommand();

        // Then
        int expectedOpcode = Opcodes.AUCTION_LIST;
        assertThat(commandToClient.getOpcode(), is(expectedOpcode));
    }

    @Test
    public void handleGetAuctionItemTest() throws IOException {

        //Given
        Long itemId = 2L;
        Command command = MockCommands.getMockGetAuctionItemCommand(itemId);

        // When
        serverReadChannel.handleRead(command);
        Command commandToClient = mockClientSocket.getOutPutStreamCommand();

        // Then
        int expectedOpcode = Opcodes.AUCTION_ITEM;
        assertThat(commandToClient.getOpcode(), is(expectedOpcode));
    }

    private void runTestReadLoginCommand(
            User user,
            HttpStatusCode httpStatusCode,
            Map expectation) throws IOException {

        //Given
        int sizeBefore = usersList.getUsersList().size();

        /* doesn't matter what the user is */
        MockAuthServer.isUserAuthExpectations(user, httpStatusCode);

        //When
        int status = serverReadChannel.handleRead((Command) expectation.get("command"));
        int sizeAfter = usersList.getUsersList().size();
        /* check what was sent to client through socket */
        Command command = mockClientSocket.getOutPutStreamCommand();

        //Then
        assertThat(status, is(expectation.get("status")));
        assertThat(sizeBefore, is(expectation.get("sizeBefore")));
        assertThat(sizeAfter, is(expectation.get("sizeAfter")));
        assertThat(command.getOpcode(), is(expectation.get("opcodeToClient")));

    }

    private void testReadRegisterCommand(
            Command command,
            HttpStatusCode httpStatusCode,
            Map expectation) throws IOException {

        //Given
        MockAuthServer.registerAuthExpectations(((RegisterUserMessage)command.getMessage()).getUser(), httpStatusCode);

        //When
        int status = serverReadChannel.handleRead(command);
        /* check what was sent to client through socket */
        Command commandToClient = mockClientSocket.getOutPutStreamCommand();

        //Then
        assertThat(status, is(expectation.get("status")));
        assertThat(commandToClient.getOpcode(), is(expectation.get("opcodeToClient")));
    }
}

