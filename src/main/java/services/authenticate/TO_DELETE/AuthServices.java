package services.authenticate.TO_DELETE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.response.AbstractResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.authenticate.PathNames;

public abstract class AuthServices {
    private final Logger logger = LoggerFactory.getLogger(AuthServices.class);

    public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");
    private final String authBaseUrl;

    public AuthServices(String authBaseUrl) {
        this.authBaseUrl = authBaseUrl;
    }

    protected abstract <T> AbstractResponse makeCall(T request);



    public AbstractResponse isUserAuth(String userName){

        String url = authBaseUrl + PathNames.IS_USER_AUTH.replace("{userName}",userName);

        Request request = new Request.Builder()
                .url(url)
                .build();

        return makeCall(request);
    }


    public AbstractResponse registerUser(User user) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);
        RequestBody body = RequestBody.create(json,JSON);
        String url = authBaseUrl + PathNames.REGISTER_USER;

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return makeCall(request);
    }


    public AbstractResponse test(User user) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);
        RequestBody body = RequestBody.create(json,JSON);
        String url = authBaseUrl + PathNames.REGISTER_USER;

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return makeCall(request);
    }
}
