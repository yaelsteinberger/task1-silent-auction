package client;

import client.channelHandler.ParseUserInputData;
import client.entity.OpcodeCommandsQuestions;
import client.entity.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import entity.command.Opcodes;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseUserInputDataTest {

    @BeforeClass
    public static void setup() throws IOException, InterruptedException {

    }

    @Test
    public void parseOpcodeFromInputDataTest() throws JsonProcessingException {
        Map<String,OpcodeCommandsQuestions> expectations = generateOpcodeExpectations();

        expectations.forEach((inputData,expect) -> {
            OpcodeCommandsQuestions result = ParseUserInputData.parseOpcodeFromInputData(inputData);

            assertThat(result.opcode(),is(expect.opcode()));
        });
    }

    @Test
    public void validateUserInputFromQuestionTest(){
        Map<String,Map> expectations = generateQuestionExpectations();

        expectations.forEach((inputData,expect) -> {
            Boolean result =
                    ParseUserInputData.validateUserInputFromQuestion(inputData,(Class)expect.get("classType"));

            assertThat(result,is(expect.get("result")));
        });

    }

    private Map<String,OpcodeCommandsQuestions> generateOpcodeExpectations(){

        return new HashMap<String,OpcodeCommandsQuestions>(){{
            put(" exit ", OpcodeCommandsQuestions.EXIT);
            put(" exit list ",OpcodeCommandsQuestions.NONE);
            put(" login ",OpcodeCommandsQuestions.LOGIN);
            put(" logina ",OpcodeCommandsQuestions.NONE);
            put(" reg ",OpcodeCommandsQuestions.REGISTER);
            put(" register ",OpcodeCommandsQuestions.NONE);
            put(" list ",OpcodeCommandsQuestions.GET_AUCTION_LIST);
            put(" list: ",OpcodeCommandsQuestions.NONE);
            put(" item ",OpcodeCommandsQuestions.GET_AUCTION_ITEM);
            put(" item: ",OpcodeCommandsQuestions.NONE);
            put(" bid ",OpcodeCommandsQuestions.ADD_BID);
            put(" bid itemId 6 value 7",OpcodeCommandsQuestions.NONE);
        }};
    }

    private Map<String,Map> generateQuestionExpectations(){

        return new HashMap<String,Map>(){{
            put("666", new HashMap(){{
                put("classType",Integer.class);
                put("result",true);
            }});
            put("666g", new HashMap(){{
                put("classType",Integer.class);
                put("result",false);
            }});
            put("66f6g", new HashMap(){{
                put("classType",String.class);
                put("result",true);
            }});

        }};
    }
}
