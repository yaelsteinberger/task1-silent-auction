package MOCKs;

import entity.User;
import java.util.Arrays;
import java.util.stream.Stream;

public class MockUsers {
    static private User[]  users = new User[]{
        new User("username1","FirstName1","LastName1"),// 0
        new User("username2","FirstName2","LastName2"),// 1
        new User("username3","FirstName3","LastName3")// 2
    };

    static public Stream<User> streamUsers(){
        return Arrays.stream(users);
    }

    public static User[] getUsers() {
        return users;
    }
}
