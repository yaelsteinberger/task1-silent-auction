package auctionList;

import org.apache.commons.collections.map.SingletonMap;
import org.w3c.dom.ls.LSOutput;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuctionItemsList {
    private Map<Long,AuctionItem> auctionItemsList;
    private AtomicLong itemId = new AtomicLong(0);

    public AuctionItemsList(Stream<AuctionItem> auctionItems) {
        auctionItemsList = auctionItems
                .collect(
                    Collectors.toMap(
                            (x) -> itemId.getAndIncrement(),
                            (x) -> x
                    ));
    }

    public Map<Long, AuctionItem> getAuctionItemsList() {
        return auctionItemsList;
    }
}
