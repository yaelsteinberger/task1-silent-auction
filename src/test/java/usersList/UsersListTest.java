package usersList;

import MOCKs.MockAuthServer;
import MOCKs.MockUsers;
import com.fasterxml.jackson.core.JsonProcessingException;
import entity.StatusCode;
import entity.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

public class UsersListTest {
    static private User[] users;
    private UsersList usersList;


    @BeforeClass
    static public void setup() throws InterruptedException {

        //Given
        MockAuthServer.startServer();
        users = MockUsers.getUsers();
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
    public void loginUserTest() throws IOException {

        //Given
        User user = new User(
                "userName4",
                "FirstName4",
                "LastName4");

        Map<HttpStatusCode,Object> expectations = new HashMap<>(){{
            put(HttpStatusCode.OK_200,
                    new HashMap(){{
                        put("statusCode", StatusCode.SUCCESS);
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
    }

    @Test
    public void findByUserNameTest() {

        //Given
        String userName = users[1].getUserName();

        //When
        for (User user : users) {
            try {
                MockAuthServer.isUserAuthExpectations(user,HttpStatusCode.OK_200);
                usersList.loginUser(user.getUserName());
                MockAuthServer.resetServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        User user = usersList.findByUserName(userName);

        //Then
        User expectedUser = users[1];

        assertThat(user, samePropertyValuesAs(expectedUser));
    }

    @Test
    public void removeByUserNameTest() {

        //Given
        String userNameToRemove = users[1].getUserName();

        //When
        for (User user : users) {
            try {
                MockAuthServer.isUserAuthExpectations(user,HttpStatusCode.OK_200);
                usersList.loginUser(user.getUserName());
                MockAuthServer.resetServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

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
        for (User user : users) {
            try {
                MockAuthServer.isUserAuthExpectations(user,HttpStatusCode.OK_200);
                usersList.loginUser(user.getUserName());
                MockAuthServer.resetServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        int sizeAfter = usersList.getUsersList().size();

        //Then
        int expectedSizeBefore = 0;
        int expectedSizeAfter = 3;

        assertThat(sizeBefore, is(expectedSizeBefore));
        assertThat(sizeAfter, is(expectedSizeAfter));
    }
}
