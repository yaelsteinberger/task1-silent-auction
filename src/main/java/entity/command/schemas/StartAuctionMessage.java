package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.TimeUnit;


public class StartAuctionMessage extends BaseMessage {
    private final Long totalTime;
    private final TimeUnit timeUnit;

    /* To be able to use ObjectMapper to read the Command and convert it as this type
    must clarify which is the class's constructor and it's members for the conversion */
    @JsonCreator
    public StartAuctionMessage(
            @JsonProperty("totalTime") Long totalTime,
            @JsonProperty("timeUnit") TimeUnit timeUnit) {
        this.totalTime = totalTime;
        this.timeUnit = timeUnit;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
