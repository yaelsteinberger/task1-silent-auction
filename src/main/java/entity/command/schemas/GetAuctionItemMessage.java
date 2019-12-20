package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.User;


public class GetAuctionItemMessage extends BaseMessage {
    private final Long itemId;

    /* to be able to use ObjectMapper to read the Command and convert it as this type
    must clarify which is the class's constructor and it's members for the conversion */
    @JsonCreator
    public GetAuctionItemMessage(@JsonProperty("itemId")  Long itemId) {
        this.itemId = itemId;
    }

    public Long getItemId() {
        return itemId;
    }
}
