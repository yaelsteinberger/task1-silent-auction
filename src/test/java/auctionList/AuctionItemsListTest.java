package auctionList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import file.JsonInputFileReader;
import file.reader.InputFileReader;
import file.reader.InputFileReaderFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

public class AuctionItemsListTest {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    /* TODO: MOCK JSON INPUT INSTEAD OF READING FILE */
    public void getAuctionItemsListTest() throws IOException {
        //Given
        String filePath = "auctionItems.json";

        //When
        InputFileReader fileReader = InputFileReaderFactory.of(filePath);
        JsonInputFileReader fileReaderJson = ((JsonInputFileReader)fileReader);

        Stream fileStream = fileReaderJson.readFile();

        AuctionItemsList auctionItemsList = new AuctionItemsList(fileStream);

        System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(auctionItemsList));

    }

}
