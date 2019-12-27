package server;

import auctionList.AuctionItemsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.clientHandler.ServerReadChannel;
import usersList.AbstractUsersList;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class AuctionTimer extends TimerTask {
    private final static Logger logger = LoggerFactory.getLogger(AuctionTimer.class);
    private final Long startTimeSeconds;
    private final Long totalTimeSeconds;
    private AtomicLong timeLeftSeconds;


    public AuctionTimer(Long timeMinutes){
        System.out.printf("init");
        Long seconds = 5L;//timeMinutes*60;
        startTimeSeconds = (new Date()).getTime()/60;
        totalTimeSeconds = seconds;
        timeLeftSeconds = new AtomicLong(seconds);
    }

    @Override
    public void run() {
//        Long residue = 0L;
        Long timeLeft = timeLeftSeconds.get();
        Long newTimeLeft = timeLeft;
        System.out.println("Time Left: " + timeLeft);

        if(timeLeft > 0){
            Long currentTimeSecond =  (new Date()).getTime()/60;
            newTimeLeft = timeLeft - (currentTimeSecond-startTimeSeconds);

        }else{
            System.out.println("DONE! Time Left: " + timeLeft);
            this.cancel();
        }

        timeLeftSeconds.set(newTimeLeft);


    }
}
