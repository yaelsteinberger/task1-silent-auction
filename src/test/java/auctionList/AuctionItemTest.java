package auctionList;

import MOCKs.MockUsers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.User;
import entity.auction.Bidder;
import entity.auction.Item;
import org.junit.BeforeClass;
import org.junit.Test;
import server.AdminUser;
import java.util.function.Consumer;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;


public class AuctionItemTest {
    private static AuctionItem auctionItem;

    ObjectMapper mapper = new ObjectMapper();
    private static ArrayList<Bidder> bidders;
    private static User[] users;

    private static Item item = new Item(
            "Item1",
            "Sparkly Item",
            100L, 20L);





    @BeforeClass
    public static void setup(){
        //Given
        auctionItem = new AuctionItem(item);
        users = MockUsers.getUsers();
        bidders = new ArrayList<>(){{
            add(new Bidder(users[2],120L));
            add(new Bidder(users[1],140L));
            add(new Bidder(users[0],160L));
            add(new Bidder(users[2],180L));
            add(new Bidder(users[0],200L));
        }};
    }

    private void generateBiddersListInAuctionItem(){
        bidders.forEach(bidder -> {
            auctionItem.addBidder(bidder.getBidder(),bidder.getBidderValue());
            try {Thread.sleep(1);} // so the time of each bidder will differ
            catch (InterruptedException e) {e.printStackTrace();}
        });
    }

    @Test
    public void getItemDataTest() {
        //When
        Item itemData = auctionItem.getItemData();

        //Then
        Item expectedItemData = item;
        assertThat(itemData, samePropertyValuesAs(expectedItemData));
    }

    @Test
    public void addBidderTest() throws IOException {
        //Given
        Map expectations = new HashMap<String,Object>(){{
            put("VALID_VALUE",
                    new HashMap<String,Object>(){{
                        put("bidValue",240L);
                        put("latestBidder", new Bidder(users[1],240L));
                    }});
            put("INVALID_BELOW_MAX",
                    new HashMap<String,Object>(){{
                        put("bidValue",180L);
                        put("latestBidder", bidders.get(bidders.size()-1));
                    }});

            put("INVALID_NOT_FOLLOW_INCREMENT",
                    new HashMap<String,Object>(){{
                        put("bidValue",235L);
                        put("latestBidder", bidders.get(bidders.size()-1));
                    }});
        }};


        Set<String> keys = expectations.keySet();
        for (String key : keys) {
            auctionItem = new AuctionItem(item);
            generateBiddersListInAuctionItem();
            runTestAddBidder(users[1], (Map) expectations.get(key));
        }
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
    public void getWinningBidder() {
        //Given
        /* passing different methods to action in the loop - this way: */
        final Consumer<Bidder> addBidder = AuctionItemTest::addBidderStaticWrapper;
        final Consumer<Long> bumpPrice = AuctionItemTest::bumpPriceStaticWrapper;

        Bidder newBidder = new Bidder(users[1], 240L);
        Map expectations = new HashMap<String,Object>(){
            {
                put("BIDDER",
                    new HashMap<String, Object>() {{
                        put("isGenerateBiddersList", true);
                        put("methodToAction", addBidder);
                        put("inputParams",newBidder);
                        put("winningBidder", newBidder.getBidder());
                        put("winningValue", newBidder.getBidderValue());
                    }});
                put("NONE_NO_BIDDERS",
                        new HashMap<String, Object>() {{
                            put("isGenerateBiddersList", false);
                            put("methodToAction", null);
                            put("inputParams",null);
                            put("winningBidder", null);
                            put("winningValue", null);
                        }});
                put("NONE_NO_BIDDERS_AFTER_BUMP",
                        new HashMap<String, Object>() {{
                            put("isGenerateBiddersList", true);
                            put("methodToAction", bumpPrice);
                            put("inputParams",3L);
                            put("winningBidder", null);
                            put("winningValue", null);
                        }});
            }};

        Set<String> keys = expectations.keySet();
        for (String key : keys) {
            auctionItem = new AuctionItem(item);
            runTestGetWinningBidder((Map) expectations.get(key));
        }
    }

    private static void addBidderStaticWrapper(Bidder bidder) {
        auctionItem.addBidder(bidder.getBidder(),bidder.getBidderValue());
    }

    private static void bumpPriceStaticWrapper(Long incrementValue) {
        auctionItem.incrementBumpPrice(incrementValue);
    }

    private void runTestAddBidder(User user, Map expectations){

        // When
        auctionItem.addBidder(user, (Long) expectations.get("bidValue"));
        LinkedBlockingDeque<Bidder> biddersList = auctionItem.getBiddersList();
        Bidder latestBidder = biddersList.peekLast();

        // Then
        Bidder expectedBidder = (Bidder) expectations.get("latestBidder");
        assertThat(latestBidder.getBidderValue(), is(expectedBidder.getBidderValue()));
        assertThat(latestBidder.getBidder(), samePropertyValuesAs(expectedBidder.getBidder()));
    }

    private void runTestGetWinningBidder(Map expectations){

        // Given
        if((boolean)expectations.get("isGenerateBiddersList")){generateBiddersListInAuctionItem();}

        // When
        if(expectations.get("methodToAction") != null){
            final Consumer<Object> methodToAction = (Consumer<Object>) expectations.get("methodToAction");
            Object inputParam = expectations.get("inputParams");
            methodToAction.accept(inputParam);
        }

        Optional<Bidder> winner = auctionItem.getWinningBidder();
        User winningBidder =  null;
        Long winningValue = null;
        if(winner.isPresent()){
            winningBidder = winner.get().getBidder();
            winningValue = winner.get().getBidderValue();
        }

        // Then
        assertThat(winningValue, is(expectations.get("winningValue")));
        try{
            assertThat(winningBidder, samePropertyValuesAs(expectations.get("winningBidder")));
        }catch(NullPointerException e){
            /* if there is no "user" (null) need to use "is" in the assert */
            assertThat(winningBidder, is(expectations.get("winningBidder")));
        }
    }
}
