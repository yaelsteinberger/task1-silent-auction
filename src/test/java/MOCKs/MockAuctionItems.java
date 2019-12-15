package MOCKs;

import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import entity.auction.Item;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.stream.Stream;

public class MockAuctionItems {
        private static AuctionItem[] auctionItems = new AuctionItem[]{
             new AuctionItem(new Item("Table", "Made of green wood", 300L, 50L)),
             new AuctionItem(new Item("Bowle", "Created from a rainbow", 30L, 5L)),
             new AuctionItem(new Item("Ring", "Made of gold from the future", 4000L, 150L)),
             new AuctionItem(new Item("Laptop", "For gamers who like sparkles", 2000L, 100L))
        };


        public static Stream<AuctionItem> streamAuctionItems(){
            return Arrays.stream(auctionItems);
        }

        public static AuctionItem[] getAuctionItems() {
        return auctionItems;
        }

        public static AuctionItemsList generateAuctionItemsList() {
            return new AuctionItemsList(streamAuctionItems());
        }
}
