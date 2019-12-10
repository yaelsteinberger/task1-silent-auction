package usersList.mock;

import entity.User;
import usersList.UsersList;

public class UsersListMockCalls extends UsersList {
    int isAuth = -1;

    @Override
    protected int authenticate(User user){
        return this.isAuth;
    }

    public void setAuth(int auth) {
        this.isAuth = auth;
    }
}
