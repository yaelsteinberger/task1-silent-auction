package usersList;

import entity.User;
import entity.httpResponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import authenticate.HttpAuthApi;
import authenticate.HttpStatusCode;
import authenticate.InvalidUserNames;

import java.util.Map;
import java.util.TreeMap;

public class UsersList extends AbstractUsersList {
    private final static Logger logger = LoggerFactory.getLogger(UsersList.class);
    private Map<String, User> usersList;

    public UsersList() {
        usersList = new TreeMap<>();
    }

    @Override
    protected int authenticate(User user) {
        int statusCode = StatusCode.SUCCESS;

        HttpAuthApi httpServices = new HttpAuthApi();
        HttpResponse responseObject = (HttpResponse) httpServices.isUserAuth(user.getUserName());

        if(responseObject.isError()){
            HttpResponse errorMsg = (HttpResponse) responseObject;
            logger.error(errorMsg.getMessage().replace("User", "User " + user.getFirstName() + " " + user.getLastName()));

            switch (errorMsg.getStatusCode()){
                case HttpStatusCode.FORBIDDEN: {
                    statusCode = StatusCode.ACCOUNT_IS_DISABLED;
                    break;
                }
                case HttpStatusCode.NOT_FOUND:{
                    statusCode = StatusCode.NO_ACCOUNT_EXISTS;
                    break;
                }
            }
        }

        return statusCode;
    }

    @Override
    public int loginUser(User user) {
        int statusCode = StatusCode.INVALID_USERNAME;

        User authUser = new User(
              user.getUserName(),
              user.getLastName(),
              user.getLastName()
        );

        boolean isValidUserName = InvalidUserNames.isUserNameValid(user.getUserName());

        if (isValidUserName) {
            statusCode = authenticate(authUser);

            if(statusCode == StatusCode.SUCCESS){
                usersList.put(user.getUserName(), user);
            }

        }

        return statusCode;
    }

    @Override
    public User findByUserName(String userName) {
        return usersList.get(userName);
    }

    @Override
    public void removeByUserName(String userName) {
        usersList.remove(userName);
    }

}
