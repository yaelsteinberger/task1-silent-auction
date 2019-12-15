package usersList;

import MOCKs.MockAuthServer;
import MOCKs.MockUsers;
import com.fasterxml.jackson.core.JsonProcessingException;
import entity.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import server.ServerProperties;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UsersListTest {
    static private List<User> users;
    private UsersList usersList;


    @BeforeClass
    static public void setup() throws InterruptedException {


        //Given
        String propFilePath = "src\\test\\resources\\mockConfig.properties";
        ServerProperties.readConfigPropertiesFile(propFilePath);
        String mockHost = (String) ServerProperties.getProperties().get("authServer.host");
        String mockPort = (String) ServerProperties.getProperties().get("authServer.port");
        MockAuthServer.setAttributes(mockHost,Integer.parseInt(mockPort));
        MockAuthServer.startServer();
        users = Arrays.asList(MockUsers.getUsers());
    }

    @AfterClass
    static public void tearDown() throws JsonProcessingException {
        MockAuthServer.stopServer();
    }

    @Before
    public void cleanServerCache(){
        usersList = new UsersList();

        /* Before each test clear the cache from any previous responses and expectations */
        MockAuthServer.resetServer();

    }

    @Test
    public void loginUserSuccessTest() throws IOException {

        //Given
        User user = new User(
                "username4",
                "FirstName4",
                "LastName4");

        //When
        MockAuthServer.isUserAuthExpectations(user.getUserName(),HttpStatusCode.OK_200);
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
        MockAuthServer.isUserAuthExpectations(user.getUserName(),HttpStatusCode.FORBIDDEN_403);
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
        MockAuthServer.isUserAuthExpectations(user.getUserName(),HttpStatusCode.NOT_FOUND_404);
        int status = usersList.loginUser(user);
        User userInList = usersList.findByUserName(user.getUserName());

        //Then
        int expectedStatusCode = StatusCode.NO_ACCOUNT_EXISTS;
        assertThat(status, is(expectedStatusCode));
        assertThat(userInList, nullValue());
    }

    @Test
    public void removeByUserNameTest() {

        //Given
        String userNameToRemove = "username2";

        //When
        users.forEach(user -> {
            try {
                MockAuthServer.isUserAuthExpectations(user.getUserName(),HttpStatusCode.OK_200);//                mockAuthServer.isUserAuthExpectations(((User)user).getUserName(),HttpStatusCode.OK_200);
                usersList.loginUser(user);
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
                MockAuthServer.isUserAuthExpectations(((User)user).getUserName(),HttpStatusCode.OK_200);
                usersList.loginUser((User) user);
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
