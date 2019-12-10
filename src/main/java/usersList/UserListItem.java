package usersList;

import entity.User;

import java.net.Socket;

public class UserListItem extends User {
    private final Socket socket;

    public UserListItem(
            String userName,
            String firstName,
            String lastName,
            Socket socket
    ) {
        super(userName, firstName, lastName);
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

}
