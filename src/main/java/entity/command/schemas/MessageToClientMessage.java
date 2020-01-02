package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

/* This class is used by ObjectMapper.convertValue so it has to have
a default constructor and setters methods for the it's members */
public class MessageToClientMessage extends BaseMessage{
    private final String message;

    @Nullable
    private final Object data;

    /* to be able to use ObjectMapper to read the Command and convert it as this type
    must clarify which is the class's constructor and it's members for the conversion */
    @JsonCreator
    public MessageToClientMessage(
            @JsonProperty("message") String message,
            @JsonProperty("data") Object data) {
        this.message = message;
        this.data = data;
    }

    public MessageToClientMessage(@JsonProperty("message")String message){
        this.message = message;
        this.data = null;
    }


    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
