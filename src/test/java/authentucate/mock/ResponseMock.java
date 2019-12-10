package authentucate.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.response.AbstractResponse;
import okhttp3.Request;
import okio.Buffer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResponseMock extends AbstractResponse {
    private final Map response;

    public ResponseMock(Request request) {
        this.response = encodeRequest(request);
    }

    @Override
    public Boolean isError() {
        return false;
    }

    public Map getResponse() {
        return response;
    }

    private Map encodeRequest(Request request)  {

        ObjectMapper mapper = new ObjectMapper();
        Buffer bodyBuffer = new Buffer();
        HashMap responseMock = null;

        try {
            String body = null;
            if(request.body() != null){
                request.body().writeTo(bodyBuffer);
                body = bodyBuffer.readUtf8();
            }

            JsonNode finalBody = (body != null) ? mapper.readTree(body) : null;
            responseMock =  new HashMap(){{
                put("url",request.url().toString());
                put("method",request.method());
                put("body",finalBody);
            }};

        } catch (final IOException e) {
            e.printStackTrace();
        }
        return responseMock;
    }
}

/*


public class ResponseMock <T>  extends AbstractResponse {
    private final T response;

    public ResponseMock(T response) {
        this.response = response;
    }

    @Override
    public Boolean isError() {
        return false;
    }

    public <T> T getResponse() {

        return (T) response;
    }
}
 */
