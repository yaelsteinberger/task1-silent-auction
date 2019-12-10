package usersList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import server.ServerProperties;
import usersList.mock.UsersListMockCalls;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;

public class UsersListTest {
    ObjectMapper mapper = new ObjectMapper();
    UsersListMockCalls usersList;
    ArrayList users;

    @Before
    public void setup(){
        //Given
        usersList = new UsersListMockCalls();
        users = new ArrayList<UserListItem>(){{
            add(new UserListItem("username1","FirstName1","LastName1", null));
            add(new UserListItem("username2","FirstName2","LastName2", null));
            add(new UserListItem("username3","FirstName3","LastName3", null));
        }};
    }

    @Test
    public void loginUserSuccessTest() throws JsonProcessingException {

        //Given
        usersList.setAuth(StatusCode.SUCCESS);
        UserListItem user = new UserListItem(
                "username4",
                "FirstName4",
                "LastName4",
                null);

        //When
        usersList.loginUser(user);
        User userInList = usersList.findByUserName(user.getUserName());

        //Then
        assertThat(userInList, is(user));
    }

    @Test
    public void loginUserFailTest() throws JsonProcessingException {

        //Given
        UserListItem user = new UserListItem(
                "username5",
                "FirstName5",
                "LastName5",
                null);
        usersList.setAuth(StatusCode.FATAL_ERROR);

        //When
        usersList.loginUser(user);
        User userInList = usersList.findByUserName(user.getUserName());

        //Then
        assertThat(userInList, is(nullValue()));
    }

    @Test
    public void removeByUserNameTest() throws JsonProcessingException {

        //Given
        String userNameToRemove = "username2";
        usersList.setAuth(StatusCode.SUCCESS);

        //When
        users.forEach(user -> {
            usersList.loginUser((UserListItem) user);
        });
        usersList.removeByUserName(userNameToRemove);

        //Then
        User user = usersList.findByUserName(userNameToRemove);
        assertThat(user, is(nullValue()));
    }
}
