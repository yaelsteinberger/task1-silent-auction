package server.entity;

import entity.command.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public enum OpcodeCommandsQuestions {
    NONE(Opcodes.NONE,"", null),
    EXIT(Opcodes.EXIT,"exit",null),
    START_AUCTION(Opcodes.START_AUCTION,"start",
            new ArrayList<Question>(){{
                add(new Question("Please enter auction time unit ('minute','hour'):", "timeUnit", Long.class));
                add(new Question("Please enter auction time (according the previous selected time units):", "totalTime", Long.class));
                add(new Question("Please enter 'run' to start the auction", "isRun", String.class));
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
