import client.Client;
import client.entity.ClientId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;


public class JustTesing {
    ObjectMapper mapper = new ObjectMapper();


    @Test
    public void myTest() throws Exception {
        ClientId.setClientId("hello");
        System.err.println(ClientId.getClientId());
        ClientId.setClientId("bye");
        System.err.println(ClientId.getClientId());
    }


}
