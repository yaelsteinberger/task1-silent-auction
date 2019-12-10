package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsersListMessage extends BaseMessage {

    private final String[] usersList;

    /* to be able to use ObjectMapper to read the Command and convert it as this type
    must clarify which is the class's constructor and it's members for the conversion */
    @JsonCreator
    public UsersListMessage(@JsonProperty("usersList") String[] usersList) {
        this.usersList = usersList;
    }

    public String[] getUsersList() {
        return usersList;
    }
}
