package usersList;

import entity.User;
import entity.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import authenticate.HttpAuthApi;
import authenticate.HttpStatusCode;
import authenticate.InvalidUserNames;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UsersList extends AbstractUsersList {
    private final static Logger logger = LoggerFactory.getLogger(UsersList.class);
    private Map<String, User> usersList;

    public UsersList() {
        usersList = new TreeMap<>();
    }

    @Override
    protected Map authenticate(String userName) {
        Map returnObj = new HashMap();
        int statusCode = StatusCode.SUCCESS;

        HttpAuthApi httpAuthApi = new HttpAuthApi();
        HttpResponse responseObject = httpAuthApi.isUserAuth(userName);

        if(responseObject.isError()){
            HttpResponse errorMsg =  responseObject;
            logger.error(errorMsg.getMessage().replace("User", "User " + userName));

            switch (errorMsg.getStatus()){
                case HttpStatusCode.FORBIDDEN: {
                    statusCode = StatusCode.ACCOUNT_IS_DISABLED;
                    break;
                }
                case HttpStatusCode.NOT_FOUND:{
                    statusCode = StatusCode.NO_ACCOUNT_EXISTS;
                    break;
                }
            }
        }else{
            Map userData = (Map) responseObject.getData().get("user");
            User user = new User(
                    (String)userData.get("userName"),
                    (String)userData.get("firstName"),
                    (String)userData.get("lastName")
            );
            returnObj.put("user",user);
        }

        returnObj.put("statusCode",statusCode);
        return returnObj;
    }

    @Override
    public int loginUser(String userName) {
        int statusCode = StatusCode.INVALID_USERNAME;

        boolean isValidUserName = InvalidUserNames.isUserNameValid(userName);

        if (isValidUserName) {
            Map returnObj = authenticate(userName);
            statusCode = (int) returnObj.get("statusCode");

            if(statusCode == StatusCode.SUCCESS){

                usersList.put(userName, (User)returnObj.get("user"));
                logger.debug("Added user {} to list", userName);
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

    public Map getUsersList(){
        return usersList;
    }

}
