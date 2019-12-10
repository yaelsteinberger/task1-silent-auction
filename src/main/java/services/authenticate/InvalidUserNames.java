package services.authenticate;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.util.Arrays;

public class InvalidUserNames {
    private final static String[] INVALID_USER_NAMES = {
            "admin",
            "administrator"
    };

    public static boolean isUserNameValid(String userName){
        Object[] names = Arrays.asList(InvalidUserNames.INVALID_USER_NAMES)
                .stream()
                .filter(invalidName -> userName.toLowerCase().contains(invalidName))
                .toArray();

        return (names.length == 0);
    }
}
