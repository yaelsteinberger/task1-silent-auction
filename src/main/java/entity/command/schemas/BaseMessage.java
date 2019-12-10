package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/* for interfaces and abstract needs to specify which subtypes
are to be considered as jason for using ObjectMapper */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmptyMessage.class, name = "EmptyMessage"),
        @JsonSubTypes.Type(value = UsersListMessage.class, name = "UsersListMessage"),
        @JsonSubTypes.Type(value = LoginUserMessage.class, name = "LoginUserMessage"),
        @JsonSubTypes.Type(value = MessageToClientMessage.class, name = "ChatWithUserMessage"),
})
public abstract class BaseMessage {}

