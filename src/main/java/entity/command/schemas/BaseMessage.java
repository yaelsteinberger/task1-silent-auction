package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/* for interfaces and abstract needs to specify which subtypes
are to be considered as jason for using ObjectMapper */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmptyMessage.class, name = "EmptyMessage"),
        @JsonSubTypes.Type(value = LoginUserMessage.class, name = "LoginUserMessage"),
        @JsonSubTypes.Type(value = AddBidMessage.class, name = "AddBidMessage"),
        @JsonSubTypes.Type(value = MessageToClientMessage.class, name = "MessageToClientMessage"),
        @JsonSubTypes.Type(value = GetAuctionItemMessage.class, name = "GetAuctionItemMessage"),
})
public abstract class BaseMessage {}

