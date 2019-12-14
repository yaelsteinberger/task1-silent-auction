package entity.httpResponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ResponseSuccess extends ResponseBasic {

    @JsonCreator
    public ResponseSuccess(
            @JsonProperty("timestamp")String timestamp,
            @JsonProperty("status")int status,
            @JsonProperty("path") String path,
            @JsonProperty("data") Map data
    ) {
        super(timestamp, status,  path, data,false);
    }
}
