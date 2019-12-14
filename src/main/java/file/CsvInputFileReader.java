package file;


import auctionList.AuctionItem;
import com.opencsv.CSVParser;

import entity.auction.Item;
import file.reader.InputFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CsvInputFileReader implements InputFileReader {

    private final static Logger logger = LoggerFactory.getLogger(CsvInputFileReader.class);

    private final String filePath;

    public CsvInputFileReader(String filePath) {
      this.filePath = filePath;
    }

    public interface Headers {
        public final int NAME = 0;
        public final int DESCRIPTION = 1;
        public final int START_PRICE = 2;
        public final int BID_INCREMENT = 3;
    }

    @Override
    public Stream readFile() throws IOException {
        logger.debug("Read json file: Start");

        logger.debug("file={}",filePath);
        if(!Files.exists(Paths.get(filePath))){
            logger.error("File Not Found");
            throw new IllegalStateException("ERROR: File path \"" + filePath + "\" doesn't exist");
        }

        File file = new File(filePath);
        logger.info("Read csv file: Done!");

        Stream<String> lines = Files.lines(file.toPath());
        Stream<String[]> linesArr = parseCSVLines(lines);

        return convertLineToAuctionItem(linesArr);

    }

    private Stream parseCSVLines(Stream<String> lines){

        CSVParser csvParser = new CSVParser();
        logger.info("Parsing csv lines");

        return lines
                .peek(ii -> logger.debug("--> parseCSVLines(lines = {})",ii))
                .map(ii -> {
                    String[] lineValues = null;
                    try {
                        lineValues = csvParser.parseLine((String)ii);
                    } catch (IOException e) {
                        logger.error(e.getStackTrace().toString());
                    }
                    return lineValues;
                });
    }

    private Stream convertLineToAuctionItem(Stream<String[]> lines){
        return lines.map(item -> new Item(
                        item[Headers.NAME],
                        item[Headers.DESCRIPTION],
                        Long.valueOf(item[Headers.START_PRICE]),
                        Long.valueOf(item[Headers.BID_INCREMENT]))
                    );
    }
}


