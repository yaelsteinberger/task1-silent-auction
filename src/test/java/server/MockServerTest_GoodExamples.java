package server;

import MOCK.MockAuthResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.httpResponse.AbstractResponse;

import entity.httpResponse.ResponseSuccess;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.*;
import org.mockserver.verify.VerificationTimes;
import services.authenticate.HttpAuthApi;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.StringBody.exact;

public class MockServerTest {
//    String baseUrl = "http://localhost:666/";
    String propFilePath = "src\\test\\resources\\mockConfig.properties";
    //    HttpAuthServicesApiMockCall httpApi;
    ObjectMapper mapper = new ObjectMapper();


    @Before
    public void setup(){
        //Given
        ServerProperties.readConfigPropertiesFile(propFilePath);
//        httpApi = new HttpAuthServicesApiMockCall();

    }


    @Test
    public void testWithResponseBuilder() throws IOException {
        MockServerClient mockServer = startClientAndServer(8080);


        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/user/yael/isactive/")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("{ 'isActive': 'true' }")
                                .withDelay(new Delay(SECONDS, 1))
                );

        HttpAuthApi authApi = new HttpAuthApi();
        AbstractResponse response = authApi.isUserAuth("yael");

        /* verify the request is according the expectations defined above */
        new MockServerClient("localhost", 8080).verify(
                request()
                        .withMethod("GET")
                        .withPath("/user/yael/isactive/")
                        .withBody(exact("{ 'isActive': 'true' }")),
                VerificationTimes.exactly(1)
        );

    }



    @Test
    public void testWithCallBack() throws IOException {
        /* call back instead of the response builder */
        MockServerClient mockServer = startClientAndServer(8080);

        mockServer
                .when(request()
                    .withMethod("GET")
                    .withPath("/user/yael/isactive/")
                )
                .respond(
                        httpRequest -> {
                            if (httpRequest.getMethod().getValue().equals("GET")) {
                                System.err.println("IM HERE TOO!");
                                return response()
                                        .withBody("helloCoy");
                            } else {
                                return notFoundResponse();
                            }
                        });
        HttpAuthApi authApi = new HttpAuthApi();

        authApi.isUserAuth("yael");


        new MockServerClient("localhost", 8080).
                verify(
                request()
                        .withMethod("GET")
                        .withPath("/user/yael/isactive/")
                        .withBody(exact("helloCoy")),
                VerificationTimes.exactly(1)
        );
    }



    @Test
    public void testWithReadingTheReceivedResponse() throws IOException {
        MockServerClient mockServer = startClientAndServer(8080);

        String timeStamp = new Date().toString();
        String path = "user/yael/isactive";
        Map data = new HashMap<String,Boolean>() {{put("active",true);}};
        HttpStatusCode status = HttpStatusCode.OK_200;

        MockAuthResponseBody responseBody = new MockAuthResponseBody<Map>(
                status,
                path,
                null,
                data
        );

//        String responseBody = "{\"timestamp\":\"Dont Care\",\"status\":200,\"path\":\"user/yaelsteinberger/isactive\",\"data\":{\"active\":true}}";


//        ResponseSuccess responseBody = new ResponseSuccess(timeStamp,status,path,data);
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/user/yaelsteinberger/isactive/")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(responseBody.writeToJsonString())
                                .withDelay(new Delay(SECONDS, 1))
                );

        HttpAuthApi authApi = new HttpAuthApi();
        AbstractResponse response = authApi.isUserAuth("yaelsteinberger");

        System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));

//        new MockServerClient("localhost", 8080)
//                .verify(
//                    request()
//                        .withMethod("GET")
//                        .withPath("/user/yaelsteinberger/isactive/"),
////                        .withBody(exact("{ 'isActive': 'true' }")),
//                VerificationTimes.exactly(1)
//        );

    }
}

