package client.channelHandler;

import client.entity.OpcodeCommandsQuestions;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;


public class ParseUserInputData {

  public static OpcodeCommandsQuestions parseOpcodeFromInputData(String rawInput) {
        final String input = rawInput.trim();
        OpcodeCommandsQuestions result = OpcodeCommandsQuestions.NONE;

        OpcodeCommandsQuestions[] expectedValidCommands = OpcodeCommandsQuestions.values();


        /* else continue parsing */
        OpcodeCommandsQuestions[] results = Arrays.stream(expectedValidCommands)
                .filter(expectedCmd -> input.equalsIgnoreCase(expectedCmd.command()))
                .toArray(OpcodeCommandsQuestions[]::new);

        if(results.length == 1){
            result = results[0];
        }

        return result;
    }

    public static boolean validateUserInputFromQuestion(String rawInput, Class expectedClassType) {
        ObjectMapper mapper = new ObjectMapper();
        boolean isInputValid = false;
        String input = rawInput.trim();

        try{
            /* check the input is as expected type */
            mapper.convertValue(input, expectedClassType);
            isInputValid = true;
        }
        catch(IllegalArgumentException e){}

        return isInputValid;
    }
}
