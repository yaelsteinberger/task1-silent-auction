package entity.channels;

import java.io.IOException;
import java.net.Socket;

public interface WriteChannel extends Runnable {

    public void write(Socket socket) throws IOException;

    public void handleWrite(String dataGram) throws IOException;
}
