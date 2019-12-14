package usersList;

import MOCKs.MockAuthServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import entity.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import server.ServerProperties;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;

public class UsersListTest {
    static private MockAuthServer mockAuthServer;
    static private ArrayList users;
    private UsersList usersList;


    @BeforeClass
    static public void setup() throws JsonProcessingException {

        //Given
        String propFilePath = "src\\test\\resources\\mockConfig.properties";
        ServerProperties.readConfigPropertiesFile(propFilePath);
        String mockHost = (String) ServerProperties.getProperties().get("authServer.host");
        String mockPort = (String) ServerProperties.getProperties().get("authServer.port");
        mockAuthServer = new MockAuthServer(mockHost,Integer.parseInt(mockPort));
        mockAuthServer.startServer();

        users = new ArrayList<User>(){{
            add(new User("username1","FirstName1","LastName1"));
            add(new User("username2","FirstName2","LastName2"));
            add(new User("username3","FirstName3","LastName3"));
        }};
    }

    @AfterClass
    static public void tearDown() throws JsonProcessingException {
        mockAuthServer.stopServer();
    }

    @Before
    public void cleanServerCache(){
        usersList = new UsersList();

        /* Before each test clear the cache from any previous responses and expectations */
        mockAuthServer.resetServer();
    }

    @Test
    public void loginUserSuccessTest() throws IOException {

        //Given
        User user = new User(
                "username4",
                "FirstName4",
                "LastName4");

        //When
        mockAuthServer.isUserAuthExpectations(user.getUserName(),HttpStatusCode.OK_200);
        int status = usersList.loginUser(user);
        User userInList = usersList.findByUserName(user.getUserName());

        //Then
        int expectedStatusCode = StatusCode.SUCCESS;
        assertThat(status, is(expectedStatusCode));
        assertThat(userInList, is(user));
    }

    @Test
    public void loginUserFailAccountDisabledTest() throws IOException {

        //Given
        User user = new User(
                "username4",
                "FirstName4",
                "LastName4");

        //When
        mockAuthServer.isUserAuthExpectations(user.getUserName(),HttpStatusCode.FORBIDDEN_403);
        int status = usersList.loginUser(user);
        User userInList = usersList.findByUserName(user.getUserName());

        //Then
        int expectedStatusCode = StatusCode.ACCOUNT_IS_DISABLED;
        assertThat(status, is(expectedStatusCode));
        assertThat(userInList, nullValue());
    }

    @Test
    public void loginUserFailAccountNotExistTest() throws IOException {

        //Given
        User user = new User(
                "username4",
                "FirstName4",
                "LastName4");

        //When
        mockAuthServer.isUserAuthExpectations(user.getUserName(),HttpStatusCode.NOT_FOUND_404);
        int status = usersList.loginUser(user);
        User userInList = usersList.findByUserName(user.getUserName());

        //Then
        int expectedStatusCode = StatusCode.NO_ACCOUNT_EXISTS;
        assertThat(status, is(expectedStatusCode));
        assertThat(userInList, nullValue());
    }

    @Test
    public void removeByUserNameTest() throws IOException {

        //Given
        String userNameToRemove = "username2";

        //When
        users.forEach(user -> {
            try {
                mockAuthServer.isUserAuthExpectations(((User)user).getUserName(),HttpStatusCode.OK_200);
                usersList.loginUser((User) user);
                mockAuthServer.resetServer();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        usersList.removeByUserName(userNameToRemove);

        //Then
        entity.User user = usersList.findByUserName(userNameToRemove);
        assertThat(user, is(nullValue()));
    }
}
