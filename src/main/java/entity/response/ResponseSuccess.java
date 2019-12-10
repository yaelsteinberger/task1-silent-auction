package entity.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ResponseSuccess extends ResponseBasic {

    private Map data;

    @JsonCreator
    public ResponseSuccess(
            @JsonProperty("timestamp")String timestamp,
            @JsonProperty("status")int status,
            @JsonProperty("path")String path,
            @JsonProperty("data") Map data
    ) {
        super(timestamp, status,  path, false);
        this.data = data;
    }

    public Map getData() {
        return data;
    }
}
