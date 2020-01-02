package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddBidMessage extends BaseMessage{

    private final String userId;
    private final Long auctionItemId;
    private final Long bidValue;


    @JsonCreator
    public AddBidMessage(
            @JsonProperty("userId") String userId,
            @JsonProperty("auctionItemId") Long auctionItemId,
            @JsonProperty("bidValue") Long bidValue) {

        this.userId = userId;
        this.auctionItemId = auctionItemId;
        this.bidValue = bidValue;
    }

    public String getUserId() {
        return userId;
    }

    public Long getAuctionItemId() {
        return auctionItemId;
    }

    public Long getBidValue() {
        return bidValue;
    }


}
