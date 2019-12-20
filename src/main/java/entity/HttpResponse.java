package entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.Map;

public class HttpResponse{

    private Integer status;
    private String timestamp;
    private String path;
    private Boolean isError;
    private String error;
    private String message;
    private Map data;

    public HttpResponse(
            @JsonProperty("timestamp")String timestamp,
            @JsonProperty("status")int status,
            @JsonProperty("path") String path,
            @JsonProperty("data") @Nullable Map data,
            @JsonProperty("error") @Nullable String error,
            @JsonProperty("message") @Nullable String message
    )  {
        this.timestamp = timestamp;
        this.status = status;
        this.path = path;
        this.data = data;
        this.error = error;
        this.message = message;
        this.isError = (error != null);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    @Nullable
    public Map getData() {
        return data;
    }

    @Nullable
    public String getError() {
        return error;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public Boolean isError() {
        return this.isError;
    }
}
