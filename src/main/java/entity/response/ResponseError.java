package entity.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseError extends ResponseBasic {

    private String error;
    private String message;

    @JsonCreator
    public ResponseError(
            @JsonProperty("timestamp")String timestamp,
            @JsonProperty("status")int status,
            @JsonProperty("error")String error,
            @JsonProperty("message") String message,
            @JsonProperty("path")String path
    ) {
        super(timestamp, status, path, true);
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
