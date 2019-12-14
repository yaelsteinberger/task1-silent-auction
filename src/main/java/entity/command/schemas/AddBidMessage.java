package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddBidMessage extends BaseMessage{

    private final Long auctionItemId;
    private final Long bidValue;


    @JsonCreator
    public AddBidMessage(
            @JsonProperty("auctionItemId") Long auctionItemId,
            @JsonProperty("bidValue") Long bidValue) {

        this.auctionItemId = auctionItemId;
        this.bidValue = bidValue;
    }

    public Long getAuctionItemId() {
        return auctionItemId;
    }

    public Long getBidValue() {
        return bidValue;
    }
}
