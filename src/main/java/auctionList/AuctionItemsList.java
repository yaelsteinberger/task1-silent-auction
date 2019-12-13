package auctionList;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.auction.Item;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuctionItemsList {
    private final Map<Long, AuctionItem> auctionItemsList;

    public AuctionItemsList(Stream<AuctionItem> auctionItems) {
        AtomicLong itemId = new AtomicLong(0);
        auctionItemsList = auctionItems
                .collect(
                    Collectors.toMap(
                            (x) -> itemId.getAndIncrement(),
                            (x) -> x
                    ));
    }

    public AuctionItem findById(Long id){
        return auctionItemsList.get(id);
    }

    public Map<Long, AuctionItem> getAuctionItemsList() {
        return auctionItemsList;
    }

    public String itemsListToPrettyString(){
        Map<Long,Item> items = auctionItemsList
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                            (x) -> x.getKey(),
                            (x) -> x.getValue().getItemData()
                    ));

        Integer nameMaxLen = items.values().stream()
                .map(item -> item.getName().length())
                .mapToInt(val -> val)
                .max()
                .orElseThrow(NoSuchElementException::new);

        Integer descriptionMaxLen = items.values().stream()
                .map(item -> item.getDescription().length())
                .mapToInt(val -> val)
                .max()
                .orElseThrow(NoSuchElementException::new);

        Integer idMaxLen = 3;
        Integer startPriceMaxLen = "Start Price".length();
        Integer incrementMaxLen = "Increment".length();

        int headerIdPadding = ((idMaxLen+1) - "Id".toString().length());
        int headerNamePadding = ((nameMaxLen+1) - "Name".length());
        int headerDescriptionPadding = ((descriptionMaxLen+1) - "Description".length());
        int headerStartPricePadding = ((startPriceMaxLen+1) - "Start Price".length());
        int headerIncrementPadding = ((incrementMaxLen+1) - "Increment".length());


        StringBuilder retVal = new StringBuilder(String.format(
                        "%s%6$" + headerIdPadding + "s |" +
                        " %s%6$" + headerNamePadding + "s |" +
                        " %s%6$" + headerDescriptionPadding + "s |" +
                        " %s%6$" + headerStartPricePadding + "s |" +
                        " %s%6$" + headerIncrementPadding + "s\n",
                "Id",
                "Name",
                "Description",
                "Start Price",
                "Increment",
                "")); /* 6 -> or padding after name */

        int size = retVal.length();

        for(int i = 0; i < size; i++){
            retVal.append("-");
        }
        retVal.append("\n");

        ObjectMapper mapper = new ObjectMapper();
        Object[] results = items.entrySet()
                .stream()
                .map(item -> {
                    Long id = item.getKey();
                    String name = item.getValue().getName();
                    String description = item.getValue().getDescription();
                    Long startPrice = item.getValue().getStartPrice();
                    Long increment = item.getValue().getBidIncrement();

                    int idPadding = ((idMaxLen+1) - id.toString().length());
                    int namePadding = ((nameMaxLen+1) - name.length());
                    int descriptionPadding = ((descriptionMaxLen+1) - description.length());
                    int startPricePadding = ((startPriceMaxLen+1) - startPrice.toString().length());
                    int incrementPadding = ((incrementMaxLen+1) - increment.toString().length());

                    String itemStr = String.format(
                            "%d%6$" + idPadding + "s |" +
                            " %s%6$" + namePadding + "s |" +
                            " %s%6$" + descriptionPadding + "s |" +
                            " %d%6$" + startPricePadding + "s |" +
                            " %d%6$" + incrementPadding + "s\n",
                            id, name, description, startPrice, increment, "");

                    return itemStr;
                })
                .toArray();


        for (Object line : results) {
            retVal.append(line);
        }

        return retVal.toString();
    }
}
