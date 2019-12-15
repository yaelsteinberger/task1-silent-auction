package server;


import MOCKs.*;
import auctionList.AuctionItemsList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.command.Command;
import entity.command.schemas.LoginUserMessage;
import org.junit.AfterClass;
import org.junit.Before;

import org.junit.BeforeClass;
import org.junit.Test;

import org.mockserver.mockserver.MockServer;
import authenticate.HttpAuthApi;
import org.mockserver.model.HttpStatusCode;
import usersList.AbstractUsersList;
import usersList.StatusCode;
import usersList.UsersList;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


public class ServerReadChannelTest {
    private static MockAuthServer mockAuthServer;
    private static UsersList usersList;
    private static User[] users;
    private static AuctionItemsList auctionItemsList;
    private static ServerReadChannel serverReadChannel;


    @BeforeClass
    public static void setup() throws JsonProcessingException {

        //Given
        String propFilePath = "src\\test\\resources\\mockConfig.properties";
        ServerProperties.readConfigPropertiesFile(propFilePath);
        String mockHost = (String) ServerProperties.getProperties().get("authServer.host");
        String mockPort = (String) ServerProperties.getProperties().get("authServer.port");
        mockAuthServer = new MockAuthServer(mockHost,Integer.parseInt(mockPort));
        mockAuthServer.startServer();

        auctionItemsList = MockAuctionItems.generateAuctionItemsList();
        usersList = new UsersList();
        users = MockUsers.getUsers();

        Socket clientSocket = new Socket();
        serverReadChannel = new ServerReadChannel(clientSocket,usersList,auctionItemsList);

    }

    @AfterClass
    public static void tearDown() {
        mockAuthServer.stopServer();
    }

    @Before
    public void cleanServerCache(){
        /* Before each test clear the cache from any previous responses and expectations */
        mockAuthServer.resetServer();
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

        for (HttpStatusCode httpStatusCode : httpStatusCodes) {
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

        for (HttpStatusCode httpStatusCode : httpStatusCodes) {
            Integer expectedStatus = expectations.get(httpStatusCode);

            testReadRegisterCommand(command,httpStatusCode,expectedStatus);
        }
    }

    private void testReadLoginCommand(
            Command command,
            HttpStatusCode httpStatusCode,
            Map expectation) throws IOException {

        try{
            //Given
            int sizeBefore = usersList.getUsersList().size();
            mockAuthServer.isUserAuthExpectations(
                    ((LoginUserMessage)command.getMessage()).getUser().getUserName(), httpStatusCode);

            //When
            int status = serverReadChannel.handleRead(command);
            int sizeAfter = usersList.getUsersList().size();

            //Then
            assertThat(status, is(expectation.get("status")));
            assertThat(sizeBefore, is(expectation.get("sizeBefore")));
            assertThat(sizeAfter, is(expectation.get("sizeAfter")));

            mockAuthServer.resetServer();

        }catch(SocketException e){
            //Ignore since didn't testing the socket to client connection
        }

    }


    private void testReadRegisterCommand(
            Command command,
            HttpStatusCode httpStatusCode,
            Integer expectedStatus) throws IOException {

        try{
            //Given
            mockAuthServer.registerAuthExpectations(
                    ((LoginUserMessage)command.getMessage()).getUser(), httpStatusCode);

            //When
            int status = serverReadChannel.handleRead(command);

            //Then
            assertThat(status, is(expectedStatus));

            mockAuthServer.resetServer();

        }catch(SocketException e){
            //Ignore since didn't testing the socket to client connection
        }

    }
}

