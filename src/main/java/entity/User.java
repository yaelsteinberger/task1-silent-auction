package entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.Socket;

public class User {
    private final String userName;
    private final String firstName;
    private final String lastName;
//    private final String phone;
//    private final String address;

    @JsonCreator
    public User(@JsonProperty("userName")String userName,
                @JsonProperty("firstName")String firstName,
                @JsonProperty("lastName")String lastName) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
}
