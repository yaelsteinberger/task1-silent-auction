package auctionList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class AuctionItemTest {
    ObjectMapper mapper = new ObjectMapper();
    AuctionItem bidItem = new AuctionItem(
            "Item1",
            "Sparkly Item",
            100L, 20L);

    ArrayList users;
    ArrayList<BidderItem> bidders;

    @Before
    public void setup(){
        //Given
        users = new ArrayList<User>(){{
            add(new User("username1","FirstName1","LastName1"));// 0
            add(new User("username2","FirstName2","LastName2"));// 1
            add(new User("username3","FirstName3","LastName3"));// 2
        }};
        bidders = new ArrayList<>(){{
            add(new BidderItem((User)users.get(2),120L));
            add(new BidderItem((User)users.get(1),140L));
            add(new BidderItem((User)users.get(0),120L));
            add(new BidderItem((User)users.get(2),180L));
            add(new BidderItem((User)users.get(0),200L));
            add(new BidderItem((User)users.get(2),160L));
        }};

        bidders.forEach(bidder -> {
            bidItem.addBidder(bidder.getBidder(),bidder.getBidderValue());
            try {Thread.sleep(1000);}
            catch (InterruptedException e) {e.printStackTrace();}
        });
    }

    @Test
    public void addBidderTest(){
        //Given
        int currentSize = bidItem.getBiddersList().size();

        //When
        bidItem.addBidder((User)users.get(2),200L);

        //Then
        int expectedSize = 1 + currentSize;
        List biddersList = bidItem.getBiddersList();


        assertThat(biddersList.size(), is(expectedSize));
    }

    @Test
    public void maxBidOfferValueTest() throws InterruptedException {
        //Given
        bidItem.addBidder((User)users.get(0),300L);
        Thread.sleep(5);
        bidItem.addBidder((User)users.get(2),310L);

        //When
        Long maxValue = bidItem.getMaxBidOfferValue();

        //Then
        Long expectedValue = 300L;
        assertThat(maxValue, is(expectedValue));
    }

    @Test
    public void forceBumpPriceTest() throws InterruptedException {
        //Given
        bidItem.addBidder((User)users.get(0),220L);
        Thread.sleep(5);
        bidItem.addBidder((User)users.get(2),230L);

        //When
        bidItem.forceBumpPrice(5);
        Long maxPrice = bidItem.getMaxBidOfferValue();
        List<BidderItem> list =  bidItem.getBiddersList();
        Object[] newList = list.stream()
                .filter(item -> (item.getBidderValue() >= maxPrice))
                .sorted((item1,item2) -> ((item1.getTimeStamp().getTime() < item2.getTimeStamp().getTime()) ? 1 : -1))
                .filter(item -> (item.getBidderValue() % bidItem.getBidIncrement()) == 0)
                .toArray();
        BidderItem user = (BidderItem) newList[newList.length-1];


        //Then
        Long expectedBumpedValue = 320L;
        String expectedUserName = "administrator";
        assertThat(user.getBidderValue(), is(expectedBumpedValue));
        assertThat(user.getBidder().getUserName(), is(expectedUserName));
    }


    @Test
    public void winningBidder() throws InterruptedException {
        //Given
        bidItem.addBidder((User)users.get(1),200L);
        Thread.sleep(5);
        bidItem.addBidder((User)users.get(2),210L);

        //When
        BidderItem bidder = (BidderItem) bidItem.getWinningBidder().get();

        //Then
        User expectedUser = (User)users.get(0);
        assertThat(bidder.getBidder(), is(expectedUser));
        // System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bidItem.getBiddersList()));
    }

    @Test
    public void noBidders() throws InterruptedException {
        //Given
        AuctionItem item = new AuctionItem(
                "Item",
                "Sparkly Item",
                100L, 20L);

        //When
        Optional user = item.getWinningBidder();

        //Then
        assertThat(user.isPresent(), is(false));
    }

    @Test
    public void noWinningBidder() throws InterruptedException {
        //Given


        //When
        bidItem.forceBumpPrice(5);
        Optional user = bidItem.getWinningBidder();

        //Then
        assertThat(user.isPresent(), is(false));
    }

    @Test
    public void auctionItemToString() throws InterruptedException {
        System.err.println(bidItem.getFormattedString());
    }
}
