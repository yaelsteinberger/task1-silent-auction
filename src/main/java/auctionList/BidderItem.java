package auctionList;

import entity.User;

import java.util.Date;

public class BidderItem {
    private final Date timeStamp;
    private final User bidder;
    private final Long bidderValue;
//    private static final Object mutex = new Object();

    public BidderItem(User bidder, Long bidderValue) {

//        synchronized(mutex){
            this.timeStamp = new Date();
            this.bidder = bidder;
            this.bidderValue = bidderValue;
//        }

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
