package MOCKs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;
import org.mockserver.model.HttpStatusCode;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class MockAuthResponseBody<T> {
        private final HttpStatusCode status;
        private final String path;

        @Nullable
        private final String message;

        @Nullable
        private final T data;

    public MockAuthResponseBody(
            HttpStatusCode status,
            String path,
            @Nullable String message,
            @Nullable T data) {
        this.status = status;
        this.path = path;
        this.message = message;
        this.data = data;
    }

    public Map getResponseMessage(){
        String date = new Date().toString();

        return Collections.unmodifiableMap(new LinkedHashMap<>() {{
            put("timestamp", MockGenericValues.DONT_CARE);
            put("status", status.code());
            if((status.code() >= 400) && (status.code() < 600)){put("error", status.name());}
            if (message != null) {put("message", MockGenericValues.DONT_CARE);}
            put("path", path);
            if (data != null) {put("data", data);}
        }});
    }

    public String writeToJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(getResponseMessage());
    }
}
