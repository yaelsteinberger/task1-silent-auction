package usersList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import org.junit.Before;
import org.junit.Test;
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
        users = new ArrayList<User>(){{
            add(new User("username1","FirstName1","LastName1"));
            add(new User("username2","FirstName2","LastName2"));
            add(new User("username3","FirstName3","LastName3"));
        }};
    }

    @Test
    public void loginUserSuccessTest() throws JsonProcessingException {

        //Given
        usersList.setAuth(StatusCode.SUCCESS);
        User user = new User(
                "username4",
                "FirstName4",
                "LastName4");

        //When
        usersList.loginUser(user);
        entity.User userInList = usersList.findByUserName(user.getUserName());

        //Then
        assertThat(userInList, is(user));
    }

    @Test
    public void loginUserFailTest() throws JsonProcessingException {

        //Given
        User user = new User(
                "username5",
                "FirstName5",
                "LastName5");
        usersList.setAuth(StatusCode.FATAL_ERROR);

        //When
        usersList.loginUser(user);
        entity.User userInList = usersList.findByUserName(user.getUserName());

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
            usersList.loginUser((User) user);
        });
        usersList.removeByUserName(userNameToRemove);

        //Then
        entity.User user = usersList.findByUserName(userNameToRemove);
        assertThat(user, is(nullValue()));
    }
}
