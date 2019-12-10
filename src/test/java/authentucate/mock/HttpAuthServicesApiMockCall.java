package authentucate.mock;

import entity.response.AbstractResponse;
import okhttp3.Request;
import services.authenticate.HttpAuthApi;


public class HttpAuthServicesApiMockCall extends HttpAuthApi {

    public HttpAuthServicesApiMockCall() {
        super();
    }

    @Override
    protected AbstractResponse makeCall(Request request) {

        return new ResponseMock((Request)request);
    }
}
