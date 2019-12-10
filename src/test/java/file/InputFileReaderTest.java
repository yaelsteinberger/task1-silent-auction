package file;

import auctionList.AuctionItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import file.reader.InputFileReader;
import file.reader.InputFileReaderFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

public class InputFileReaderTest {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void CSVInputFileReaderTest(){
        //Given
        String filePath = "bobo.csv";

        //When
        InputFileReader fileReader = InputFileReaderFactory.of(filePath);
        CsvInputFileReader fileReaderCSV = ((CsvInputFileReader)fileReader);

        //Then
        assertThat(fileReaderCSV, isA(CsvInputFileReader.class));
    }


    @Test
    public void JsonInputFileReaderTest(){
        //Given
        String filePath = "bobo.json";

        //When
        InputFileReader fileReader = InputFileReaderFactory.of(filePath);
        JsonInputFileReader fileReaderJSON = ((JsonInputFileReader)fileReader);

        //Then
        assertThat(fileReaderJSON, isA(JsonInputFileReader.class ));
    }

    @Test
    /* TODO: MOCK JSON FILE */
    public void readingJsonFileTest() throws IOException {
        //Given
        String filePath = "auctionItems.json";

        //When
        InputFileReader fileReader = InputFileReaderFactory.of(filePath);
        JsonInputFileReader fileReaderJson = ((JsonInputFileReader)fileReader);

        Stream output = fileReaderJson.readFile();
        Object[] auctionItems = output.toArray();


        System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(auctionItems));

        //Then
        assertThat(fileReaderJson, isA(JsonInputFileReader.class ));
    }

    @Test
    /* TODO: MOCK CSV FILE */
    public void readingCsvFileTest() throws IOException {
        //Given
        String filePath = "auctionItems.csv";

        //When
        InputFileReader fileReader = InputFileReaderFactory.of(filePath);
        CsvInputFileReader fileReaderCsv = ((CsvInputFileReader)fileReader);

        Stream output = fileReaderCsv.readFile();
        Object[] auctionItems = output.toArray();


        System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(auctionItems));

        //Then
        assertThat(fileReaderCsv, isA(CsvInputFileReader.class ));
    }
}


