package entity.auction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.User;

import java.util.Date;

public class Bidder {
    private final Date timeStamp;
    private final User bidder;
    private final Long bidderValue;

    @JsonCreator
    public Bidder(
            @JsonProperty("bidder") User bidder,
            @JsonProperty("bidderValue") Long bidderValue) {
            this.timeStamp = new Date();
            this.bidder = bidder;
            this.bidderValue = bidderValue;
    }

    public User getBidder() {
        return bidder;
    }

    public Long getBidderValue() {
        return bidderValue;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
}
