package auctionList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.auction.Bidder;
import entity.auction.Item;
import javafx.util.converter.DateTimeStringConverter;
import org.junit.Before;
import org.junit.Test;
import server.AdminUser;


import java.io.DataInput;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;


public class AuctionItemTest {
    ObjectMapper mapper = new ObjectMapper();
    Item item = new Item(
            "Item1",
            "Sparkly Item",
            100L, 20L);
    AuctionItem auctionItem;

    ArrayList users;
    ArrayList<Bidder> bidders;

    @Before
    public void setup(){
        //Given
        auctionItem = new AuctionItem(item);
        users = new ArrayList<User>(){{
            add(new User("username1","FirstName1","LastName1"));// 0
            add(new User("username2","FirstName2","LastName2"));// 1
            add(new User("username3","FirstName3","LastName3"));// 2
        }};
        bidders = new ArrayList<>(){{
            add(new Bidder((User)users.get(2),120L));
            add(new Bidder((User)users.get(1),140L));
            add(new Bidder((User)users.get(0),120L));
            add(new Bidder((User)users.get(2),180L));
            add(new Bidder((User)users.get(0),200L));
            add(new Bidder((User)users.get(2),160L));
        }};
    }

    @Test
    public void addBidderTest() throws IOException {
        //Given
        bidders.forEach(bidder -> {
            auctionItem.addBidder(bidder.getBidder(),bidder.getBidderValue());
            try {Thread.sleep(1);}
            catch (InterruptedException e) {e.printStackTrace();}
        });

        //When
        LinkedBlockingDeque<Bidder> biddersList = auctionItem.getBiddersList();

        String firstBidderUserName = biddersList.getFirst().getBidder().getUserName();
        int listSize = biddersList.size();

        //Then
        String expectedFirstBidder = AdminUser.ADMIN.getUserName();
        int expectedSize = 5;
        assertThat(firstBidderUserName, is(expectedFirstBidder));
        assertThat(listSize, is(expectedSize));
    }


    @Test
    public void incrementBumpPriceTest() throws InterruptedException {
        //Given
        bidders.forEach(bidder -> {
            auctionItem.addBidder(bidder.getBidder(),bidder.getBidderValue());
            try {Thread.sleep(1);}
            catch (InterruptedException e) {e.printStackTrace();}
        });

        //When
        auctionItem.incrementBumpPrice(3L);

        //Then
        Bidder bumpPriceBidder = auctionItem.getBiddersList().peekLast();
        String expectedFirstBidder = AdminUser.ADMIN.getUserName();
        Long expectedBumpedPrice = 260L;

        assertThat(bumpPriceBidder.getBidder().getUserName(), is(expectedFirstBidder));
        assertThat(bumpPriceBidder.getBidderValue(), is(expectedBumpedPrice));
    }


    @Test
    public void winningBidderTest() throws JsonProcessingException {
        //Given
        bidders.forEach(bidder -> {
            auctionItem.addBidder(bidder.getBidder(),bidder.getBidderValue());
            try {Thread.sleep(1);}
            catch (InterruptedException e) {e.printStackTrace();}
        });

        //When
        Optional bidder = auctionItem.getWinningBidder();

        //Then
        Long expectedBidderValue = bidders.get(4).getBidderValue();
        assertThat(bidder.isPresent(), is(true));
        assertThat(((Bidder)bidder.get()).getBidderValue(), is(expectedBidderValue));
    }

    @Test
    public void noWinningBidderTest() throws JsonProcessingException {
        //When
        Optional bidder = auctionItem.getWinningBidder();

        //Then
        assertThat(bidder.isPresent(), is(false));
    }

    @Test
    public void noWinningBidderAfterBumpTest() throws JsonProcessingException {
        //Given
        bidders.forEach(bidder -> {
            auctionItem.addBidder(bidder.getBidder(),bidder.getBidderValue());
            try {Thread.sleep(1);}
            catch (InterruptedException e) {e.printStackTrace();}
        });

        //When
        auctionItem.incrementBumpPrice(4L);
        Optional bidder = auctionItem.getWinningBidder();

        //Then
        assertThat(bidder.isPresent(), is(false));
    }
}
