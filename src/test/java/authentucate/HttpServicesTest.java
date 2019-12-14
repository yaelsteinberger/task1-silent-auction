package authentucate;

import MOCKs.MockAuthServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import entity.User;
import entity.HttpResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import server.ServerProperties;
import authenticate.HttpAuthApi;
import authenticate.PathNames;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpServicesTest {
    static private HttpAuthApi httpAuthApi;
    static private MockAuthServer mockAuthServer;

    @BeforeClass
    static public void setup() throws JsonProcessingException {

        //Given
        String propFilePath = "src\\test\\resources\\mockConfig.properties";
        ServerProperties.readConfigPropertiesFile(propFilePath);
        String mockHost = (String) ServerProperties.getProperties().get("authServer.host");
        String mockPort = (String) ServerProperties.getProperties().get("authServer.port");
        httpAuthApi = new HttpAuthApi();
        mockAuthServer = new MockAuthServer(mockHost,Integer.parseInt(mockPort));
        mockAuthServer.startServer();
    }

    @AfterClass
    static public void tearDown() throws JsonProcessingException {
        mockAuthServer.stopServer();
    }

    @Before
    public void cleanServerCache(){
        /* Before each test clear the cache from any previous responses and expectations */
        mockAuthServer.resetServer();
    }

    @Test
    public void isAuthActiveTest() {

        // Given
        String userName = "username";
        HttpStatusCode status = HttpStatusCode.OK_200;

        // When
        try {
            mockAuthServer.isUserAuthExpectations(userName,status);
            HttpResponse response = (HttpResponse) httpAuthApi.isUserAuth(userName);

            //Then
            String expectedPath = "/" + PathNames.IS_USER_AUTH.replace("{userName}",userName);

//            PrintHelper.printPrettyInRed(response);
            assertThat(response.isError(), is(false));
            assertThat(response.getPath(), is(expectedPath));
            assertThat(response.getStatusCode(), is(status.code()));
            assertThat(response.getError(), nullValue());
            assertThat(response.getMessage(), nullValue());
            assertThat(response.getData().get("active"), is(true));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isAuthDisabledTest()  {
        // Given
        String userName = "username";
        HttpStatusCode status = HttpStatusCode.FORBIDDEN_403;

        // When
        try {
            mockAuthServer.isUserAuthExpectations(userName,status);
            HttpResponse response = (HttpResponse) httpAuthApi.isUserAuth(userName);

            //Then
            String expectedPath = "/" + PathNames.IS_USER_AUTH.replace("{userName}",userName);

//            PrintHelper.printPrettyInRed(response);
            assertThat(response.isError(), is(true));
            assertThat(response.getStatusCode(), is(status.code()));
            assertThat(response.getError(), is(status.name()));
            assertThat(response.getMessage(), isA(String.class));
            assertThat(response.getPath(), is(expectedPath));
            assertThat(response.getData().get("active"), is(false));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isAuthNotExistsTest()  {
        // Given
        String userName = "username";
        HttpStatusCode status = HttpStatusCode.NOT_FOUND_404;

        // When
        try {
            mockAuthServer.isUserAuthExpectations(userName,status);
            HttpResponse response = (HttpResponse) httpAuthApi.isUserAuth(userName);

            //Then
            String expectedPath = "/" + PathNames.IS_USER_AUTH.replace("{userName}",userName);

//            PrintHelper.printPrettyInRed(response);
            assertThat(response.isError(), is(true));
            assertThat(response.getStatusCode(), is(status.code()));
            assertThat(response.getError(), is(status.name()));
            assertThat(response.getMessage(), isA(String.class));
            assertThat(response.getPath(), is(expectedPath));
            assertThat(response.getData(), nullValue());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registerUserSuccessTest()  {
        // Given
        HttpStatusCode status = HttpStatusCode.OK_200;
        User user = new User("username","FirstName","LastName");


        // When
        try {
            mockAuthServer.registerAuthExpectations(user,status);
            HttpResponse response = (HttpResponse) httpAuthApi.registerUser(user);

        //Then
        String expectedPath = "/" + PathNames.REGISTER_USER;

//            PrintHelper.printPrettyInRed(response);
        assertThat(response.isError(), is(false));
        assertThat(response.getStatusCode(), is(status.code()));
        assertThat(response.getError(), nullValue());
        assertThat(response.getMessage(), nullValue());
        assertThat(response.getPath(), is(expectedPath));
        assertThat(response.getData(), isA(Map.class));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void registerUserFailTest()  {
        // Given
        HttpStatusCode status = HttpStatusCode.FORBIDDEN_403;
        User user = new User("username","FirstName","LastName");


        // When
        try {
            mockAuthServer.registerAuthExpectations(user,status);
            HttpResponse response = (HttpResponse) httpAuthApi.registerUser(user);

            //Then
            String expectedPath = "/" + PathNames.REGISTER_USER;

//            PrintHelper.printPrettyInRed(response);
            assertThat(response.isError(), is(true));
            assertThat(response.getStatusCode(), is(status.code()));
            assertThat(response.getError(), is(status.name()));
            assertThat(response.getMessage(), isA(String.class));
            assertThat(response.getPath(), is(expectedPath));
            assertThat(response.getData(), nullValue());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
