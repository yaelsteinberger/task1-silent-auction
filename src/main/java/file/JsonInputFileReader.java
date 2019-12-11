package file;

import auctionList.AuctionItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
//import entity.FullName;
import entity.auction.Item;
import file.reader.InputFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class JsonInputFileReader implements InputFileReader {

    private final static Logger logger = LoggerFactory.getLogger(JsonInputFileReader.class);

    private final String filePath;

    public JsonInputFileReader(String filePath) {
      this.filePath = filePath;
    }

    @Override
    public Stream readFile() throws IOException {
        logger.debug("Read json file: Start");

        logger.debug("file={}",filePath);
        if(!Files.exists(Paths.get(filePath))) {
            logger.error("File Not Found");
            throw new IllegalStateException("ERROR: File path \"" + filePath + "\" doesn't exist");
        }

        ObjectMapper objMapper = new ObjectMapper();
        InputStream input = new FileInputStream(filePath);
        logger.info("Read json file: Done!");

        // Deserialization for array json:
        TypeReference reference = new TypeReference<List<Item>>(){};

        List<Item> auctionItems = (List<Item>)objMapper.readValue(input,reference);
        logger.info("Parsing json lines");

        return auctionItems.stream();

    }
}


