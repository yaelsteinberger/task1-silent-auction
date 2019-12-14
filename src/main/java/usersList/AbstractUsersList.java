package usersList;

import entity.User;

public abstract class AbstractUsersList {

    protected abstract int authenticate(User user);

    public abstract int loginUser(User user);

    public abstract User findByUserName(String userName);

    public abstract void removeByUserName(String userName);
}
