package auctionList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.User;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionItem {
    private final String name;
    private final String description;
    private final Long startPrice;
    private final Long bidIncrement;

    @JsonIgnore
    private Stack<BidderItem> biddersList;

    @JsonCreator
    public AuctionItem(
            @JsonProperty("name")String name,
            @JsonProperty("description")String description,
            @JsonProperty("startPrice")Long startPrice,
            @JsonProperty("bidIncrement")Long bidIncrement) {
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.bidIncrement = bidIncrement;
        biddersList = new Stack<BidderItem>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getStartPrice() {
        return startPrice;
    }

    public Long getBidIncrement() {
        return bidIncrement;
    }

    public List getBiddersList() {
        return biddersList;
    }

    public void addBidder(@NotNull User user, Long bidderValue){
        biddersList.add(new BidderItem(user,bidderValue));
    }

    public void forceBumpPrice(Integer bumpValueMultiple){
        Long bumpValue = this.bidIncrement * bumpValueMultiple;
        Long maxVal = biddersList.stream()
                .map(BidderItem::getBidderValue)
                .max(Long::compare).get();

        /* in case the max value doesn't follow the bidIncrement value, get the closest value */
        Long validMaxValue = maxVal - (maxVal % bidIncrement);
        Long newValue = validMaxValue + bumpValue;

        /* bump the price */
        this.biddersList.add(new BidderItem(
                new User("administrator","Admin","Manager"),newValue));
    }

    public Long getMaxBidOfferValue(){
        /* find the maximum offered price */
        Optional<Long> maxValue = biddersList.stream()
                .filter(item -> (item.getBidderValue() % bidIncrement) == 0)
                .map(BidderItem::getBidderValue)
                .max(Long::compare);

        return (maxValue.isPresent() && (maxValue.get() >= this.startPrice)) ?
                maxValue.get() : this.startPrice ;
    }

    public Optional getWinningBidder(){

        Optional retValue = Optional.ofNullable(null);

        if(biddersList.isEmpty()){
            return retValue;
        }

        /* find the maximum offered price */
        Long maxPrice = getMaxBidOfferValue();

        /* find all entries with max price */
        Object[] newList = biddersList.stream()
                .filter(item -> (item.getBidderValue() >= maxPrice))
                .sorted((item1,item2) -> ((item1.getTimeStamp().getTime() < item2.getTimeStamp().getTime()) ? 1 : -1))
                .filter(item -> (item.getBidderValue() % bidIncrement) == 0)
                .toArray();

        /* if there are duplicates, get the entry with the oldest bidding date */
        boolean isListEmpty = (newList.length > 0);
        if(isListEmpty && !((BidderItem) newList[newList.length - 1]).getBidder().getUserName().equals("administrator")){
            retValue = Optional.of((BidderItem) newList[newList.length-1]);
        }

        return retValue;
    }

    public String getFormattedString(){
        String text = "";

        text = "Item Name: " + name.toUpperCase() + "\n";
        text = text + "Description: " + description + "\n";
        text = text + "Start Price: $" + startPrice + "\n";
        text = text + "Price Increment: $" + bidIncrement + "\n\n";
        text = text + "Bidders List: \n";

        AtomicReference<String> bidders = new AtomicReference<>("");
        biddersList.forEach(bidder -> {
            bidders.set(bidders + bidder.getTimeStamp().toString() + " | " + bidder.getBidder().getUserName() + " | $" + bidder.getBidderValue() + "\n");
        });

        text = text + bidders.get();

        return text;
    }
}
