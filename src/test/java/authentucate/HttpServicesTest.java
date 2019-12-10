package authentucate;

import authentucate.mock.HttpAuthServicesApiMockCall;
import authentucate.mock.ResponseMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import server.ServerProperties;

import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpServicesTest {
    String baseUrl = "http://localhost:666/";
    String propFilePath = "src\\test\\resources\\mockConfig.properties";
    HttpAuthServicesApiMockCall httpApi;
    ObjectMapper mapper = new ObjectMapper();


    @Before
    public void setup(){
        //Given
        ServerProperties.readConfigPropertiesFile(propFilePath);
        httpApi = new HttpAuthServicesApiMockCall();

    }


    @Test
    public void isAuthTest() throws JsonProcessingException {
        // Given
        String userName = "username";

        // When
        ResponseMock responseMock = (ResponseMock) httpApi.isUserAuth(userName);
        Map requestObject = responseMock.getResponse();

        //Then
        String expectedMethod = "get";
        String expectedUrl = baseUrl + "user/" + userName + "/isactive/";
        String expectedBody = null;

        assertThat(requestObject.get("method").toString().toLowerCase(), is(expectedMethod));
        assertThat(requestObject.get("url"), is(expectedUrl));
        assertThat(requestObject.get("body"), is(nullValue()));
    }

    @Test
    public void registerUserTest() throws JsonProcessingException {
        // Given
        User user = new User(
                "username",
                "FirstName",
                "LastName"
        );

        // When
        ResponseMock responseMock = (ResponseMock) httpApi.registerUser(user);
        Map requestObject = responseMock.getResponse();

        //Then
        String expectedMethod = "post";
        String expectedUrl = baseUrl + "user/register/";
        String expectedBody = "{\"userName\":\"username\",\"firstName\":\"FirstName\",\"lastName\":\"LastName\"}";

        assertThat(requestObject.get("method").toString().toLowerCase(), is(expectedMethod));
        assertThat(requestObject.get("url"), is(expectedUrl));
        assertThat(requestObject.get("body").toString(), is(expectedBody));
    }
}
