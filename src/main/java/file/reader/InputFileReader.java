package file.reader;


import java.io.IOException;
import java.util.stream.Stream;

public interface InputFileReader {

    Stream readFile() throws IOException;

}
