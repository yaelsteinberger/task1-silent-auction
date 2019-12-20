package MOCKs;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.command.Command;
import util.PrintHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class MockSocket extends Socket {

    private Command outPutStreamCommand;

    // a mockSocket constructor does not need hostname and port number
    public MockSocket() {}

    // return an InputStream with a dummy request
    public InputStream getInputStream() {
        return new ByteArrayInputStream("GET / HTTP/1.1\nHost: localhost".getBytes());
    }

    // coming up next! :)
    public OutputStream getOutputStream() {
        ObjectMapper mapper = new ObjectMapper();

        return new OutputStream() {

            @Override
            public void write(byte b[], int off, int len) throws IOException {
                outPutStreamCommand = mapper.readValue(b,Command.class);
//                PrintHelper.printPrettyInRed(outPutStreamCommand);
            }

            @Override
            public void write(int b) throws IOException {}


        };
    }

    public Command getOutPutStreamCommand() {
        return outPutStreamCommand;
    }
}
