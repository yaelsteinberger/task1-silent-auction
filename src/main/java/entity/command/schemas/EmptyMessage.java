package entity.command.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/* To prevent the below exception, adding "@JsonSerialize" to the class.
Exception:
"No serializer found for class components.command.schemas.EmptyMessage
and no properties discovered to create BeanSerializer"
*/
@JsonSerialize
public class EmptyMessage extends BaseMessage {

    /* to be able to use ObjectMapper to read the Command and convert it as this type
    must clarify which is the class's constructor and it's members for the conversion */
    @JsonCreator
    public EmptyMessage() {}
}
