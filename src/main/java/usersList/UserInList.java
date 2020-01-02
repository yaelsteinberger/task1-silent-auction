package usersList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.User;

import java.net.Socket;

public class UserInList {
    private final User user;
    private final Socket socket;

    @JsonCreator
    public UserInList(@JsonProperty("user") User user,
                      @JsonProperty("socket") Socket socket) {

        this.user = user;
        this.socket = socket;
    }

    public User getUser() {
        return user;
    }

    public Socket getSocket() {
        return socket;
    }
}
