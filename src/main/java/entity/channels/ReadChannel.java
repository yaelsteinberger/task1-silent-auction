package entity.channels;

import java.io.IOException;

public interface ReadChannel extends Runnable {

    public void read() throws IOException;

    public int handleRead() throws IOException;

}
