package client.channelHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.StatusCode;
import entity.channels.WriteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;


public class ClientWriteChannel implements WriteChannel {

    private final static Logger logger = LoggerFactory.getLogger(ClientWriteChannel.class);

    private final Socket socket;
    private final ChannelServices channelServices;
    private ObjectMapper mapper;

    public ClientWriteChannel(Socket socket) {
        this.socket = socket;
        this.channelServices = new ChannelServices(socket);
    }

    @Override
    public void write() throws IOException {

    }

    @Override
    public void handleWrite(String dataGram) throws IOException {

    }

    @Override
    public void run() {
        boolean isRun = true;
        int statusCode;

        while(isRun){
            try {
                /* handle client input */
                statusCode = this.channelServices.handleUserRequest();
                isRun = (statusCode != StatusCode.FATAL_ERROR);

            } catch (InterruptedException | IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            } finally {

            }
        }
        logger.debug("EXIT");
    }
}
