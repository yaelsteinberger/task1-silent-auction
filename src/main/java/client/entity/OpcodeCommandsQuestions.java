package client.entity;

import entity.command.Opcodes;

import java.util.ArrayList;
import java.util.List;

public enum OpcodeCommandsQuestions {
    NONE(Opcodes.NONE,"", null),
    EXIT(Opcodes.EXIT,"exit",null),
    GET_AUCTION_LIST(Opcodes.GET_AUCTION_LIST,"list", null),
    LOGIN(Opcodes.LOGIN_CLIENT,"login",
            new ArrayList<Question>(){{
                add(new Question("Please enter your user name:", "userName", String.class));
            }}),
    GET_AUCTION_ITEM(Opcodes.GET_AUCTION_ITEM,"item",
            new ArrayList<Question>(){{
                add(new Question("Please enter item id:", "itemId", Long.class));
            }}),
    REGISTER(Opcodes.REGISTER_CLIENT,"reg",
            new ArrayList<Question>(){{
                add(new Question("Please enter first name:", "firstName", String.class));
                add(new Question("Please enter last name:", "lastName", String.class));
                add(new Question("Please enter user name:", "userName", String.class));
            }}),
    ADD_BID(Opcodes.ADD_BID,"bid",
                new ArrayList<Question>(){{
                    add(new Question("Please enter item id:", "auctionItemId", Long.class));
                    add(new Question("Please enter bid value to offer:", "bidValue", Long.class));
                }});

        private Integer opcode;
        private String command;
        private List<Question> questions;

    OpcodeCommandsQuestions(
            Integer opcode,
            String command,
            List<Question> questions) {
        this.opcode = opcode;
        this.command = command;
        this.questions = questions;

    }

        public Integer opcode() {
            return opcode;
        }
        public String command() {
            return command;
        }
        public List<Question> questions() {
            return questions;
        }





}
