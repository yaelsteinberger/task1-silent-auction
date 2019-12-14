package auctionList;


import entity.auction.Item;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class AuctionItemsListTest {

    static private AuctionItemsList auctionItemsList;
    static private AuctionItem[] auctionItems;

    @BeforeClass
    static public void setup(){
        //Given
        auctionItems = new AuctionItem[]{
                new AuctionItem(new Item("Table", "Made of green wood", 300L, 50L)),
                new AuctionItem(new Item("Bowle", "Created from a rainbow", 30L, 5L)),
                new AuctionItem(new Item("Ring", "Made of gold from the future", 4000L, 150L)),
                new AuctionItem(new Item("Laptop", "For gamers who like sparkles", 2000L, 100L))
        };

        auctionItemsList = new AuctionItemsList(Arrays.stream(auctionItems));
    }

    @Test
    public void getAuctionItemsListTest() throws IOException {

        // When
        Map list = auctionItemsList.getAuctionItemsList();

        //Then
        int expectedItemsNumber = 4;
        assertThat(list.size(), is(expectedItemsNumber));
    }

    @Test
    public void findAuctionItemByIdTest() throws IOException {
        //Given
        Long id = 2L;

        // When
        AuctionItem auctionItem = auctionItemsList.findById(id);

        //Then
        AuctionItem expectedAuctionItem = auctionItems[2];
        assertThat(auctionItem, is(expectedAuctionItem));
    }
}
