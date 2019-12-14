package entity.channels;

import entity.command.Command;

import java.io.IOException;

public interface ReadChannel extends Runnable {

    public Command read() throws IOException;

    public int handleRead(Command command) throws IOException;

}
