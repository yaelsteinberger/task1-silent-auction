package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/* This class is used by ObjectMapper.convertValue so it has to have
a default constructor and setters methods for the it's members */
public class MessageToClientMessage extends BaseMessage{
    private final String message;

    /* to be able to use ObjectMapper to read the Command and convert it as this type
    must clarify which is the class's constructor and it's members for the conversion */
    @JsonCreator
    public MessageToClientMessage(@JsonProperty("message")String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
