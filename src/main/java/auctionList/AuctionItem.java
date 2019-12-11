package auctionList;

import entity.User;
import entity.auction.Bidder;
import entity.auction.Item;
import org.jetbrains.annotations.NotNull;
import server.AdminUser;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionItem {
    private final Item itemData;
    private Stack<Bidder> biddersList;

    public AuctionItem(Item item) {
        this.itemData = item;
        this.biddersList = new Stack<>();

        /* bump to start price */
        bumpPrice(itemData.getStartPrice());
    }

    public Item getItemData() {
        return itemData;
    }

    public Stack<Bidder> getBiddersList() {
        return biddersList;
    }

    public void addBidder(@NotNull User user, Long bidderValue){
        if(isBidValid(bidderValue)){
            this.biddersList.push(new Bidder(user,bidderValue));
        }
    }

    public Optional<Bidder> getWinningBidder(){


        return (this.biddersList.size() <= 1 ||
                this.biddersList.peek().getBidder().equals(AdminUser.ADMIN)) ?
                Optional.ofNullable(null) :
                Optional.of(this.biddersList.peek());
    }


    public void bumpPrice(Long value){
        /* bump the price */
        addBidder(AdminUser.ADMIN, value);
    }

    public void incrementBumpPrice(Long incrementValue){
        if(incrementValue > 0){
            Long maxValue = biddersList.peek().getBidderValue();
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

        AtomicReference<String> bidders = new AtomicReference<>("");
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

            bidders.set( bidders.get() + bidderStr);
        });

        text = text + bidders.get();

        return text;
    }

    private boolean isBidValid(Long value){

        if(!this.biddersList.empty()) {
            /* the offer should follow the bid increment and be higher than the last offer */
            Long lastValue = this.biddersList.peek().getBidderValue();
            boolean isValidIncrement = (value % itemData.getBidIncrement() == 0);

            return (value > lastValue) && isValidIncrement;
        }

        return true;
    }


}
