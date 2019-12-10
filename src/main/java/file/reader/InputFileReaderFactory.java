package file.reader;
import file.CsvInputFileReader;
import file.JsonInputFileReader;
import org.apache.commons.io.FilenameUtils;


public class InputFileReaderFactory {

    public static InputFileReader of(String filePath){
        String fileType = FilenameUtils.getExtension(filePath);
        switch (fileType){
            case "csv": {
                return new CsvInputFileReader(filePath);
            }
            case "json": {
                return new JsonInputFileReader(filePath);
            }
            default:
                throw new IllegalStateException("ERROR: Reading from unsupported file type: " + fileType);
        }
    }
}
