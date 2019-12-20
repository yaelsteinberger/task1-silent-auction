package client;

import entity.User;

public class UserDetails {
    private static User userDetails;
    private static Object lock = new Object();

    public static void setUserDetails(User user){
        synchronized (lock){
            if( userDetails != null){
                userDetails = user;
            }
        }
    }

    public static User getUserDetails() {
        return userDetails;
    }
}
