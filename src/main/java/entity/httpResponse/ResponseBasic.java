package entity.httpResponse;

import javax.annotation.Nullable;
import java.util.Map;

public class ResponseBasic extends AbstractResponse {

    private Integer status;
    private String timestamp;
    private String path;
    private Boolean isError;
    @Nullable
    private Map data;



    public ResponseBasic(
            String timeStamp,
            int status,
            String path,
            Map data,
            boolean isError
    )  {
        this.timestamp = timeStamp;
        this.status = status;
        this.path = path;
        this.data = data;
        this.isError = isError;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public Integer getStatusCode() {
        return status;
    }

    public String getPath() {
        return path;
    }

    @Nullable
    public Map getData() {
        return data;
    }

    @Override
    public Boolean isError() {
        return this.isError;
    }
}
