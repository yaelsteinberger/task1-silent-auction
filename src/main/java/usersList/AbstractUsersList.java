package usersList;

import entity.User;

import java.net.Socket;

public abstract class AbstractUsersList   {

    protected abstract Object authenticate(String userName);

    public abstract int loginUser(String userName, Socket socket);

    public abstract UserInList findByUserName(String userName);

    public abstract void removeByUserName(String userName);
}
