package usersList;

import entity.User;

public abstract class AbstractUsersList   {

    protected abstract Object authenticate(String userName);

    public abstract int loginUser(String userName);

    public abstract User findByUserName(String userName);

    public abstract void removeByUserName(String userName);
}
