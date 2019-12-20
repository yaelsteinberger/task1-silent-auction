package client;

import entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class GenerateUser {

    private static AtomicInteger userNameIndex = new AtomicInteger(0);

    private static List<Map> usersPool;
    private interface Headers {
        int FIRST_NAME = 0;
        int LAST_NAME = 1;
        int USER_NAME = 2;
        int IS_REGISTERED = 3;
    }

    public static User generateName(boolean isRegistered){

        if(usersPool == null){generateUsersPool();}

        Object[] users = usersPool.stream()
                .filter(item -> ((boolean)item.get("isRegistered") && isRegistered) || (!(boolean)item.get("isRegistered") && !isRegistered))
                .map(item -> item.get("user"))
                .toArray();

        return (User)users[0];
    }

    private static void generateUsersPool(){
        usersPool = new ArrayList();
        Object[][] usersList = getUsers();

        for (Object[] item : usersList) {
            User user = new User(
                    (String)item[Headers.USER_NAME],
                    (String)item[Headers.FIRST_NAME],
                    (String)item[Headers.LAST_NAME]
            );

            Map userObject = new HashMap(){{
                put("isRegistered",(boolean)item[Headers.IS_REGISTERED]);
                put("user",user);
            }};

            usersPool.add(userObject);
        }
    }

    private static Object[][] getUsers(){
        Object[][] retVal = {
                {"Guy", "Peleg", "guypeleg",true}, // already registered
                {"Nati", "Nahum", "natinahum",false} // not registered
//                {"Yael", "Steinberger", "yaelsteinberger",true}, // already registered

        };

        return retVal;
    }
}
