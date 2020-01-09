package authentucate;

import MOCKs.MockAuthServer;
import MOCKs.MockGenericValues;
import MOCKs.MockSocketTarget;
import MOCKs.MockTestProperties;
import authenticate.HttpAuthApi;
import authenticate.PathNames;
import entity.HttpResponse;
import entity.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;


public class HttpServicesTest {
    private static HttpAuthApi httpAuthApi;
    private static User user;

    @BeforeClass
    static public void setup() throws IOException {

        //Given
        MockAuthServer.startServer();

        httpAuthApi = new HttpAuthApi();
        user = new User(
                "userName",
                "FirstName",
                "LastName");
    }

    @AfterClass
    public static void tearDown() throws IOException {
        MockAuthServer.stopServer();
    }

    @Before
    public void cleanServerCache() {
        /* Before each test clear the cache from any previous responses and expectations */
        MockAuthServer.resetServer();
    }

    @Test
    public void isAuthUserTest() throws IOException {

        //Given
        String expectedPath = "/" + PathNames.IS_USER_AUTH.replace("{userName}",user.getUserName());
        Map<HttpStatusCode,Object> expectations = new HashMap<>(){{
            put(HttpStatusCode.OK_200,
                new HttpResponse(
                        MockGenericValues.DONT_CARE,
                        HttpStatusCode.OK_200.code(),
                        expectedPath,
                        new HashMap<String,Object>(){{
                                put("id", 1);
                                put("firstName", user.getFirstName());
                                put("lastName", user.getLastName());
                                put("userName", user.getUserName());
                                put("active", true);
                            }},
                    null,
                    null
                ));
                put(HttpStatusCode.NOT_FOUND_404,
                    new HttpResponse(
                            MockGenericValues.DONT_CARE,
                            HttpStatusCode.NOT_FOUND_404.code(),
                            expectedPath,
                            null,
                            HttpStatusCode.NOT_FOUND_404.name(),
                            MockGenericValues.DONT_CARE
                    ));
                put(HttpStatusCode.FORBIDDEN_403,
                        new HttpResponse(
                                MockGenericValues.DONT_CARE,
                                HttpStatusCode.FORBIDDEN_403.code(),
                                expectedPath,
                                null,
                                HttpStatusCode.FORBIDDEN_403.name(),
                                MockGenericValues.DONT_CARE
                        ));
        }};

        Set<HttpStatusCode> httpStatusCodes = expectations.keySet();

        //When
        for (HttpStatusCode httpStatusCode : httpStatusCodes) {
            HttpResponse expectation = (HttpResponse) expectations.get(httpStatusCode);
            runTestIsAuthUser(user,httpStatusCode,expectation);
        }

    }

    @Test
    public void registerUserTest() throws IOException {

        // Given
        String expectedPath = "/" + PathNames.REGISTER_USER;
        Map<HttpStatusCode,Object> expectations = new HashMap<>(){{
            put(HttpStatusCode.OK_200,
                    new HttpResponse(
                            MockGenericValues.DONT_CARE,
                            HttpStatusCode.OK_200.code(),
                            expectedPath,
                            new HashMap<String,Object>(){{
                                put("id", 1);
                                put("firstName", user.getFirstName());
                                put("lastName", user.getLastName());
                                put("userName", user.getUserName());
                                put("active", true);
                            }},
                            null,
                            null
                    ));
            put(HttpStatusCode.FORBIDDEN_403,
                    new HttpResponse(
                            MockGenericValues.DONT_CARE,
                            HttpStatusCode.FORBIDDEN_403.code(),
                            expectedPath,
                            null,
                            HttpStatusCode.FORBIDDEN_403.name(),
                            MockGenericValues.DONT_CARE
                    ));
        }};

        Set<HttpStatusCode> httpStatusCodes = expectations.keySet();

        //When
        for (HttpStatusCode httpStatusCode : httpStatusCodes) {
            HttpResponse expectation = (HttpResponse) expectations.get(httpStatusCode);
            runTestRegisterUser(user,httpStatusCode,expectation);
        }
    }

    private void runTestIsAuthUser(User user,
                                   HttpStatusCode httpStatusCode,
                                   HttpResponse expectation) throws IOException {
        MockAuthServer.resetServer();

        //Given
        MockAuthServer.isUserAuthExpectations(user, httpStatusCode);

        //When
        HttpResponse response = httpAuthApi.isUserAuth(user.getUserName());

        //Then
        assertThat(response, samePropertyValuesAs(expectation));
    }

    private void runTestRegisterUser(User user,
                                   HttpStatusCode httpStatusCode,
                                   HttpResponse expectation) throws IOException {

        MockAuthServer.resetServer();

        //Given
        MockAuthServer.registerAuthExpectations(user, httpStatusCode);

        //When
        HttpResponse response = httpAuthApi.registerUser(user);

        //Then
        assertThat(response, samePropertyValuesAs(expectation));
    }
}
