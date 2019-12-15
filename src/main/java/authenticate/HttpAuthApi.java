package authenticate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.HttpResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerProperties;

import java.io.IOException;
import java.util.Objects;

public class HttpAuthApi {
    private final Logger logger = LoggerFactory.getLogger(HttpAuthApi.class);
    private ObjectMapper mapper = new ObjectMapper();

    private static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");
    private final String authBaseUrl;
    private final OkHttpClient httpClient;


    public HttpAuthApi() {
        this.authBaseUrl = (String) ServerProperties.getProperties().get("authServer.url");
        this.httpClient = new OkHttpClient();
    }


    private HttpResponse makeCall(Request request){

        HttpResponse responseObj = null;

        try(Response response = httpClient.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            responseObj = mapper.readValue(responseBody, HttpResponse.class);

        } catch (IOException e){
            logger.error(e.getMessage());
        }

        return responseObj;
    }

    public HttpResponse isUserAuth(String userName){

        String url = authBaseUrl + PathNames.IS_USER_AUTH.replace("{userName}",userName);

//        System.err.println(url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        return makeCall(request);
    }


    public HttpResponse registerUser(User user) throws JsonProcessingException {

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
