package auctionList;


import MOCKs.MockAuctionItems;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class AuctionItemsListTest {

    static private AuctionItemsList auctionItemsList;

    @BeforeClass
    static public void setup(){
        //Given
        auctionItemsList = new AuctionItemsList(MockAuctionItems.streamAuctionItems());
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
        AuctionItem expectedAuctionItem = MockAuctionItems.getAuctionItems()[Math.toIntExact(id)];
        assertThat(auctionItem, is(expectedAuctionItem));
    }
}
