package entity.auction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {
    private final String name;
    private final String description;
    private final Long startPrice;
    private final Long bidIncrement;

    @JsonCreator
    public Item(
            @JsonProperty("name")String name,
            @JsonProperty("description")String description,
            @JsonProperty("startPrice")Long startPrice,
            @JsonProperty("bidIncrement")Long bidIncrement) {
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.bidIncrement = bidIncrement;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getStartPrice() {
        return startPrice;
    }

    public Long getBidIncrement() {
        return bidIncrement;
    }


}
