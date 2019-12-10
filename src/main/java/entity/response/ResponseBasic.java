package entity.response;

public class ResponseBasic extends AbstractResponse {

    private Integer status;
    private String timestamp;
    private String path;
    private Boolean isError;


    public ResponseBasic(
            String timeStamp,
            int status,
            String path,
            boolean isError
    )  {
        this.timestamp = timeStamp;
        this.status = status;
        this.path = path;
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

    @Override
    public Boolean isError() {
        return this.isError;
    }
}
