package MOCKs;

import authenticate.PathNames;
import entity.User;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Delay;
import org.mockserver.model.HttpStatusCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


public class MockAuthServer {
    private static String host;
    private static Integer port;
    private static ClientAndServer mockServer;

    public static void startServer() throws IOException {
        MockTestProperties.reset();
        host = MockTestProperties.getAuthServerHost();
        port = MockTestProperties.getAuthServerPort();

        mockServer = startClientAndServer(port);

    }

    public static void stopServer()  {
        mockServer.stop();
    }

    public static void resetServer(){
       new MockServerClient(host, port).reset();
    }


    public static void isUserAuthExpectations(User user, HttpStatusCode status) throws IOException {
        String path = "/" + PathNames.IS_USER_AUTH.replace("{userName}",user.getUserName());
        boolean isExpectActive = (status.code() == HttpStatusCode.OK_200.code());
        String message = isExpectActive ? null : MockGenericValues.DONT_CARE;
        Map data = getIsUserAuthDataByStatus(status,user);
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

    public static void registerAuthExpectations(User user, HttpStatusCode status) throws IOException {

        String path = "/" + PathNames.REGISTER_USER;
        boolean isExpectActive = (status.code() == HttpStatusCode.OK_200.code());
        String message = isExpectActive ? null : MockGenericValues.DONT_CARE;
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


    private static Map getIsUserAuthDataByStatus(HttpStatusCode status, User user){
        boolean isActive = (status.code() == HttpStatusCode.OK_200.code());
        return !isActive ? null :
                new HashMap<String,Object>(){{
                    put("id",1);
                    put("firstName",user.getFirstName());
                    put("lastName",user.getLastName());
                    put("userName",user.getUserName());
                    put("active",true);
                }};
    }
}

