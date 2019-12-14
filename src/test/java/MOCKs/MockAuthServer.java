package MOCKs;

import entity.User;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Delay;
import org.mockserver.model.HttpStatusCode;
import authenticate.PathNames;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


public class MockAuthServer {

    private final String host;
    private final Integer port;
    private ClientAndServer mockServer;

    public MockAuthServer(String host,Integer port) {
        this.host = host;
        this.port = port;
    }

    public void startServer() {
        this.mockServer = startClientAndServer(port);
    }

    public void stopServer() {
        mockServer.stop();
    }

    public void resetServer(){
        new MockServerClient(host, port).reset();
    }


    public void isUserAuthExpectations(String userName, HttpStatusCode status) throws IOException {

        String path = "/" + PathNames.IS_USER_AUTH.replace("{userName}",userName);
        boolean isExpectActive = (status.code() == HttpStatusCode.OK_200.code());
        String message = isExpectActive ? null : "Don't Care";
        Map data = getDataByStatus(status);

        MockAuthResponseBody<Map> responseBody = new MockAuthResponseBody<Map>(status, path, message, data);
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(path)
                )
                .respond(
                        response()
                                .withStatusCode(status.code())
                                .withBody(responseBody.writeToJsonString())
                                .withDelay(new Delay(SECONDS, 1))
                );
    }

    public void registerAuthExpectations(User user, HttpStatusCode status) throws IOException {

        String path = "/" + PathNames.REGISTER_USER;
        boolean isExpectActive = (status.code() == HttpStatusCode.OK_200.code());
        String message = isExpectActive ? null : "Don't Care";
        HashMap data = !isExpectActive ? null :
                new HashMap<String,Object>(){{
            put("id",1);
            put("firstName",user.getFirstName());
            put("lastName",user.getLastName());
            put("userName",user.getUserName());
            put("active",true);
        }};

        MockAuthResponseBody<Map> responseBody = new MockAuthResponseBody<Map>(status, path, message, data);
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath(path)
                )
                .respond(
                        response()
                                .withStatusCode(status.code())
                                .withBody(responseBody.writeToJsonString())
                                .withDelay(new Delay(SECONDS, 1))
                );
    }


    private Map getDataByStatus(HttpStatusCode status){
        boolean isActive = (status.code() == HttpStatusCode.OK_200.code());
        boolean isDoesntExists = (status.code() == HttpStatusCode.NOT_FOUND_404.code());
        return isDoesntExists ? null : new HashMap<String,Boolean>() {{put("active",isActive);}};
    }
}

