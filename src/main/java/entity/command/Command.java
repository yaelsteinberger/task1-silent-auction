package entity.command;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.command.schemas.BaseMessage;

public class Command{


    private final Integer opcode;
    private final BaseMessage message;

    /* to be able to use ObjectMapper to read the input streaming data and convert it as
    this type, must clarify which is the class's constructor and it's members for the
    conversion */
    @JsonCreator
    public Command(
            @JsonProperty("opcode") Integer opcode,
            @JsonProperty("message") BaseMessage message
    ) {
        this.opcode = opcode;
        this.message = message;
    }

    public Integer getOpcode() {
        return opcode;
    }

    public BaseMessage getMessage() {
        return message;
    }
}
