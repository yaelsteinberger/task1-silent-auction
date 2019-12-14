package auctionList;

import com.google.common.collect.ImmutableSet;
import entity.User;
import entity.auction.Bidder;
import entity.auction.Item;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.jetbrains.annotations.NotNull;
import server.AdminUser;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class AuctionItem {
    private final Item itemData;
    private final LinkedBlockingDeque<Bidder> biddersList;

    public AuctionItem(Item item) {
        this.itemData = item;
        this.biddersList = new LinkedBlockingDeque<>();


        /* bump to start price */
        addBidder(AdminUser.ADMIN, itemData.getStartPrice());
    }

    public Item getItemData() {
        return itemData;
    }

    public LinkedBlockingDeque<Bidder> getBiddersList() {
        return biddersList;
    }

    public void addBidder(@NotNull User user, Long bidderValue){
        if(isBidValid(bidderValue)){
            this.biddersList.add(new Bidder(user,bidderValue));
        }
    }

    public Optional<Bidder> getWinningBidder(){
        return (this.biddersList.size() <= 1 ||
                this.biddersList.peekLast().getBidder().equals(AdminUser.ADMIN)) ?
                Optional.ofNullable(null) :
                Optional.of(this.biddersList.peekLast());
    }

    public void incrementBumpPrice(Long incrementValue){
        if(incrementValue > 0){
            Long maxValue = biddersList.peekLast().getBidderValue();
            Long newValue = maxValue + incrementValue * itemData.getBidIncrement();

            /* bump the price */
            addBidder(AdminUser.ADMIN, newValue);
        }
    }


    public String toPrettyString(){

        Integer maxUserNameLen = this.biddersList.stream()
                .map(bidder -> bidder.getBidder().getUserName().length())
                .mapToInt(val -> val)
                .max()
                .orElseThrow(NoSuchElementException::new);

        String text = "";

        text = "Item Name: " + itemData.getName().toUpperCase() + "\n";
        text = text + "Description: " + itemData.getDescription() + "\n";
        text = text + "Start Price: $" + itemData.getStartPrice() + "\n";
        text = text + "Price Increment: $" + itemData.getBidIncrement() + "\n\n";
        text = text + "Bidders List: \n";

        StringBuilder retValue = new StringBuilder(text);
        this.biddersList.forEach(bidder -> {
            Integer stringPadding = (maxUserNameLen+1) - bidder.getBidder().getUserName().length();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS dd MMM yyyy zzz");
            String dateStr = dateFormat.format(bidder.getTimeStamp());

            int dateStrLen = dateStr.length();

            String bidderStr = String.format("%" + dateStrLen + "s | %s%4$" + stringPadding + "s| $%d\n",
                    dateStr,
                    bidder.getBidder().getUserName(),
                    bidder.getBidderValue(),
                    "" /* 4 -> for padding after username */);
            retValue.append(bidderStr);
        });



        return retValue.toString();
    }

    private boolean isBidValid(Long value){

        if(!this.biddersList.isEmpty()) {
            /* the offer should follow the bid increment and be higher than the last offer */
            Long lastValue = this.biddersList.peekLast().getBidderValue();
            boolean isValidIncrement = (value % itemData.getBidIncrement() == 0);

            return (value > lastValue) && isValidIncrement;
        }

        return true;
    }
}
