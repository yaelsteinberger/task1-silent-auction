package authenticate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.httpResponse.AbstractResponse;
import entity.httpResponse.HttpResponse;
import entity.httpResponse.ResponseError;
import entity.httpResponse.ResponseSuccess;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerProperties;

import java.io.IOException;
import java.util.Objects;

public class HttpAuthApi {
    private final Logger logger = LoggerFactory.getLogger(HttpAuthApi.class);

    public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");
    private final String authBaseUrl;
    private final OkHttpClient httpClient;


    public HttpAuthApi() {
        this.authBaseUrl = (String) ServerProperties.getProperties().get("authServer.url");
        this.httpClient = new OkHttpClient();
    }


    protected AbstractResponse makeCall(Request request){

        HttpResponse responseObj = null;

        try(Response response = httpClient.newCall(request).execute()) {

            Class responseClass = response.isSuccessful() ?
                    ResponseSuccess.class :
                    ResponseError.class;

            ObjectMapper mapper = new ObjectMapper();
            String responseBody = Objects.requireNonNull(response.body()).string();
            responseObj = (HttpResponse) mapper.readValue(responseBody, HttpResponse.class);

        } catch (IOException e){
            logger.error(e.getMessage());
        }

        return responseObj;

    }

    public AbstractResponse isUserAuth(String userName){

        String url = authBaseUrl + PathNames.IS_USER_AUTH.replace("{userName}",userName);

        System.err.println(url);
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
}
