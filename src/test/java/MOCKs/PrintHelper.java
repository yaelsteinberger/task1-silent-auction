package MOCKs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrintHelper{
    private static ObjectMapper mapper = new ObjectMapper();

    public static void printPrettyInRed(Object obj) throws JsonProcessingException {
        System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
    }
}
