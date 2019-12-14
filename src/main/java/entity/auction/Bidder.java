package entity.auction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import entity.User;

import java.util.Date;

public class Bidder {
    private final ImmutableSet<Date> timeStamp;
    private final ImmutableSet<User> bidder;
    private final ImmutableSet<Long> bidderValue;

    @JsonCreator
    public Bidder(
            @JsonProperty("bidder") User bidder,
            @JsonProperty("bidderValue") Long bidderValue) {
            this.timeStamp = ImmutableSet.of(new Date());
            this.bidder = ImmutableSet.of(bidder);
            this.bidderValue = ImmutableSet.of(bidderValue);


    }

    public User getBidder() {
        return (User) bidder.toArray()[0];
    }

    public Long getBidderValue() {
        return (Long) bidderValue.toArray()[0];
    }

    public Date getTimeStamp() {
        return (Date) timeStamp.toArray()[0];
    }
}
