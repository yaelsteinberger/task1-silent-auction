package entity.httpResponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ResponseError extends ResponseBasic {

    private String error;
    private String message;

    @JsonCreator
    public ResponseError(
            @JsonProperty("timestamp")String timestamp,
            @JsonProperty("status")int status,
            @JsonProperty("error")String error,
            @JsonProperty("message") String message,
            @JsonProperty("path")String path,
            @JsonProperty("data") Map data
    ) {
        super(timestamp, status, path, null, true);
        this.message = message;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }


}
