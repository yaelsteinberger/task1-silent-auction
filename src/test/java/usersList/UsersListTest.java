package usersList;

import MOCKs.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import entity.HttpResponse;
import entity.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import server.ServerProperties;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
//import static org.hamcrest //.samePropertyValuesAs;
//import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;

public class UsersListTest {
    static private List<User> users;
    private UsersList usersList;
    private static ServerSocket listener;
    private static Socket client;


    @BeforeClass
    static public void setup() throws InterruptedException, IOException {

        //Given
        MockAuthServer.startServer();
        users = Arrays.asList(MockUsers.getUsers());

        /* run listener */
//        listener = new ServerSocket(MockTestProperties.getServerPort());
    }

    @AfterClass
    static public void tearDown() throws JsonProcessingException {
        MockAuthServer.stopServer();
//        listener.close();
    }

    @Before
    public void cleanServerCache(){
        usersList = new UsersList();

        /* Before each test clear the cache from any previous responses and expectations */
        MockAuthServer.resetServer();

    }

    private void connectToClientSocket() throws IOException {
        /* run client */
        MockSocketTarget mockClient = new MockSocketTarget();
        mockClient.openSocketToSource();

        /* get client socket */
        client = listener.accept();
    }


    @Test
    public void loginUserTest() throws IOException {

        //Given
        User user = new User(
                "userName4",
                "FirstName4",
                "LastName4");

        Map<HttpStatusCode,Object> expectations = new HashMap<>(){{
            put(HttpStatusCode.OK_200,
                    new HashMap(){{
                        put("statusCode",StatusCode.SUCCESS);
                        put("user",user);
                    }});
            put(HttpStatusCode.FORBIDDEN_403,
                    new HashMap(){{
                        put("statusCode",StatusCode.ACCOUNT_IS_DISABLED);
                        put("user",null);
                    }});
            put(HttpStatusCode.NOT_FOUND_404,
                            new HashMap(){{
                        put("statusCode",StatusCode.NO_ACCOUNT_EXISTS);
                        put("user",null);
                    }});
        }};



        Set<HttpStatusCode> httpStatusCodes = expectations.keySet();

        //When
        for (HttpStatusCode httpStatusCode : httpStatusCodes) {
            usersList = new UsersList();

            Map expectation = (Map) expectations.get(httpStatusCode);
            runTestLoginUser(user,httpStatusCode, expectation);
        }
    }

    private void runTestLoginUser(User user,
                                   HttpStatusCode httpStatusCode,
                                   Map expectation) throws IOException {

//        connectToClientSocket();
        MockAuthServer.resetServer();

        //Given
        MockAuthServer.isUserAuthExpectations(user, httpStatusCode);

        //When
        int status = usersList.loginUser(user.getUserName());
        User userInList = usersList.findByUserName(user.getUserName());

        //Then
        assertThat(status, is(expectation.get("statusCode")));

        try{
            assertThat(userInList, samePropertyValuesAs(expectation.get("user")));
        }catch(NullPointerException e){
            /* if there is no "user" (null) need to use "is" in the assert */
            assertThat(userInList, is(expectation.get("user")));
        }

//        client.close();
    }

    @Test
    public void removeByUserNameTest() {

        //Given
        String userNameToRemove = "username2";

        //When
        users.forEach(user -> {
            try {
                MockAuthServer.isUserAuthExpectations(user,HttpStatusCode.OK_200);
                usersList.loginUser(user.getUserName());
                MockAuthServer.resetServer();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        usersList.removeByUserName(userNameToRemove);

        //Then
        User user = usersList.findByUserName(userNameToRemove);
        assertThat(user, is(nullValue()));
    }

    @Test
    public void getUsersListTest() {

        //Given
        int sizeBefore = usersList.getUsersList().size();

        //When
        users.forEach(user -> {
            try {
                MockAuthServer.isUserAuthExpectations(user,HttpStatusCode.OK_200);
                usersList.loginUser(user.getUserName());
                MockAuthServer.resetServer();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        int sizeAfter = usersList.getUsersList().size();

        //Then
        int expectedSizeBefore = 0;
        int expectedSizeAfter = 3;

        assertThat(sizeBefore, is(expectedSizeBefore));
        assertThat(sizeAfter, is(expectedSizeAfter));
    }
}
