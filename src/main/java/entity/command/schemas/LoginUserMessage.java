package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.User;


public class LoginUserMessage extends BaseMessage {
    private final User user;

    /* to be able to use ObjectMapper to read the Command and convert it as this type
    must clarify which is the class's constructor and it's members for the conversion */
    @JsonCreator
    public LoginUserMessage(@JsonProperty("user")  User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
